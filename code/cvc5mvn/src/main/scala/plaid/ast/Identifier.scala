package plaid.ast

case class Identifier(name: String) extends PreludeExpression, ConstraintExpr, java.lang.Comparable[Identifier] {
  override def compareTo(other: Identifier): Int = name.compareTo(other.name)
}
