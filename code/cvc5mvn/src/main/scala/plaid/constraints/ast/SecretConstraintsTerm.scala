package plaid.constraints.ast

case class SecretConstraintsTerm(w: String, i: Int) extends ConstraintsTerm{
  override def prettyPrint(): String = "s[" + w + "]@" + i.toString
}
