package plaid.ast
import java.lang

case class MessageExpr(getE: PreludeExpression) extends MemoryExpr{
  override def children(): lang.Iterable[Node] = new java.util.ArrayList[Node]()

  override def prettyPrint(): String = "m[\"" + getE.toString + "\"]"
}
