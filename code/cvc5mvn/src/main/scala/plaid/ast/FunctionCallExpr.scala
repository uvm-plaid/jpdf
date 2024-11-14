package plaid.ast
import java.{lang, util}

case class FunctionCallExpr(getFname: Identifier, getParameters: java.util.List[PreludeExpression]) extends PreludeExpression{
  override def children(): lang.Iterable[Node] = new util.ArrayList[Node]()

  override def prettyPrint(): String = throw new UnsupportedOperationException()
}
