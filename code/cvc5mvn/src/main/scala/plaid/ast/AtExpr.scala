package plaid.ast
import java.{lang, util}

case class AtExpr(getE1: PreludeExpression, getE2: PreludeExpression) extends PreludeExpression{
  override def children(): lang.Iterable[Node] = new util.ArrayList[Node]()

  override def prettyPrint(): String = getE1.prettyPrint() + "@" + getE2.prettyPrint()
}
