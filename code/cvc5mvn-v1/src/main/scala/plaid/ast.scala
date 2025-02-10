package plaid

import plaid.ast.*

import java.util.stream.Collectors

/**
 * Provides a human-readable string representation of an AST node and its decedents. Currently only supports node types
 * used in Overture protocols.
 *
 * @param n The node to represent
 * @return Human-readable representation
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
  case OTExpr(e1, i1, e2, e3) => s"OT(${prettyPrint(e1)}@${prettyPrint(i1)},${prettyPrint(e2)}, ${prettyPrint(e3)})"
  case OTFourExpr(s1, s2, i1, e1, e2, e3, e4) => s"OT4((${prettyPrint(s1)}, ${prettyPrint(s2)})@${prettyPrint(i1)}, ${prettyPrint(e1)}, ${prettyPrint(e2)}, ${prettyPrint(e3)}, ${prettyPrint(e4)})"
  case AndConstraintExpr(e1, e2) => s"${prettyPrint(e1)} AND ${prettyPrint(e2)}"
  case NotConstraintExpr(e) => s"NOT ${prettyPrint(e)}"
  case EqualConstraintExpr(e1, e2) => s"${prettyPrint(e1)} == ${prettyPrint(e2)}"

}
