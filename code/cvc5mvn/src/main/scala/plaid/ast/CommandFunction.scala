package plaid.ast
import java.lang

case class CommandFunction(getFname: Identifier, getY: java.util.List[Identifier], getC: PreludeCommand) extends PreludeFunction {
  override def children(): lang.Iterable[Node] = new java.util.ArrayList[Node]

  override def prettyPrint(): String = throw new UnsupportedOperationException()
}
