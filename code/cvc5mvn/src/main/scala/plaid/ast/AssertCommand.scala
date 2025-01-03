package plaid.ast

case class AssertCommand(e1: PreludeExpression, e2: PreludeExpression, e3: PreludeExpression) extends PreludeCommand{
  override def prettyPrint(): String
    = "assert" + "(" + e1.prettyPrint() + "=" + e2.prettyPrint() + ")" + "@" + e3.prettyPrint() + "\n"
}