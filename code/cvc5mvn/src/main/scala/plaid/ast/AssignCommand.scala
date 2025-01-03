package plaid.ast

case class AssignCommand(e1: PreludeExpression, e2: PreludeExpression) extends PreludeCommand
