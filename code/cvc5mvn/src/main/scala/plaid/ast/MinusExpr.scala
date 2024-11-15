package plaid.ast

case class MinusExpr(getE: PreludeExpression) extends PreludeExpression{
  override def prettyPrint(): String = "(" + "-" + getE.prettyPrint() + ")" 
}
