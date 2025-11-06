package plaid.prelude.ast

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader

class ConstraintFuncTest {

  /** Functions with no calls have no dependencies. */
  @Test
  def dependenciesWithoutCalls(): Unit = {
    val fn = Loader.constraintFunc("f(i) { m[\"x\"] == 3 }")
    assertEquals(Set(), fn.constraintDependencies())
  }

  /** Functions consisting of a call has just that dependency. */
  @Test
  def dependenciesJustCall(): Unit = {
    val fn = Loader.constraintFunc("f(i) { g(i) }")
    assertEquals(Set(Identifier("g")), fn.constraintDependencies())
  }

}
