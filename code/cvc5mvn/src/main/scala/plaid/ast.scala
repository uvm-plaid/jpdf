package plaid

import plaid.ast.*

import java.util.stream.Collectors

/**
 * Pretty print provides a human-readable string representation of an AST node.
 *
 * @param n The node to represent
 * @return A human-readable string representation of the node
 */
def prettyPrint(n: Node): String = n match {
  case AssertCommand(e1, e2, i) => s"assert (${prettyPrint(e1)} = ${prettyPrint(e2)})@${prettyPrint(i)}\n"
  case AssignCommand(e1, e2) => s"${prettyPrint(e1)} := ${prettyPrint(e2)}\n"
  case AtExpr(e, i) => s"${prettyPrint(e)}@${prettyPrint(i)}"
  case CommandList(lst) => lst.stream().map(x => prettyPrint(x)).collect(Collectors.joining(""))
  case Identifier(name) => name
  case MessageExpr(e) => s"m[\"${prettyPrint(e)}\"]"
  case MinusExpr(e) => s"-${prettyPrint(e)}"
  case Num(n) => s"$n"
  case OutputExpr() => "out"
  case PlusExpr(e1, e2) => s"(${prettyPrint(e1)} + ${prettyPrint(e2)})}"
  case PublicExpr(e) => s"p[\"${prettyPrint(e)}\"]"
  case RandomExpr(e) => s"r[\"${prettyPrint(e)}\"]"
  case SecretExpr(e) => s"s[\"${prettyPrint(e)}\"]"
  case Str(x) => x
  case TimesExpr(e1, e2) => s"(${prettyPrint(e1)} * ${prettyPrint(e2)})}"
}