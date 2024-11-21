package plaid.constraints.ast

case class MessageConstraintsTerm(w: String, i: Int) extends ConstraintsTerm{
  override def prettyPrint(): String = "m[" + w + "]@" + i.toString
}