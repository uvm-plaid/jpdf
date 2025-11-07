package plaid.prelude.ast

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader

class CmdFuncTest {

  /** Functions with no calls have no dependencies. */
  @Test
  def dependenciesWithoutCalls(): Unit =
    val fn = Loader.cmdFunc("f() { m[\"x\"]@1 := 3 }")
    assertEquals(Set(), fn.cmdDependencies())

  /** Functions consisting of a call has just that dependency. */
  @Test
  def dependenciesJustCall(): Unit =
    val fn = Loader.cmdFunc("f() { g() }")
    assertEquals(Set(Identifier("g")), fn.cmdDependencies())
}
