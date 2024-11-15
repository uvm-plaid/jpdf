package plaid.ast

case class FieldSelectExpr(getE: PreludeExpression, getL: Identifier) extends PreludeExpression{
  override def prettyPrint(): String = throw new UnsupportedOperationException()
}
