package plaid.ast

case class AssertCommand(e1: Expr, e2: Expr, e3: Expr) extends PreludeCommand
