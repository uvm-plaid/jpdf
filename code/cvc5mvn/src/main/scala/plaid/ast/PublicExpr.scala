package plaid.ast
import java.lang

case class PublicExpr(getE: PreludeExpression) extends MemoryExpr{
  override def children(): lang.Iterable[Node] = new java.util.ArrayList[Node]()

  override def prettyPrint(): String = "p[\"" + getE.toString + "\"]"
}
