package plaid.constraints.ast

case class EqualConstraintsExpr(e1: ConstraintsTerm, e2: ConstraintsTerm) extends ConstraintsExpr{
  override def prettyPrint(): String = e1.prettyPrint() + "==" + e2.prettyPrint()
}
