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

  /** When a function gets expanded, so do its preconditions, postconditions, and body. */
  @Test
  def expandComponents(): Unit =
    val src ="""
      precondition: ("ab" == "a" ++ "b")
      f() { m["u" ++ "v"]@1 := 3 }
      postcondition: ("y" ++ "z" == "yz") """

    val fn = Loader.cmdFunc(src).expand()
    assertEquals(Loader.constraint(""" "ab" == "ab" """), fn.precond.get)
    assertEquals(Loader.constraint(""" "yz" == "yz" """), fn.postcond.get)
    assertEquals(Loader.command(""" m["uv"]@1 := 3 """), fn.body.head)
}
