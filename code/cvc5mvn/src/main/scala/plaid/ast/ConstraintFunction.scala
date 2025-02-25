package plaid.ast

case class ConstraintFunction(id: Identifier, params: java.util.List[Identifier], body: ConstraintExpr) extends Node
