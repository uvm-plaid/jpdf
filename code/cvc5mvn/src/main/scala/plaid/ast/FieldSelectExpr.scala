package plaid.ast

case class FieldSelectExpr(e: PreludeExpression, l: Identifier) extends PreludeExpression{
  override def prettyPrint(): String = throw new UnsupportedOperationException()
}
