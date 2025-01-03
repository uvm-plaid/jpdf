package plaid.ast
import java.{lang, util}

case class LetExpr(y: Identifier, e1: PreludeExpression, e2: PreludeExpression) extends PreludeExpression{
  override def prettyPrint(): String = throw new UnsupportedOperationException()
}
