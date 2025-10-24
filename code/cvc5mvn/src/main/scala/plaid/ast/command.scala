package plaid.ast

trait PreludeCommand extends Node
case class AssertCommand(e1: Expr, e2: Expr, e3: Expr) extends PreludeCommand
case class AssignCommand(e1: Expr, e2: Expr) extends PreludeCommand
case class CommandList(c1: PreludeCommand, c2: PreludeCommand) extends PreludeCommand
case class FunctionCallCommand(fname: Identifier, parameters: java.util.List[Expr]) extends PreludeCommand
case class LetCommand(y: Identifier, e: Expr, c: PreludeCommand) extends PreludeCommand
