package plaid.ast

case class AssignCommand(e1: Expr, e2: Expr) extends PreludeCommand
