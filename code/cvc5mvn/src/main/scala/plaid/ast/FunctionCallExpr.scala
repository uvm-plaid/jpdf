package plaid.ast

case class FunctionCallExpr(getFname: Identifier, getParameters: java.util.List[PreludeExpression]) extends PreludeExpression{
  override def prettyPrint(): String = throw new UnsupportedOperationException()
}
