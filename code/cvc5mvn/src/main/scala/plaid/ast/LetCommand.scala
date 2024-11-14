package plaid.ast
import java.lang

case class LetCommand(getY: Identifier, getE: PreludeExpression, getC: PreludeCommand) extends PreludeCommand {
  override def children(): lang.Iterable[Node] = new java.util.ArrayList[Node]

  override def prettyPrint(): String =
    throw new UnsupportedOperationException()
}
