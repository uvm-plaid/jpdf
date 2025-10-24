package plaid.ast

case class FunctionCallCommand(fname: Identifier, parameters: java.util.List[Expr]) extends PreludeCommand
