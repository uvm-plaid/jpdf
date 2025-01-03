package plaid.ast

case class MinusExpr(e: PreludeExpression) extends PreludeExpression{
  override def prettyPrint(): String = "(" + "-" + e.prettyPrint() + ")" 
}
