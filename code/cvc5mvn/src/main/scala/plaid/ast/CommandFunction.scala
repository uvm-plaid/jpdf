package plaid.ast

case class CommandFunction(getFname: Identifier, getY: java.util.List[Identifier], getC: PreludeCommand) extends PreludeFunction {
  override def prettyPrint(): String = throw new UnsupportedOperationException()
}
