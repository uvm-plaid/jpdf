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
    // TODO If we add non-overture nodes, make sure overture protocols contain only overture nodes separately!
  }

  private def recurse(n: Node, f: Node => Boolean): Boolean =
    f(n) && children(n).forall(c => recurse(c, f))

  private def freeFromAts(n: Node) = recurse(n, {
    case AtExpr(_, _) => false
    case _ => true
  })

  private def partyIndexesAreNumbers(n: Node) = recurse(n, {
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

  private def assertAtsNotNested(n: Node) = recurse(n, {
    case AssertCommand(e1, e2, _) => freeFromAts(e1) && freeFromAts(e2)
    case _ => true
  })

  def checkOverture(protocol: CommandList): Boolean =
    partyIndexesAreNumbers(protocol) &&
    memoryIndexesAreStrings(protocol) &&
    assignmentAtsCoverRight(protocol) &&
    assignmentLeftWellFormed(protocol) &&
    outputPartyIndexesMatch(protocol) &&
    assertAtsNotNested(protocol)

}