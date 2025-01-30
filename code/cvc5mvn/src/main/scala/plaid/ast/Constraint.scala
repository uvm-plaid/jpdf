package plaid.ast

@Deprecated
case class Constraint(precondition : ConstraintExpr, postcondition: ConstraintExpr) extends Node