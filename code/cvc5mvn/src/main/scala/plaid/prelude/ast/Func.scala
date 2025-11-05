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
  def dependencyOrdered(f: F => Set[Identifier]): List[F] =
    if trg.isEmpty then Nil
    else
      val ids = trg.map(_.id).toSet
      val first = trg.filter(x => f(x).forall(ids.contains))
      if first.isEmpty then throw Exception("Cyclic function calls detected")
      first ++ trg.filterNot(first.contains).dependencyOrdered(f)

extension (trg: List[ExprFunc])
  /** Expand all the expression functions in this list. */
  def expandAll(): List[ExprFunc] = trg
    .dependencyOrdered { _.exprDependencies() }
    .foldLeft(List()) { (acc, f) => f.expand(acc) :: acc }

extension (trg: List[ConstraintFunc])
  /** Expand all the constraint functions in this list. */
  def expandAll(exprCtx: List[ExprFunc]): List[ConstraintFunc] = trg
    .dependencyOrdered { _.constraintDependencies() }
    .foldLeft(List()) { (acc, f) => f.expand(exprCtx, acc) :: acc }

extension (trg: List[CmdFunc])
  /** Expand all the constraint functions in this list. */
  def expandAll(exprCtx: List[ExprFunc], constraintCtx: List[ConstraintFunc]): List[CmdFunc] = trg
    .map { _.expand(exprCtx, constraintCtx) }

case class ExprFunc(id: Identifier, parms: List[Identifier], body: Expr) extends Func {
  /** Expand expression function calls in this expression function. */
  def expand(ctx: List[ExprFunc]): ExprFunc =
    copy(body = body.expand(ctx))
}

case class ConstraintFunc(id: Identifier, parms: List[Identifier], body: Constraint) extends Func {
  /** Expand expression and constraint function calls in this constraint function. */
  def expand(exprCtx: List[ExprFunc], constraintCtx: List[ConstraintFunc]): ConstraintFunc =
    copy(body = body.expandExpr(exprCtx).expand(constraintCtx))
}

case class CmdFunc(id: Identifier, typedVariables: List[TypedIdentifier], body: List[Cmd], precond: Option[Constraint], postcond: Option[Constraint]) extends Func {
  /** Expand expression and constraint function calls in this constraint function. */
  def expand(exprCtx: List[ExprFunc], constraintCtx: List[ConstraintFunc]): CmdFunc = copy(
    body = body.map(_.expandExpr(exprCtx)),
    precond = precond.map(_.expand(constraintCtx)),
    postcond = postcond.map(_.expand(constraintCtx)))
}
