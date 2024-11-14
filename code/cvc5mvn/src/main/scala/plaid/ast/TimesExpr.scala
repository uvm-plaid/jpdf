package plaid.ast
import java.lang


case class TimesExpr(getE1: PreludeExpression, getE2: PreludeExpression) extends PreludeExpression{
  override def children() : lang.Iterable[Node] = new java.util.ArrayList[Node]()
  
  override def prettyPrint(): String = "(" + getE1.prettyPrint() + "*" + getE2.prettyPrint() + ")"
}
