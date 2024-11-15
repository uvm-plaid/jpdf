package plaid.ast

case class Identifier(getName: String) extends PreludeExpression{
  override def prettyPrint(): String = getName
}
