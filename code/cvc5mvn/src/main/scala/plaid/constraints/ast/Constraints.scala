package plaid.constraints.ast

case class Constraints(constraints : java.util.List[ConstraintsExpr]) extends Node{
  override def prettyPrint(): String = throw new UnsupportedOperationException()

}
