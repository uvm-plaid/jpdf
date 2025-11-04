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
   * Expand all the expressions contained inside this constraint. The ctx is
   * for looking up expression functions, and bindings are for any identifiers
   * that should be replaced by expressions.
   */
  def expandExpr(ctx: List[ExprFunc], bindings: Map[Identifier, Expr]): Constraint = transform {
    case CallConstraint(id, parms) => Some(CallConstraint(id, parms.map(_.expand(ctx, bindings))))
    case EqualConstraint(e1, e2) => Some(EqualConstraint(e1.expand(ctx, bindings), e2.expand(ctx, bindings)))
    case _ => None
  }

  /**
   * Recursively inline any constraint function calls contained inside this
   * constraint. All expressions in all constraints should be expanded before
   * this is used, but it would probably work anyway.
   */
  def expand(ctx: List[ConstraintFunc], bindings: Map[Identifier, Expr]): Constraint = transform {
    case CallConstraint(id, parms) =>
      val f = ctx.resolve(id)
      val formalParms = f.parms
      val actualParms = parms.map(_.expand(bindings = bindings))
      Some(f.body.expand(ctx, Map.from(formalParms.zip(actualParms))))
    case _ => None
  }
}

case class AndConstraint(e1: Constraint, e2: Constraint) extends Constraint
case class CallConstraint(fn: Identifier, parms: List[Expr]) extends Constraint
case class EqualConstraint(e1: Expr, e2: Expr) extends Constraint
case class NotConstraint(e: Constraint) extends Constraint
case class TrueConstraint() extends Constraint
