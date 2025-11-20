package plaid.prelude.logic

import plaid.prelude.ast.*

case class AstError(ctx: String, msg: String, offender: Node)

/** Check if a node has a valid node type to appear in a fully expanded constraint. */
private def legalNodeType(n: Node) = n match
  case AndConstraint(_, _) => true
  case EqualConstraint(_, _) => true
  case TrueConstraint() => true
  case PlusExpr(_, _) => true
  case MinusExpr(_) => true
  case TimesExpr(_, _) => true
  case AtExpr(_, _) => true
  case RandomExpr(_) => true
  case SecretExpr(_) => true
  case MessageExpr(_) => true
  case PublicExpr(_) => true
  case OutputExpr() => true
  case Str(_) => true
  case Num(_) => true
  case _  => false

/** Check if a memory expression has a string literal for its index (if it has an index). */
private def stringMemoryIndex(n: Node) = n match
  case MessageExpr(Str(_)) => true
  case x: MessageExpr => false
  case PublicExpr(Str(_)) => true
  case x: PublicExpr => false
  case RandomExpr(Str(_)) => true
  case x: RandomExpr => false
  case SecretExpr(Str(_)) => true
  case x: SecretExpr => false
  case _ => true

/** Check if an at expression has a numeric party index. */
private def numericPartyIndex(n: Node) = n match
  case AtExpr(_, Num(_)) => true
  case x: AtExpr => false
  case _ => true

/** Check that a memory expression is always wrapped with at, except public. */
private def memoryAtWrapped(n: Node) = n match
  case AtExpr(MessageExpr(_), Num(_)) => true
  case AtExpr(RandomExpr(_), Num(_)) => true
  case AtExpr(SecretExpr(_), Num(_)) => true
  case AtExpr(PublicExpr(_), Num(_)) => true
  case AtExpr(OutputExpr(), Num(_)) => true
  case x: AtExpr => false
  case _ => true

extension (trg: Constraint)
  def checkProperExpansion(ctx: String): List[AstError] =
    val checks = Map(
      legalNodeType -> "Illegal node type in constraint",
      stringMemoryIndex -> "Memory expression must have string literal index",
      numericPartyIndex -> "At expression must have a numeric party index",
      memoryAtWrapped -> "At expression must wrap a memory expression")

    trg.descendants().flatMap(c => checks
      .flatMap { (f, msg) => if !f(c) then List(AstError(ctx, msg, c)) else Nil })
      .toList
