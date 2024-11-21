package plaid.constraints.ast

case class PublicConstraintsTerm(w: String) extends ConstraintsTerm{
  override def prettyPrint(): String = "p[" + w + "]@"
}
