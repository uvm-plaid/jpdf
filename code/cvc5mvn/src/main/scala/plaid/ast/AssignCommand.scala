package plaid.ast

case class AssignCommand(getE1: PreludeExpression, getE2: PreludeExpression) extends PreludeCommand {
  override def prettyPrint(): String = getE1.prettyPrint() + ":=" + getE2.prettyPrint() + "\n"
}
