package plaid.ast
import java.lang


case class ConcatExpr(getE1: PreludeExpression, getE2: PreludeExpression) extends PreludeExpression{
  override def children() : lang.Iterable[Node] = new java.util.ArrayList[Node]()

  override def prettyPrint(): String = throw new UnsupportedOperationException()
}
