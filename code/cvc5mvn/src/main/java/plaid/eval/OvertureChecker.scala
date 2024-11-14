//package plaid.eval
//import plaid.ast.*
//
//class OvertureChecker {
//  // m[w]@i := e@i
//  // p[w] := e@i
//  // out@i := e@i
//
//  /**
//   * AtExpr(e1, e2), e2 has to be a number
//   * @param node input
//   */
//  def atsHaveNums(node: Node): Unit = {
//    node match {
//      case AtExpr(_, Num(_)) =>
//      case AtExpr(_, _) => throw new RuntimeException("not a number: " + e2)
//      case node => node.children.forEach(this.atsHaveNums)
//    }
//  }
//
//  /**
//   * TODO: At expression should not appear in any other nodes
//   * @param node input
//   */
//  private def noAt(node: Node): Unit = {
//    node match {
//      case AtExpr(_, _) => throw new RuntimeException("multiple at expressions")
//
//      case _ => node.children.forEach(this.noAt)
//    }
//  }
//
//  /**
//   * e1 := e2
//   * check right side of assignment. there should not be multiple at expression
//   * @param node input
//   */
//  def isValidSender(node: Node): Unit = {
//    node match {
//      case AssignCommand(e1, e2) if (e2.getClass == classOf[AtExpr]) => e2.children.forEach(this.noAt)
//
//      case _ => node.children.forEach(this.isValidSender)
//    }
//  }
//
//  /**
//   * // out@i := e@i
//   * for output case, party indices should agree
//   * @param node input
//   */
//  def isIndexMatch(node: Node): Unit = {
//    node match {
//      case AssignCommand(AtExpr(OutputExpr(), Num(num1)), AtExpr(_, Num(num2))) if num1 != num2 => throw new RuntimException("party indices disagree: " + num1 ", " + num2)
//
//      case _ => node.children.forEach(this.isIndexMatch)
//    }
//  }
//
//  /**
//   * memories in overture contain only string
//   * TODO: will node be assigncommand?
//   */
//  def isValidVariables(node: Node): Unit = {
//    node match{
//      case AssignmentCommand(AtExpr(MessageExpr(Str(_)), _), AtExpr( , Num(_)))
//      }
//
//
//
//    }
//  }
//  // check e is overture expression in m[w] := e@i
//
//  // left side only should be memories
//}
