package plaid.constraints.ast

case class AndConstraintsExpr(e1: ConstraintsExpr, e2: ConstraintsExpr) extends ConstraintsExpr{
  override def prettyPrint(): String = e1.prettyPrint() + "AND" + e2.prettyPrint()
}