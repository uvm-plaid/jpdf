package plaid.prelude.ast

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader

class ExprTest {

  /** Functions with no calls have no dependencies. */
  @Test
  def dependenciesWithoutCalls(): Unit = {
    val src = "exprfunctions: f(i) { m[\"x\" ++ i] }"
    val fn = Loader.program(src).exprFuncs.head
    assertEquals(Set(), fn.exprDependencies())
  }

  /** Functions consisting of a call has just that dependency. */
  @Test
  def dependenciesJustCall(): Unit = {
    val src = "exprfunctions: f(i) { g(i) }"
    val fn = Loader.program(src).exprFuncs.head
    assertEquals(Set(Identifier("g")), fn.exprDependencies())
  }

}
