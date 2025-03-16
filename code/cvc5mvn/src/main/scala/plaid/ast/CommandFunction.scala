package plaid.ast


case class CommandFunction(fname: Identifier, typedVariables: java.util.List[TypedIdentifier], c: PreludeCommand) extends Node