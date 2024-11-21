package plaid.constraints.ast

case class NotConstraintsExpr(e: ConstraintsExpr) extends ConstraintsExpr{
  override def prettyPrint(): String = "NOT" + e.prettyPrint()
}