package plaid.prelude.logic

import io.github.cvc5.{Kind, Solver, Term}
import plaid.prelude.cvc.TermFactory

extension (trg: TermFactory)
  /** Determine if the term has a set of assignments that make it true. */
  def satisfiable(e: Term): Boolean =
    findModelSatisfying(e).nonEmpty

  /** Find a set of assignments that make a term true, if one exists. */
  def findModelSatisfying(term: Term): Option[Map[Term, Int]] =
    val solver = new Solver(trg.termManager)
    solver.setOption("produce-models", "true")
    solver.assertFormula(term)
    val result = solver.checkSat()
    if (!result.isSat) return None

    val mod = Integer.parseInt(trg.sort.getFiniteFieldSize)
    Some(trg.getMemories.map(m =>
      val value = solver.getValue(m.term)
      val finiteFieldValue = Integer.parseInt(value.getFiniteFieldValue)
      m.term -> Math.floorMod(finiteFieldValue, mod)).toMap)

  /** E1 entails E2 if there is no model that satisfies (E1 AND not E2) */
  def entails(e1: Term, e2: Term): Boolean =
    val notE2 = trg.termManager.mkTerm(Kind.NOT, e2)
    val e1EntailsNotE2 = trg.termManager.mkTerm(Kind.AND, e1, notE2)
    !satisfiable(e1EntailsNotE2)

// TODO Should this be another TermFactory extension method?
extension (trg: Contract)
  /** Finds any entailments in a function contract that are false. */
  def verificationFailures(cvc: TermFactory): List[Entailment] =
    val bindings = trg.f.typedVariables.map(x => x.y -> x.t.freshValue()).toMap
    trg.internals.filter { x =>
      val a = x.a.expand(bindings = bindings)
      val b = x.b.expand(bindings = bindings)

      if !WellFormed.checkConstraint(a) || !WellFormed.checkConstraint(b) then
        throw Exception(s"Constraints must be ground")

      !cvc.entails(cvc.toTerm(a), cvc.toTerm(b))
    }
