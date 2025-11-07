package plaid.prelude.ast

trait Func extends Node {
  def id: Identifier

  /**
   * Collect the identifiers for the direct expression function dependencies of
   * this function.
   */
  def exprDependencies(): Set[Identifier] =
    descendants().collect { case CallExpr(id, _) => id }.toSet

  /**
   * Collect the identifiers for the direct constraint function dependencies of
   * this function.
   */
  def constraintDependencies(): Set[Identifier] =
    descendants().collect { case CallConstraint(id, _) => id }.toSet

  /**
   * Collect the identifiers for the direct command function dependencies of
   * this function.
   */
  def cmdDependencies(): Set[Identifier] =
    descendants().collect { case CallCmd(id, _) => id }.toSet
}

extension [F <: Func](trg: List[F])
  /** Look up a function by its identifier. */
  def resolve(id: Identifier): F = trg.find(x => id == x.id).get
  /** Sort in such a way that there are no forward dependencies */
  def dependencyOrderedUsing(f: F => Set[Identifier]): List[F] =
    if trg.isEmpty then Nil
    else
      val ids = trg.map(_.id).toSet
      val first = trg.filter(x => f(x).forall(y => !ids.contains(y)))
      if first.isEmpty then throw Exception("Cyclic function calls detected")
      first ++ trg.filterNot(first.contains).dependencyOrderedUsing(f)

object ListExprFuncExt {
  extension (trg: List[ExprFunc])
    /** Sort in such a way that there are no forward dependencies */
    def dependencyOrdered(): List[ExprFunc] = trg.dependencyOrderedUsing(_.exprDependencies())
    /** Expand all the expression functions in this list. */
    def expandAll(): List[ExprFunc] = trg
      .dependencyOrdered()
      .foldLeft(List()) { (acc, f) => f.expand(acc) :: acc }
}

object ListConstraintFuncExt {
  extension (trg: List[ConstraintFunc])
    /** Sort in such a way that there are no forward dependencies */
    def dependencyOrdered(): List[ConstraintFunc] = trg.dependencyOrderedUsing(_.constraintDependencies())
    /** Expand all the constraint functions in this list. */
    def expandAll(exprCtx: List[ExprFunc]): List[ConstraintFunc] = trg
      .dependencyOrdered()
      .foldLeft(List()) { (acc, f) => f.expand(exprCtx, acc) :: acc }
}

object ListCmdFuncExt {
  extension (trg: List[CmdFunc])
    /** Sort in such a way that there are no forward dependencies */
    def dependencyOrdered(): List[CmdFunc] = trg.dependencyOrderedUsing(_.cmdDependencies())
    /** Expand all the constraint functions in this list. */
}

case class ExprFunc(id: Identifier, parms: List[Identifier], body: Expr) extends Func {
  /** Expand expression function calls in this expression function. */
  def expand(ctx: List[ExprFunc]): ExprFunc =
    copy(body = body.expand(ctx))
}

case class ConstraintFunc(id: Identifier, parms: List[Identifier], body: Constraint) extends Func {
  /** Expand expression and constraint function calls in this constraint function. */
  def expand(exprCtx: List[ExprFunc], constraintCtx: List[ConstraintFunc]): ConstraintFunc =
    copy(body = body.expand(exprCtx, constraintCtx))
}

case class CmdFunc(id: Identifier, typedVariables: List[TypedIdentifier], body: List[Cmd], precond: Option[Constraint], postcond: Option[Constraint]) extends Func