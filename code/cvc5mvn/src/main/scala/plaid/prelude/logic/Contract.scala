package plaid.prelude.logic

import plaid.prelude.ast.ListCmdFuncExt.dependencyOrdered
import plaid.prelude.ast.{AndConstraint, AssertCmd, AssignCmd, CallCmd, Cmd, CmdFunc, Constraint, EqualConstraint, Identifier, Node, TrueConstraint}

case class Entailment(src: Node, a: Constraint, b: Constraint)

/**
 * A context that is built up as commands are processed in succession. It
 * includes as a single constraint the conjunction of the postconditions of all
 * the commands that have been processed so far, and a collection of all the
 * entailments that have to hold based on all the commands that have been
 * processed so far.
 */
case class HoareContext(cons: Constraint = TrueConstraint(), ent: List[Entailment] = List()) {
  /** Add a new postcondition in the constraint of this Hoare context. */
  private def include(c: Constraint): HoareContext = copy(cons = AndConstraint(cons, c))
  /** Add a new entailment to this Hoare context. */
  private def include(e: Entailment): HoareContext = copy(ent = e :: ent)
  /** Include a new command in this Hoare context. */
  def include(cmd: Cmd, contracts: List[Contract]): HoareContext = cmd match
    case AssertCmd(e1, e2, party) =>
      val a = e1.indexParties(Some(party))
      val b = e2.indexParties(Some(party))
      include(Entailment(cmd, cons, EqualConstraint(a, b)))
    case AssignCmd(e1, e2) => include(EqualConstraint(e1.indexParties(), e2.indexParties()))
    case CallCmd(id, parms) =>
      val contract = contracts.lookup(id)
      val formalParms = contract.f.typedVariables.map(_.y)
      val actualParms = parms.map(_.indexParties())
      val bindings = formalParms.zip(actualParms).toMap
      val pre = contract.pre.expand(bindings = bindings)
      val post = contract.post.expand(bindings = bindings)
      include(Entailment(cmd, cons, pre)).include(post)
    case other => throw Exception(s"Unsupported command $other")
}

/**
 * Tracks for a function what its (possibly inferred) preconditions and
 * postconditions are, as well as any entailments that must hold internally so
 * that the postcondition can actually be trusted.
 */
case class Contract(f: CmdFunc, pre: Constraint, post: Constraint, internals: List[Entailment])

extension (trg: List[Contract])
  /** Look up the contract for the specified command function identifier. */
  def lookup(id: Identifier): Contract = trg.find(_.f.id == id).get

extension (trg: List[CmdFunc])
  /** Calculate contracts for all the commands (which must be expanded). */
  def contracts(): List[Contract] = trg
    .dependencyOrdered()
    .foldLeft(List()) { (acc, x) => x.contract(acc) :: acc }

extension (trg: CmdFunc)
  def contract(fns: List[Contract]): Contract =
    val pre = trg.precond.getOrElse(TrueConstraint())
    val ctx = trg.body.foldLeft(HoareContext(cons = pre)) { (acc, x) => acc.include(x, fns) }
    val post = trg.postcond.getOrElse(ctx.cons)
    val overall = Entailment(trg, ctx.cons, post)
    Contract(trg, pre, post, overall :: ctx.ent)
