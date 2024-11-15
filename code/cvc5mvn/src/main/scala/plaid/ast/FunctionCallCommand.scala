package plaid.ast

case class FunctionCallCommand(getFname: Identifier, getParameters: java.util.List[PreludeExpression]) extends PreludeCommand{
  override def prettyPrint(): String = throw new UnsupportedOperationException()
}

