package plaid.ast

case class FunctionCallExpr(fname: Identifier, parameters: java.util.List[PreludeExpression]) extends PreludeExpression, ConstraintExpr
