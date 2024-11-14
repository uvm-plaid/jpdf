package plaid.ast
import java.lang

// TODO: do we have to use immutable Map for case class?
case class FieldExpr(getElements: java.util.Map[Identifier, PreludeExpression]) extends PreludeExpression{
  override def children(): lang.Iterable[Node] = new java.util.ArrayList[Node]()

  override def prettyPrint() : String = throw new UnsupportedOperationException()
}
