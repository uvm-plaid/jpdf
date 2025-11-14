package plaid.prelude.logic

import io.github.cvc5.TermManager
import org.junit.Assert.{assertFalse, assertTrue}
import org.junit.Test
import plaid.prelude.antlr.Loader
import plaid.prelude.ast.Constraint
import plaid.prelude.cvc.TermFactory

class VerifierTest {
  private val cvc = new TermFactory(new TermManager(), "7")

  private def satisfiable(src: String): Boolean =
    satisfiable(Loader.constraint(src))

  private def satisfiable(constraint: Constraint): Boolean =
    val e = cvc.toTerm(constraint)
    cvc.satisfiable(e)

  /** Single equality constraints are always satisfiable. */
  @Test
  def singleEquality(): Unit =
    assertTrue(satisfiable("out@1 == 1"))

  /** Conjunction of non-contradictory constraints is satisfiable. */
  @Test
  def multipleNoncontradictory(): Unit =
    assertTrue(satisfiable("out@1 == 1 AND out@2 == 2"))

  /** Conjunction of contradictory constraints are not satisfiable. */
  @Test
  def contradictory(): Unit =
    assertFalse(satisfiable("out@1 == 1 AND out@1 == 2"))

  /** Everything entails a universally satisfied constraint. */
  @Test
  def everythingEntails(): Unit =
    val t = cvc.toTerm(Loader.constraint("T"))
    val f = cvc.toTerm(Loader.constraint("NOT T"))
    assertTrue(cvc.entails(t, t))
    assertTrue(cvc.entails(f, t))

  /** Nothing entails an unsatisfiable constraint except another unsatisfiable constraint. */
  @Test
  def nothingEntails(): Unit =
    val t = cvc.toTerm(Loader.constraint("T"))
    val f = cvc.toTerm(Loader.constraint("NOT T"))
    assertFalse(cvc.entails(t, f))
    assertTrue(cvc.entails(f, f))
}
