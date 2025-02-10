package plaid.eval

import scala.jdk.CollectionConverters._

import plaid.ast._

object OvertureChecker {

  private def children(n: Node): Iterable[Node] = n match {
    case AssertCommand(e1, e2, i) => Iterable(e1, e2, i)
    case AssignCommand(e1, e2) => Iterable(e1, e2)
    case AtExpr(e, i) => Iterable(e, i)
    case CommandList(lst) => lst.asScala
    case MessageExpr(s) => Iterable(s)
    case MinusExpr(e) => Iterable(e)
    case Num(_) => Iterable()
    case OutputExpr() => Iterable()
    case PlusExpr(e1, e2) => Iterable(e1, e2)
    case PublicExpr(e) => Iterable(e)
    case RandomExpr(e) => Iterable(e)
    case SecretExpr(e) => Iterable(e)
    case Str(_) => Iterable()
    case TimesExpr(e1, e2) => Iterable(e1, e2)
    case OTExpr(e1, i1, e2, e3) => Iterable(e1, i1, e2, e3)
    case OTFourExpr(s1, s2, i1, e1, e2, e3, e4) => Iterable(s1, s2, i1, e1, e2, e3, e4)
    // TODO Add other kinds of nodes and externalize
  }

  private def overtureNodeTypes(n: Node) = recurse(n, {
    case AssertCommand(_, _, _) => true
    case AssignCommand(_, _) => true
    case AtExpr(_, _) => true
    case CommandList(_) => true
    case MessageExpr(_) => true
    case MinusExpr(_) => true
    case Num(_) => true
    case OutputExpr() => true
    case PlusExpr(_, _) => true
    case PublicExpr(_) => true
    case RandomExpr(_) => true
    case SecretExpr(_) => true
    case Str(_) => true
    case TimesExpr(_, _) => true
    case OTExpr(_, _, _ , _) => true
    case OTFourExpr(_, _, _, _, _, _, _) => true
    case _ => false
  })

  private def recurse(n: Node, f: Node => Boolean): Boolean =
    f(n) && children(n).forall(c => recurse(c, f))

  private def freeFromAts(n: Node) = recurse(n, {
    case AtExpr(_, _) => false
    case _ => true
  })

  private def partyIndexesAreNumbers(n: Node) = recurse(n, {
    case OTExpr(_, Num(_), _, _) => true
    case OTExpr(_, _, _, _) => false
    case OTFourExpr(_, _, Num(_), _, _, _, _) => true
    case OTFourExpr(_, _, _, _, _, _, _) => false
    case AtExpr(_, Num(_)) => true
    case AtExpr(_, _) => false
    case AssertCommand(_, _, Num(_)) => true
    case AssertCommand(_, _, _) => false
    case _ => true
  })

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

  private def assignmentAtsCoverRight(n: Node) = recurse(n, {
    case AssignCommand(_, AtExpr(e, _)) => freeFromAts(e)
    case AssignCommand(_, _) => false
    case _ => true
  })

  private def assignmentLeftWellFormed(n: Node) = recurse(n, {
    case AssignCommand(AtExpr(MessageExpr(_), _), _) => true
    case AssignCommand(PublicExpr(_), _) => true
    case AssignCommand(AtExpr(OutputExpr(), _), _) => true
    case AssignCommand(_, _) => false
    case _ => true
  })

  private def outputPartyIndexesMatch(n: Node) = recurse(n, {
    case AssignCommand(AtExpr(OutputExpr(), i1), AtExpr(_, i2)) => i1 == i2
    case _ => true
  })

  private def otReceiverPartyIndexesMatch(n: Node) = recurse(n, {
    case AssignCommand(AtExpr(_, i), AtExpr(OTExpr(_, i1, _, _), _)) => i == i1
    case AssignCommand(AtExpr(_, i), AtExpr(OTFourExpr(_, _, i1, _, _, _, _), _)) => i == i1
    case _ => true
  })

  private def assertAtsNotNested(n: Node) = recurse(n, {
    case AssertCommand(e1, e2, _) => freeFromAts(e1) && freeFromAts(e2)
    case _ => true
  })


  def checkOverture(protocol: PreludeCommand): Boolean =
    overtureNodeTypes(protocol) &&
    partyIndexesAreNumbers(protocol) &&
    memoryIndexesAreStrings(protocol) &&
    assignmentAtsCoverRight(protocol) &&
    assignmentLeftWellFormed(protocol) &&
    outputPartyIndexesMatch(protocol) &&
      otReceiverPartyIndexesMatch(protocol) &&
    assertAtsNotNested(protocol)

}