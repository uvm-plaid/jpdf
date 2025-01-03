package plaid.ast

case class AssertCommand(e1: PreludeExpression, e2: PreludeExpression, e3: PreludeExpression) extends PreludeCommand
