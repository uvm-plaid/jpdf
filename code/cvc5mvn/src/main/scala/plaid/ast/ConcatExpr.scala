package plaid.ast
import java.lang


case class ConcatExpr(e1: PreludeExpression, e2: PreludeExpression) extends PreludeExpression{
  override def prettyPrint(): String = throw new UnsupportedOperationException()
}
