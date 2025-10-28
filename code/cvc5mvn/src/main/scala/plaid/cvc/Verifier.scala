package plaid.cvc

import io.github.cvc5.{Kind, Result, Solver, Term}
import plaid.antlr.Loader
import plaid.ast.Cmd

import java.util.{HashMap, Map}
import scala.collection.JavaConverters.asScalaSetConverter

class Verifier(val termFactory: TermFactory) {

  def satisfies(src: String): Boolean =
    satisfies(Loader.toCommand(src))

  def satisfies(command: Cmd): Boolean = {
    val e = termFactory.toTerm(command)
    satisfies(e)
  }

  def satisfies(e: Term): Boolean =
    findModelSatisfying(e) != null

  def findModelSatisfying(term: Term): Map[Term, Integer] = {
    val solver = new Solver(termFactory.termManager)
    solver.setOption("produce-models", "true")
    solver.assertFormula(term)
    val result = solver.checkSat()
    if (!result.isSat()) return null

    val map: Map[Term, Integer] = new HashMap()
    val mod = Integer.parseInt(termFactory.sort.getFiniteFieldSize)
    for (memory <- termFactory.getMemories.asScala) {
      val value = solver.getValue(memory.term)
      val finiteFieldValue = Integer.parseInt(CvcUtils.finiteFieldValue(value))
      map.put(memory.term, Math.floorMod(finiteFieldValue, mod))
    }
    map
  }

  /** E1 entails E2 if there is no model that satisfies (E1 AND not E2) */
  def entails(e1: Term, e2: Term): Boolean = {
    if (e2 == null) return true
    val notE2 = termFactory.termManager.mkTerm(Kind.NOT, e2)
    val e1EntailsNotE2 = termFactory.termManager.mkTerm(Kind.AND, e1, notE2)
    !satisfies(e1EntailsNotE2)
  }
}
