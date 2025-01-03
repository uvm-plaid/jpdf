package plaid.ast

case class FunctionCallCommand(fname: Identifier, parameters: java.util.List[PreludeExpression]) extends PreludeCommand{
  override def prettyPrint(): String = throw new UnsupportedOperationException()
}

