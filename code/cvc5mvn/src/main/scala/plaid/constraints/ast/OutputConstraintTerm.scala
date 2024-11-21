package plaid.constraints.ast

case class OutputConstraintTerm(i: Int) extends ConstraintsTerm{
  override def prettyPrint(): String = "out@" + i.toString
}
