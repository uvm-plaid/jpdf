package plaid.ast

case class AtExpr(e1: PreludeExpression, e2: PreludeExpression) extends PreludeExpression{
  override def prettyPrint(): String = e1.prettyPrint() + "@" + e2.prettyPrint()
}
