package plaid.ast

case class AtExpr(getE1: PreludeExpression, getE2: PreludeExpression) extends PreludeExpression{
  override def prettyPrint(): String = getE1.prettyPrint() + "@" + getE2.prettyPrint()
}
