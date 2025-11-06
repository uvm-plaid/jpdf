package plaid.prelude.ast

trait Node {

  /**
   * Collect all the children of this node.
   */
  def children(): Iterable[Node] = this match
    case AtExpr(e1, e2) => List(e1, e2)
    case ConcatExpr(e1, e2) => List(e1, e2)
    case FieldExpr(elements) => elements.flatten((x, y) => List(x, y))
    case FieldSelectExpr(e, l) => List(e, l)
    case CallExpr(fn, parms) => fn :: parms
    case LetExpr(y, e1, e2) => List(y, e1, e2)
    case MessageExpr(e) => List(e)
    case RandomExpr(e) => List(e)
    case MinusExpr(e) => List(e)
    case PlusExpr(e1, e2) => List(e1, e2)
    case PublicExpr(e) => List(e)
    case SecretExpr(e) => List(e)
    case TimesExpr(e1, e2) => List(e1, e2)
    case AndConstraint(e1, e2) => List(e1, e2)
    case NotConstraint(e) => List(e)
    case CallConstraint(id, parms) => id :: parms
    case EqualConstraint(e1, e2) => List(e1, e2)
    case AssertCmd(e1, e2, e3) => List(e1, e2, e3)
    case AssignCmd(e1, e2) => List(e1, e2)
    case CallCmd(id, parms) => id :: parms
    case LetCmd(y, e, c) => y :: e :: c
    case RecordType(elements) => elements.flatten((x, y) => List(x, y))
    case ExprFunc(id, parms, body) => id :: body :: parms
    case ConstraintFunc(id, parms, body) => id :: body :: parms
    case CmdFunc(id, parms, body, precond, postcond) => id :: body ++ parms ++ precond.toList ++ postcond.toList
    case _ => List()

  /**
   * Collect all the descendants of this node.
   */
  def descendants(): Iterable[Node] = children() ++ children().flatMap(_.descendants())

  /**
   * Provides a human-readable string representation of an AST node and its decedents.
   */
  def prettyPrint(): String = this match
    case AssertCmd(e1, e2, i) => s"assert (${e1.prettyPrint()} = ${e2.prettyPrint()})@${i.prettyPrint()}\n"
    case AssignCmd(e1, e2) => s"${e1.prettyPrint()} := ${e2.prettyPrint()}\n"
    case AtExpr(e, i) => s"${e.prettyPrint()}@${i.prettyPrint()}"
    case Identifier(name) => name
    case MessageExpr(e) => s"m[${e.prettyPrint()}]"
    case MinusExpr(e) => s"-${e.prettyPrint()}"
    case Num(n) => s"$n"
    case OutputExpr() => "out"
    case PlusExpr(e1, e2) => s"(${e1.prettyPrint()} + ${e2.prettyPrint()})"
    case PublicExpr(e) => s"p[${e.prettyPrint()}]"
    case RandomExpr(e) => s"r[${e.prettyPrint()}]"
    case SecretExpr(e) => s"s[${e.prettyPrint()}]"
    case Str(x) => s"\"$x\""
    case ConcatExpr(e1, e2) => s"${e1.prettyPrint()} ++ ${e2.prettyPrint()}"
    case TimesExpr(e1, e2) => s"(${e1.prettyPrint()} * ${e2.prettyPrint()})"
    case AndConstraint(e1, e2) => s"${e1.prettyPrint()} AND ${e2.prettyPrint()}"
    case NotConstraint(e) => s"NOT ${e.prettyPrint()}"
    case EqualConstraint(e1, e2) => s"${e1.prettyPrint()} == ${e2.prettyPrint()}"
    case TrueConstraint() => "T"
    case CallExpr(i, es) => s"${i.prettyPrint()}(${es.map(_.prettyPrint()).mkString(", ")})"
    case x => throw Exception(s"Unhandled node $x")
}

case class TypedIdentifier(y: Identifier, t: Type) extends Node
case class Program(cmdFuncs: List[CmdFunc], exprFuncs: List[ExprFunc], constraintFuncs: List[ConstraintFunc]) extends Node
