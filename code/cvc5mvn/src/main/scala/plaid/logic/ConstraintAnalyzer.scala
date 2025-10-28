package plaid.logic

import plaid.ast.*
import plaid.prettyPrint

import scala.collection.mutable

class ConstraintAnalyzer(val program: Program, order: String) {

  private val verifier = new GenEntailVerifier(program, order)
  private val functionConstraints = new mutable.HashMap[Identifier, Constraints]()

  private def binding(formal: List[TypedIdentifier], actual: List[Expr]): Map[Identifier, Expr] =
    formal.map(_.y).zip(actual).toMap

  /** Calculate precondition and postcondition for a function (FN rule) */
  def inferPrePostFN(function: CommandFunction): Constraints = {
    val evaluator = Evaluator(program)
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
        functionConstraints.put(function.fname, Constraints(function.precond, function.postcond))
        Constraints(function.precond, function.postcond)
      else
        throw new RuntimeException(s"The hardpack does not hold for ${prettyPrint(function.fname)}")
  }

  /** Recursively infer pre/postconditions for a command */
  def inferPrePostCmd(command: Cmd, evaluator: Evaluator): Constraints = command match
    case assignCmd: AssignCmd =>
      Constraints(
        TrueConstraint(),
        EqualConstraint(
          evaluator.expression(assignCmd.e1),
          appendPartyIndex(evaluator.expression(assignCmd.e2), null)
        )
      )

    case assertCmd: AssertCmd =>
      Constraints(
        EqualConstraint(
          appendPartyIndex(evaluator.expression(assertCmd.e1), evaluator.expression(assertCmd.e3)),
          appendPartyIndex(evaluator.expression(assertCmd.e2), evaluator.expression(assertCmd.e3))
        ),
        TrueConstraint()
      )

    case letCmd: LetCmd =>
      inferPrePostCmd(evaluator.command(letCmd), evaluator)

    case listCmd: ListCmd =>
      val constraints = List(
        inferPrePostCmd(listCmd.c1, evaluator),
        inferPrePostCmd(listCmd.c2, evaluator))

      val reducedPre =
        constraints.map(_.precondition).filter(_ != null).reduceOption((a, b) => AndConstraint(a, b))
      val reducedPost =
        constraints.map(_.postcondition).filter(_ != null).reduceOption((a, b) => AndConstraint(a, b))

      Constraints(reducedPre.orNull, reducedPost.orNull)

    case callCmd: CallCmd =>
      val id = callCmd.fname
      val fn = program.resolveCommandFunction(id)

      if !functionConstraints.contains(id) then
        functionConstraints.put(id, inferPrePostFN(fn))
      val constraints = functionConstraints(id)

      val ev = evaluator.copy(bindings = binding(fn.typedVariables, callCmd.parameters))
      val pre = Option(constraints.precondition).map(ev.constraint).orNull
      val post = Option(constraints.postcondition).map(ev.constraint).orNull
      Constraints(pre, post)

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
