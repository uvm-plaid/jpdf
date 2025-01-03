package plaid.ast

case class Identifier(name: String) extends PreludeExpression{
  override def prettyPrint(): String = name
}
