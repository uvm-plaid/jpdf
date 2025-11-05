package plaid.prelude.ast

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader

class ConstraintFuncTest {

  /** Functions with no calls have no dependencies. */
  @Test
  def dependenciesWithoutCalls(): Unit = {
    val src = "constraintfunctions: f(i) { m[\"x\"] == 3 }"
    val fn = Loader.program(src).constraintFuncs.head
    assertEquals(Set(), fn.constraintDependencies())
  }

  /** Functions consisting of a call has just that dependency. */
  @Test
  def dependenciesJustCall(): Unit = {
    val src = "constraintfunctions: f(i) { g(i) }"
    val fn = Loader.program(src).constraintFuncs.head
    assertEquals(Set(Identifier("g")), fn.constraintDependencies())
  }

}
