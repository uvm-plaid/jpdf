package plaid.ast
import java.lang


case class ConcatExpr(getE1: PreludeExpression, getE2: PreludeExpression) extends PreludeExpression{
  override def prettyPrint(): String = throw new UnsupportedOperationException()
}
