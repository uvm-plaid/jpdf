package plaid.prelude.cvc

import io.github.cvc5.TermManager
import org.junit.Assert.{assertFalse, assertTrue}
import org.junit.Test
import plaid.prelude.antlr.Loader
import plaid.prelude.ast.Constraint

class VerifierTmpTest {
  private val termManager = new TermManager()
  private val termFactory = new TermFactory(termManager, "7")
  private val verifier = new VerifierTmp(termFactory)

  private def satisfiable(src: String): Boolean =
    satisfiable(Loader.constraint(src))

  private def satisfiable(constraint: Constraint): Boolean =
    val e = termFactory.toTerm(constraint)
    verifier.satisfies(e)

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
    val t = termFactory.toTerm(Loader.constraint("T"))
    val f = termFactory.toTerm(Loader.constraint("NOT T"))
    assertTrue(verifier.entails(t, t))
    assertTrue(verifier.entails(f, t))

  /** Nothing entails an unsatisfiable constraint except another unsatisfiable constraint. */
  @Test
  def nothingEntails(): Unit =
    val t = termFactory.toTerm(Loader.constraint("T"))
    val f = termFactory.toTerm(Loader.constraint("NOT T"))
    assertFalse(verifier.entails(t, f))
    assertTrue(verifier.entails(f, f))
}
