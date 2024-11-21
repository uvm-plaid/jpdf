package plaid.constraints.ast

case class TimesConstraintsTerm(e1: ConstraintsTerm, e2: ConstraintsTerm) extends ConstraintsTerm{
  override def prettyPrint() : String = e1.prettyPrint() + "*" + e2.prettyPrint()
}
