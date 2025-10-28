package plaid.logic

import plaid.ast.*
import plaid.ScalaFunctions

import java.util.{ArrayList, HashMap, Optional, List as JList, Map as JMap}
import scala.jdk.CollectionConverters.*
import scala.jdk.OptionConverters.RichOption

class ConstraintAnalyzer(val program: Program, order: String) {

  private val verifier = new GenEntailVerifier(program, order)
  private val functionConstraints: JMap[Identifier, Constraints] = new HashMap()

  private def binding(formal: List[TypedIdentifier], actual: List[Expr]): Map[Identifier, Expr] =
    formal.map(_.y).zip(actual).toMap

  /** Calculate precondition and postcondition for a function (FN rule) */
  def inferPrePostFN(function: CommandFunction): Constraints = {
    val evaluator = new ConstraintEvaluator(program)
    val constraints = inferPrePostCmd(function.c, evaluator)

    if function.precond == null && function.postcond == null then
      functionConstraints.put(function.fname, constraints)
      constraints
    else
      val e1 = Option(function.precond).getOrElse(TrueConstraint())
      val e2 = Option(function.postcond).getOrElse(TrueConstraint())
      val e1p = Option(constraints.precondition).getOrElse(TrueConstraint())
      val e2p = Option(constraints.postcondition).getOrElse(TrueConstraint())

      if verifier.genEntails(function.typedVariables, e1, e1p) &&
        verifier.genEntails(function.typedVariables, AndConstraint(e1, e2p), e2) then
        functionConstraints.put(function.fname, new Constraints(function.precond, function.postcond))
        new Constraints(function.precond, function.postcond)
      else
        throw new RuntimeException(s"The hardpack does not hold for ${ScalaFunctions.prettyPrint(function.fname)}")
  }

  /** Recursively infer pre/postconditions for a command */
  def inferPrePostCmd(command: Cmd, evaluator: ConstraintEvaluator): Constraints = command match
    case assignCmd: AssignCmd =>
      new Constraints(
        TrueConstraint(),
        EqualConstraint(
          evaluator.toOverture(assignCmd.e1),
          appendPartyIndex(evaluator.toOverture(assignCmd.e2), null)
        )
      )

    case assertCmd: AssertCmd =>
      new Constraints(
        EqualConstraint(
          appendPartyIndex(evaluator.toOverture(assertCmd.e1), evaluator.toOverture(assertCmd.e3)),
          appendPartyIndex(evaluator.toOverture(assertCmd.e2), evaluator.toOverture(assertCmd.e3))
        ),
        TrueConstraint()
      )

    case letCmd: LetCmd =>
      inferPrePostCmd(evaluator.evalInstruction(letCmd), evaluator)

    case listCmd: ListCmd =>
      val constraints: JList[Constraints] = new ArrayList()
      constraints.add(inferPrePostCmd(listCmd.c1, evaluator))
      constraints.add(inferPrePostCmd(listCmd.c2, evaluator))

      val reducedPre: Optional[Constraint] =
        constraints.asScala.map(_.precondition).filter(_ != null).reduceOption((a, b) => AndConstraint(a, b)).asJava
      val reducedPost: Optional[Constraint] =
        constraints.asScala.map(_.postcondition).filter(_ != null).reduceOption((a, b) => AndConstraint(a, b)).asJava

      new Constraints(reducedPre.orElse(null), reducedPost.orElse(null))

    case callCmd: CallCmd =>
      val id = callCmd.fname
      val fn = program.resolveCommandFunction(id)

      if !functionConstraints.containsKey(id) then
        functionConstraints.put(id, inferPrePostFN(fn))
      val constraints = functionConstraints.get(id)

      evaluator.binding_list.add(binding(fn.typedVariables, callCmd.parameters).asJava)
      val pre = Option(constraints.precondition).map(evaluator.evalConstraint).orNull
      val post = Option(constraints.postcondition).map(evaluator.evalConstraint).orNull
      evaluator.binding_list.removeLast()
      new Constraints(pre, post)

    case _ =>
      throw new RuntimeException("cannot infer precondition from invalid input")

  /** Append party index to expressions for arithmetic operations on memories */
  private def appendPartyIndex(expression: Expr, partyIndex: Expr): Expr = expression match
    case e: AtExpr       => appendPartyIndex(e.e1, e.e2)
    case e: RandomExpr   => AtExpr(e, partyIndex)
    case e: SecretExpr   => AtExpr(e, partyIndex)
    case e: MessageExpr  => AtExpr(e, partyIndex)
    case e: OutputExpr   => AtExpr(e, partyIndex)
    case e: MinusExpr    => MinusExpr(appendPartyIndex(e.e, partyIndex))
    case e: TimesExpr    => TimesExpr(appendPartyIndex(e.e1, partyIndex), appendPartyIndex(e.e2, partyIndex))
    case e: PlusExpr     => PlusExpr(appendPartyIndex(e.e1, partyIndex), appendPartyIndex(e.e2, partyIndex))
    case e: OTExpr       =>
      OTExpr(
        appendPartyIndex(e.e1, e.i1),
        e.i1,
        appendPartyIndex(e.e2, partyIndex),
        appendPartyIndex(e.e3, partyIndex)
      )
    case e: OTFourExpr   =>
      OTFourExpr(
        appendPartyIndex(e.s1, e.i1),
        appendPartyIndex(e.s2, e.i1),
        e.i1,
        appendPartyIndex(e.e1, partyIndex),
        appendPartyIndex(e.e2, partyIndex),
        appendPartyIndex(e.e3, partyIndex),
        appendPartyIndex(e.e4, partyIndex)
      )
    case e: Expr => e
}
