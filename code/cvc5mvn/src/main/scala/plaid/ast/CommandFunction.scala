package plaid.ast


case class CommandFunction(fname: Identifier, typedVariables: java.util.List[TypedIdentifier], c: Cmd, precond: Constraint, postcond: Constraint) extends Node