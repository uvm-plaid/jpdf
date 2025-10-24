package plaid.ast

trait ConstraintExpr extends Node
case class AndConstraintExpr(e1: ConstraintExpr, e2: ConstraintExpr) extends ConstraintExpr
case class EqualConstraintExpr(e1: Expr, e2: Expr) extends ConstraintExpr
case class NotConstraintExpr(e: ConstraintExpr) extends ConstraintExpr
case class TrueConstraintExpr() extends ConstraintExpr
