package plaid.ast

case class AndConstraintExpr(e1: ConstraintExpr, e2: ConstraintExpr) extends ConstraintExpr