package plaid.ast

case class Identifier(name: String) extends PreludeExpression, ConstraintExpr
