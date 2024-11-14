package plaid.ast

import java.util
import java.lang

case class FieldSelectExpr(getE: PreludeExpression, getL: Identifier) extends PreludeExpression{
  override def children(): lang.Iterable[Node] = new util.ArrayList[Node]()

  override def prettyPrint(): String = throw new UnsupportedOperationException();
}
