package plaid.ast
import java.lang

case class SecretExpr(getE: PreludeExpression) extends MemoryExpr{
  override def children(): lang.Iterable[Node] = new java.util.ArrayList[Node]()

  override def prettyPrint(): String = "s[\"" + getE.toString + "\"]"
}