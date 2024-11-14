package plaid.ast
import java.lang

case class AssertCommand(getE1: PreludeExpression, getE2: PreludeExpression, getE3: PreludeExpression) extends PreludeCommand{
  override def children(): lang.Iterable[Node] = new java.util.ArrayList[Node]

  override def prettyPrint(): String
    = "assert" + "(" + getE1.prettyPrint() + "=" + getE2.prettyPrint() + ")" + "@" + getE3.prettyPrint() + "\n"
}