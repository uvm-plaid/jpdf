package plaid.prelude.logic

import plaid.prelude.ast.*

import scala.annotation.tailrec
import scala.collection.mutable

class ConstraintAnalyzer(val program: Program, order: String) {

  private val verifier = new GenEntailVerifier(program, order)
  private val functionConstraints = new mutable.HashMap[Identifier, Constraints]()

  private def binding(formal: List[TypedIdentifier], actual: List[Expr]): Map[Identifier, Expr] =
    formal.map(_.y).zip(actual).toMap

  /** Calculate precondition and postcondition for a function (FN rule) */
  def inferPrePostFN(function: CmdFunc): Constraints = {
//    val evaluator = Evaluator(program)
//    val constraints = inferPrePostCmd(function.c, evaluator)
//
//    if function.precond == null && function.postcond == null then
//      functionConstraints.put(function.fname, constraints)
//      constraints
//    else
//      val e1 = Option(function.precond).getOrElse(TrueConstraint())
//      val e2 = Option(function.postcond).getOrElse(TrueConstraint())
//      val e1p = Option(constraints.precondition).getOrElse(TrueConstraint())
//      val e2p = Option(constraints.postcondition).getOrElse(TrueConstraint())
//
//      if verifier.genEntails(function.typedVariables, e1, e1p) &&
//        verifier.genEntails(function.typedVariables, AndConstraint(e1, e2p), e2) then
//        functionConstraints.put(function.fname, Constraints(function.precond.get, function.postcond.get))
//        Constraints(function.precond.get, function.postcond.get)
//      else
        throw new RuntimeException(s"The hardpack does not hold for ${prettyPrint(function.id)}")
  }

  /** Recursively infer pre/postconditions for a command */
  def inferPrePostCmd(command: Cmd, evaluator: Evaluator): Constraints = command match
    case assignCmd: AssignCmd =>
      Constraints(
        TrueConstraint(),
        EqualConstraint(
          evaluator.expression(assignCmd.e1),
          PartyIndexer.expression(evaluator.expression(assignCmd.e2), null)
        )
      )

    case assertCmd: AssertCmd =>
      Constraints(
        EqualConstraint(
          PartyIndexer.expression(evaluator.expression(assertCmd.e1), evaluator.expression(assertCmd.e3)),
          PartyIndexer.expression(evaluator.expression(assertCmd.e2), evaluator.expression(assertCmd.e3))
        ),
        TrueConstraint()
      )

//    case letCmd: LetCmd =>
//      inferPrePostCmd(evaluator.command(letCmd), evaluator)
//
//    case listCmd: ListCmd =>
//      val constraints = List(
//        inferPrePostCmd(listCmd.c1, evaluator),
//        inferPrePostCmd(listCmd.c2, evaluator))
//
//      val reducedPre =
//        constraints.map(_.precondition).filter(_ != null).reduceOption((a, b) => AndConstraint(a, b))
//      val reducedPost =
//        constraints.map(_.postcondition).filter(_ != null).reduceOption((a, b) => AndConstraint(a, b))
//
//      Constraints(reducedPre.orNull, reducedPost.orNull)

    case callCmd: CallCmd =>
      val id = callCmd.id
      val fn = program.resolveCommandFunction(id)

      if !functionConstraints.contains(id) then
        functionConstraints.put(id, inferPrePostFN(fn))
      val constraints = functionConstraints(id)

      val ev = evaluator.copy(bindings = binding(fn.typedVariables, callCmd.parms))
      val pre = Option(constraints.precondition).map(ev.constraint).orNull
      val post = Option(constraints.postcondition).map(ev.constraint).orNull
      Constraints(pre, post)
}

case class Entailment(cmd: Cmd, a: Constraint, b: Constraint)
case class HoareContext(cons: Constraint, ent: List[Entailment])
case class Contract(f: CmdFunc, pre: Constraint, post: Constraint, ctx: HoareContext)

extension (ctx: HoareContext)
  def include(c: Constraint): HoareContext = ctx.copy(cons = AndConstraint(ctx.cons, c))
  def include(e: Entailment): HoareContext = ctx.copy(ent = ctx.ent :+ e)

// TODO Pre-processing: Append party indexes to expressions and evaluate everything
// TODO Apply party indexes across commands too

//def contract(f: CommandFunction, contracts: List[Contract]): Contract =
//  val ctx = f.c.foldLeft((acc: HoareContext, cmd: Cmd) => cmd match
//    case AssertCmd(e1, e2, e3) => acc.include(Entailment(cmd, acc.cons, EqualConstraint(e1, e2)))
//    case AssignCmd(dest, OTExpr(e1, i1, e2, e3)) => ???
//    case AssignCmd(dest, OTFourExpr(s1, s2, i1, e1, e2, e3, e4)) => ???
//    case AssignCmd(e1, e2) => acc.include(EqualConstraint(e1, e2))
//    case CallCmd(fn, actualParms) =>
//      val contract = contracts.find(_.f.fname == fn).get
//      val formalParms = contract.f.typedVariables.map(_.y)
//      val bindings = Map.from(formalParms.zip(actualParms))
//      // Surely we can substitute these in the precondition?
//      val evaluator = Evaluator(program, bindings)
//      val posts = ???
//      val ent = ???
//      acc.include(contract.)
//    case other => throw Exception(s"Command $other is unsupported"))
//  // Precondition is True if it is unspecified
//  val pre = f.precond.getOrElse(TrueConstraint())
//  // Postcondition is all the constraints induced by the function body if it is unspecified
//  val post = f.postcond.getOrElse(hoareContext.cons)
//  Contract(f.fname, pre, post, )

def dependencies(f: CmdFunc): List[Identifier] =
  f.body.flatMap(dependencies)

/** Collect the immediate command function dependencies of a command */
@tailrec
def dependencies(cmd: Cmd): List[Identifier] = cmd match
  case CallCmd(fn, parms) => List(fn)
  case LetCmd(y, e, c) => c.flatMap(dependencies)
  case _ => List()

def contracts(program: Program): List[Contract] = {
  val fs = resolutionSort(program.cmdFuncs)
  val zzz = fs.foldLeft(List())// { (acc, x) => contract(acc, x)}
  ???
}

//def collectEntailments(fs: List[CommandFunction]): Map[CommandFunction, List[Entailment]] =
//  fs.map(f =>
//    val hoareContext = HoareContext().include(f.c)
//    // TODO Preconditions and postconditions should be Options on the CommandFunction
//    // Precondition is True if it is unspecified
//    val pre = if f.precond == null then TrueConstraint() else f.precond
//    // Postcondition is all the constraints induced by the function body if it is unspecified
//    val post = if f.postcond == null then hoareContext.constraint else f.postcond
//    f.copy(precond = pre, postcond = post) -> hoareContext.entailments
//  ).toMap
//  
/** As analysis of a Program progresses, support for tracking the constraints that are in play, and the entailments that
 * have to be checked */
