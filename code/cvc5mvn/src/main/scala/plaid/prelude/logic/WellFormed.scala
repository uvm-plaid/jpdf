package plaid.prelude.logic

import plaid.prelude.ast.*

// check if a (final/evaluated) constraint is valid
object WellFormed {

  // TODO Apply constraints
  
  /**
   * valid constraint node type
   */
  private def constraintNodeTypes(n: Node) = recurse(n, {
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
    case OTExpr(_, _, _ , _) => true
    case OTFourExpr(_, _, _, _, _, _, _) => true
    case other =>
      println(s"***** Found non-ground node $other")
      false
  })

  /**
   * traverse the entire AST rooted at n and
   * recursively check whether the given node and its sub-nodes satisfy f
   * */
  private def recurse(n: Node, f: Node => Boolean): Boolean =
    f(n) && n.children().forall(c => recurse(c, f))


  private def memoryIndexesAreStrings(n: Node) = recurse(n, {
    case MessageExpr(Str(_)) => true
    case x: MessageExpr =>
      println(s"***** Found malformed memory expression $x")
      false
    case PublicExpr(Str(_)) => true
    case x: PublicExpr =>
      println(s"***** Found malformed memory expression $x")
      false
    case RandomExpr(Str(_)) => true
    case x: RandomExpr =>
      println(s"***** Found malformed memory expression $x")
      false
    case SecretExpr(Str(_)) => true
    case x: SecretExpr =>
      println(s"***** Found malformed memory expression $x")
      false
    case _ => true
  })

  private def partyIndexesAreNumbers(n: Node) = recurse(n, {
    case AtExpr(_, Num(_)) => true
    case x: AtExpr =>
      println(s"***** Found malformed at expression $x")
      false
    case _ => true
  })

  // 1. memories always come with at except public
  private def memoryWellFormed(n: Node) = recurse(n, {
    case AtExpr(MessageExpr(_), Num(_)) => true
    case AtExpr(RandomExpr(_), Num(_)) => true
    case AtExpr(SecretExpr(_), Num(_)) => true
    case AtExpr(PublicExpr(_), Num(_)) => true
    case AtExpr(OutputExpr(), Num(_)) => true
    case x: AtExpr =>
      println(s"***** Found malformed at expression $x")
      false
    case _ => true
  })


  def checkConstraint(constraint: Constraint): Boolean = {
      constraintNodeTypes(constraint) &&
      memoryIndexesAreStrings(constraint) &&
      partyIndexesAreNumbers(constraint) &&
      memoryWellFormed(constraint)
  }

}