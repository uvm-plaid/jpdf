package plaid.prelude.logic

import org.junit.Assert.{assertFalse, assertTrue}
import org.junit.Test
import plaid.prelude.antlr.Loader
import plaid.prelude.ast.Constraint
import plaid.prelude.cvc.TermFactory

class VerifierTest {

  private def satisfiable(src: String): Boolean =
    satisfiable(Loader.constraint(src))

  private def satisfiable(constraint: Constraint): Boolean =
    val factory = TermFactory("7")
    val e = factory.toTerm(constraint)
    factory.satisfiable(e)

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
    val factory = TermFactory("7")
    val t = factory.toTerm(Loader.constraint("T"))
    val f = factory.toTerm(Loader.constraint("NOT T"))
    assertTrue(factory.entails(t, t))
    assertTrue(factory.entails(f, t))

  /** Nothing entails an unsatisfiable constraint except another unsatisfiable constraint. */
  @Test
  def nothingEntails(): Unit =
    val factory = TermFactory("7")
    val t = factory.toTerm(Loader.constraint("T"))
    val f = factory.toTerm(Loader.constraint("NOT T"))
    assertFalse(factory.entails(t, f))
    assertTrue(factory.entails(f, f))
}
