package plaid.ast
import java.{lang, util}

case class LetExpr(getY: Identifier, getE1: PreludeExpression, getE2: PreludeExpression) extends PreludeExpression{
  override def children(): lang.Iterable[Node] = new util.ArrayList[Node]()

  override def prettyPrint(): String = throw new UnsupportedOperationException()
}
