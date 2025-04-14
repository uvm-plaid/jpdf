package plaid.ast


case class CommandFunction(fname: Identifier, typedVariables: java.util.List[TypedIdentifier], c: PreludeCommand, precond: ConstraintExpr, postcond: ConstraintExpr) extends Node