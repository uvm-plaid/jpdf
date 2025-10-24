package plaid.eval

import plaid.ast._

// check if a (final/evaluated) constraint is valid
object ConstraintChecker {

  /**
   * convert AST to Iterable type
   * let us recursively explore the full AST
   */
  private def children(n: Node): Iterable[Node] = n match {
    case AndConstraint(e1, e2) => Iterable(e1, e2)
    case EqualConstraint(e1, e2) => Iterable(e1, e2)
    case TrueConstraint() => Iterable()
    case PlusExpr(e1, e2) => Iterable(e1, e2)
    case MinusExpr(e) => Iterable(e)
    case TimesExpr(e1, e2) => Iterable(e1, e2)
    case AtExpr(e, i) => Iterable(e, i)
    case RandomExpr(e) => Iterable(e)
    case SecretExpr(e) => Iterable(e)
    case MessageExpr(e) => Iterable(e)
    case PublicExpr(e) => Iterable(e)
    case OutputExpr() => Iterable()
    case Str(_) => Iterable()
    case Num(_) => Iterable()
    case OTExpr(e1, i1, e2, e3) => Iterable(e1, i1, e2, e3)
    case OTFourExpr(s1, s2, i1, e1, e2, e3, e4) => Iterable(s1, s2, i1, e1, e2, e3, e4)
  }

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
    case _ => false
  })

  /**
   * traverse the entire AST rooted at n and
   * recursively check whether the given node and its sub-nodes satisfy f
   * */
  private def recurse(n: Node, f: Node => Boolean): Boolean =
    f(n) && children(n).forall(c => recurse(c, f))


  private def memoryIndexesAreStrings(n: Node) = recurse(n, {
    case MessageExpr(Str(_)) => true
    case MessageExpr(_) => false
    case PublicExpr(Str(_)) => true
    case PublicExpr(_) => false
    case RandomExpr(Str(_)) => true
    case RandomExpr(_) => false
    case SecretExpr(Str(_)) => true
    case SecretExpr(_) => false
    case _ => true
  })

  private def partyIndexesAreNumbers(n: Node) = recurse(n, {
    case AtExpr(_, Num(_)) => true
    case AtExpr(_, _) => false
    case _ => true
  })

  // 1. memories always come with at except public
  private def memoryWellFormed(n: Node) = recurse(n, {
    case AtExpr(MessageExpr(_), Num(_)) => true
    case AtExpr(RandomExpr(_), Num(_)) => true
    case AtExpr(SecretExpr(_), Num(_)) => true
    case AtExpr(PublicExpr(_), Num(_)) => true
    case AtExpr(OutputExpr(), Num(_)) => true
    case AtExpr(_, _) => false
    case _ => true
  })


  def checkConstraint(constraint: Constraint): Boolean = {
      constraintNodeTypes(constraint) &&
      memoryIndexesAreStrings(constraint) &&
      partyIndexesAreNumbers(constraint) &&
      memoryWellFormed(constraint)
  }

}