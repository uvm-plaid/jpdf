package plaid.ast
import java.lang

case class ExprFunction(fname: Identifier, y: java.util.List[Identifier], e: PreludeExpression) extends Node
