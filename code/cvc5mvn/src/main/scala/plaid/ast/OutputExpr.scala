package plaid.ast
import java.lang

case class OutputExpr() extends MemoryExpr{
  override def children(): lang.Iterable[Node] = new java.util.ArrayList[Node]()

  override def prettyPrint(): String = "out"

}
