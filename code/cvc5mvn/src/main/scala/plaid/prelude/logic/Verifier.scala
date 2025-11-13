package plaid.prelude.logic

import io.github.cvc5.{Kind, Solver, Term}
import plaid.prelude.cvc.TermFactory

class Verifier(val termFactory: TermFactory) {

  def satisfiable(e: Term): Boolean =
    findModelSatisfying(e) != null

  def findModelSatisfying(term: Term): Map[Term, Int] =
    val solver = new Solver(termFactory.termManager)
    solver.setOption("produce-models", "true")
    solver.assertFormula(term)
    val result = solver.checkSat()
    if (!result.isSat) return null

    val mod = Integer.parseInt(termFactory.sort.getFiniteFieldSize)
    termFactory.getMemories.map(m =>
      val value = solver.getValue(m.term)
      val finiteFieldValue = Integer.parseInt(value.getFiniteFieldValue)
      m.term -> Math.floorMod(finiteFieldValue, mod)).toMap

  /** E1 entails E2 if there is no model that satisfies (E1 AND not E2) */
  def entails(e1: Term, e2: Term): Boolean =
    if (e2 == null) return true
    val notE2 = termFactory.termManager.mkTerm(Kind.NOT, e2)
    val e1EntailsNotE2 = termFactory.termManager.mkTerm(Kind.AND, e1, notE2)
    !satisfiable(e1EntailsNotE2)
}

extension (trg: Contract)
  /** Finds any entailments in a function contract that are false. */
  def verificationFailures(cvc: TermFactory): List[Entailment] =
    val bindings = trg.f.typedVariables.map(x => x.y -> x.t.freshValue()).toMap
    trg.internals.filter { x =>
      val a = x.a.expand(bindings = bindings)
      val b = x.b.expand(bindings = bindings)

      if !WellFormed.checkConstraint(a) || !WellFormed.checkConstraint(b) then
        throw Exception(s"Constraints must be ground")

      val verifier = new Verifier(cvc)
      !verifier.entails(cvc.toTerm(a), cvc.toTerm(b))
    }
