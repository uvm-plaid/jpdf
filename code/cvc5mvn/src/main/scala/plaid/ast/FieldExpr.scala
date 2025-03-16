package plaid.ast
import java.lang

// TODO: do we have to use immutable Map for case class?
case class FieldExpr(elements: java.util.TreeMap[Identifier, PreludeExpression]) extends PreludeExpression
