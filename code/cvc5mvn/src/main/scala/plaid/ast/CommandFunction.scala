package plaid.ast

case class CommandFunction(fname: Identifier, y: java.util.List[Identifier], c: PreludeCommand) extends Node
