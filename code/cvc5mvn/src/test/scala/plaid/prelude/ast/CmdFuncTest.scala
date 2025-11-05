package plaid.prelude.ast

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader

class CmdFuncTest {

  /** Functions with no calls have no dependencies. */
  @Test
  def dependenciesWithoutCalls(): Unit = {
    val src = "cmdfunctions: f() { m[\"x\"]@1 := 3 }"
    val fn = Loader.program(src).cmdFuncs.head
    assertEquals(Set(), fn.cmdDependencies())
  }

  /** Functions consisting of a call has just that dependency. */
  @Test
  def cmdfunctions(): Unit = {
    val src = "cmdfunctions: f() { g() }"
    val fn = Loader.program(src).cmdFuncs.head
    assertEquals(Set(Identifier("g")), fn.cmdDependencies())
  }

}
