package plaid.prelude.logic

import plaid.prelude.ast.*
import plaid.prelude.prettyPrint
import plaid.prelude.transform.{Evaluator, PartyIndexer}

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
          PartyIndexer.appendPartyIndex(evaluator.expression(assignCmd.e2), null)
        )
      )

    case assertCmd: AssertCmd =>
      Constraints(
        EqualConstraint(
          PartyIndexer.appendPartyIndex(evaluator.expression(assertCmd.e1), evaluator.expression(assertCmd.e3)),
          PartyIndexer.appendPartyIndex(evaluator.expression(assertCmd.e2), evaluator.expression(assertCmd.e3))
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
      val id = callCmd.fn
      val fn = program.resolveCommandFunction(id)

      if !functionConstraints.contains(id) then
        functionConstraints.put(id, inferPrePostFN(fn))
      val constraints = functionConstraints(id)

      val ev = evaluator.copy(bindings = binding(fn.typedVariables, callCmd.parms))
      val pre = Option(constraints.precondition).map(ev.constraint).orNull
      val post = Option(constraints.postcondition).map(ev.constraint).orNull
      Constraints(pre, post)
}

case class Entailment(cmd: Cmd, pre: Constraint, post: Constraint)

// TODO Pre-processing: Append party indexes to expressions and evaluate everything
// TODO Apply party indexes across commands too

/** As analysis of a Program progresses, support for tracking the constraints that are in play, and the entailments that
 * have to be checked */
case class HoareContext(
  constraint: Constraint = TrueConstraint(),
  entailments: List[Entailment] = List()) {

  /** Constructs an updated context that reflects the inclusion of a new command */
  def include(cmd: Cmd): HoareContext = cmd match
    case AssertCmd(e1, e2, e3) =>
      val ent = Entailment(cmd, constraint, EqualConstraint(e1, e2))
      copy(entailments = entailments.appended(ent))
    case AssignCmd(dest, OTExpr(e1, i1, e2, e3)) => ???
    case AssignCmd(dest, OTFourExpr(s1, s2, i1, e1, e2, e3, e4)) => ???
    case AssignCmd(e1, e2) =>
      val eq = EqualConstraint(e1, e2)
      copy(constraint = AndConstraint(constraint, eq))
    case ListCmd(c1, c2) => include(c1).include(c2)
    case CallCmd(fn, parms) =>
      // TODO Tricky, generalize here or after?
      copy(constraint = ???, entailments = ???)
    case other => throw Exception(s"Command $other is not evaluated")
}
