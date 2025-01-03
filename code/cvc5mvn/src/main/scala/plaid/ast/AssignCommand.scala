package plaid.ast

case class AssignCommand(e1: PreludeExpression, e2: PreludeExpression) extends PreludeCommand {
  override def prettyPrint(): String = e1.prettyPrint() + " := " + e2.prettyPrint() + "\n"
}
