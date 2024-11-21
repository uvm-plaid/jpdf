package plaid.constraints.ast

case class RandomConstraintsTerm(w: String, i: Int) extends ConstraintsTerm{
  override def prettyPrint(): String = "r[" + w + "]@" + i.toString
}