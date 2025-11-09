package plaid.prelude.ast

sealed trait Constraint extends Node {

  /**
   * Apply a transformation f to this constraint. If f does not return a
   * constraint, transform applies f to the children of this expression,
   * recursively. If there are no children, the constraint is returned as-is.
   */
  def transform(f: Constraint => Option[Constraint]): Constraint = f(this).getOrElse(this match
    case AndConstraint(e1, e2) => AndConstraint(e1.transform(f), e2.transform(f))
    case NotConstraint(e) => NotConstraint(e.transform(f))
    case other => f(other).getOrElse(other))

  /**
   * Recursively inline any constraint function calls contained inside this
   * constraint. All expressions in all constraints should be expanded before
   * this is used, but it would probably work anyway.
   */
  def expand(
      exprCtx: List[ExprFunc] = Nil,
      constraintCtx: List[ConstraintFunc] = Nil,
      bindings: Map[Identifier, Expr] = Map()): Constraint = transform {
    case EqualConstraint(e1, e2) => Some(EqualConstraint(e1.expand(exprCtx, bindings), e2.expand(exprCtx, bindings)))
    case CallConstraint(id, parms) =>
      val f = constraintCtx.resolve(id)
      val formalParms = f.parms
      val actualParms = parms.map(_.expand(exprCtx, bindings))
      Some(f.body.expand(exprCtx, constraintCtx, Map.from(formalParms.zip(actualParms))))
    case _ => None
  }
}

case class AndConstraint(e1: Constraint, e2: Constraint) extends Constraint
case class CallConstraint(fn: Identifier, parms: List[Expr]) extends Constraint
case class EqualConstraint(e1: Expr, e2: Expr) extends Constraint
case class NotConstraint(e: Constraint) extends Constraint
case class TrueConstraint() extends Constraint
