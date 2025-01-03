package plaid.ast
import java.lang

// TODO: do we have to use immutable Map for case class?
case class FieldExpr(elements: java.util.Map[Identifier, PreludeExpression]) extends PreludeExpression{
  override def prettyPrint() : String = throw new UnsupportedOperationException()
}
