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
    val ids = trg.map(_.id).toSet
    val first = trg.filter(x => f(x).intersect(ids).isEmpty)
    first ++ trg.filterNot(first.contains).dependencyOrdered(f)

case class ExprFunc(id: Identifier, parms: List[Identifier], body: Expr) extends Func
case class ConstraintFunc(id: Identifier, parms: List[Identifier], body: Constraint) extends Func
case class CmdFunc(id: Identifier, typedVariables: List[TypedIdentifier], body: List[Cmd], precond: Option[Constraint], postcond: Option[Constraint]) extends Func
