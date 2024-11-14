package plaid.ast
import java.lang

case class AssignCommand(getE1: PreludeExpression, getE2: PreludeExpression) extends PreludeCommand {
  override def children(): lang.Iterable[Node] = new java.util.ArrayList[Node]

  override def prettyPrint(): String = getE1.prettyPrint() + ":=" + getE2.prettyPrint() + "\n"
}
