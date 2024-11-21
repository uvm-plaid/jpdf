package plaid.constraints.ast

case class MinusConstraintsTerm(e: ConstraintsTerm) extends ConstraintsTerm{
  override def prettyPrint() : String = "-" + e.prettyPrint()
}