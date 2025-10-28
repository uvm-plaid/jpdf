package plaid.cvc

import io.github.cvc5.{Kind, Solver, Term}
import plaid.antlr.Load
import plaid.ast.Cmd

class Verifier(val termFactory: TermFactory) {

  def satisfies(src: String): Boolean =
    satisfies(Load.command(src))

  def satisfies(command: Cmd): Boolean = {
    val e = termFactory.toTerm(command)
    satisfies(e)
  }

  def satisfies(e: Term): Boolean =
    findModelSatisfying(e) != null

  def findModelSatisfying(term: Term): Map[Term, Int] = {
    val solver = new Solver(termFactory.termManager)
    solver.setOption("produce-models", "true")
    solver.assertFormula(term)
    val result = solver.checkSat()
    if (!result.isSat) return null

    val mod = Integer.parseInt(termFactory.sort.getFiniteFieldSize)
    termFactory.getMemories.map(m =>
      val value = solver.getValue(m.term)
      val finiteFieldValue = Integer.parseInt(CvcUtils.finiteFieldValue(value))
      m.term -> Math.floorMod(finiteFieldValue, mod)).toMap
  }

  /** E1 entails E2 if there is no model that satisfies (E1 AND not E2) */
  def entails(e1: Term, e2: Term): Boolean = {
    if (e2 == null) return true
    val notE2 = termFactory.termManager.mkTerm(Kind.NOT, e2)
    val e1EntailsNotE2 = termFactory.termManager.mkTerm(Kind.AND, e1, notE2)
    !satisfies(e1EntailsNotE2)
  }
}
