package plaid.ast

trait Constraint extends Node
case class AndConstraint(e1: Constraint, e2: Constraint) extends Constraint
case class EqualConstraint(e1: Expr, e2: Expr) extends Constraint
case class NotConstraint(e: Constraint) extends Constraint
case class TrueConstraint() extends Constraint
