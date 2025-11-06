package plaid.prelude.ast

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader
import plaid.prelude.ast.ListExprFuncExt.dependencyOrdered

class ExprFuncTest {

  /** Functions with no calls have no dependencies. */
  @Test
  def dependenciesWithoutCalls(): Unit =
    val fn = Loader.exprFunc("f(i) { m[\"x\" ++ i] }")
    assertEquals(Set(), fn.exprDependencies())

  /** Functions consisting of a call has just that dependency. */
  @Test
  def dependenciesJustCall(): Unit =
    val fn = Loader.exprFunc("f(i) { g(i) }")
    assertEquals(Set(Identifier("g")), fn.exprDependencies())

  /** A chain of functions that call each other have the correct forward dependency order when sorted. */
  @Test
  def dependencyOrderChain(): Unit =
    val f = Loader.exprFunc("f() { g() }")
    val g = Loader.exprFunc("g() { h() }")
    val h = Loader.exprFunc("h() { 0 }")
    val sortedOrder = List(h, g, f)
    assertEquals(sortedOrder, List(f, g, h).dependencyOrdered())
    assertEquals(sortedOrder, List(g, f, h).dependencyOrdered())
    assertEquals(sortedOrder, List(h, f, g).dependencyOrdered())
    assertEquals(sortedOrder, List(h, g, f).dependencyOrdered())

  /** A function that has multiple dependencies appears after both when sorted. */
  @Test
  def dependencyOrderMultiple(): Unit =
    val f = Loader.exprFunc("f() { g() ++ h() }")
    val g = Loader.exprFunc("g() { 0 }")
    val h = Loader.exprFunc("h() { 0 }")
    assertEquals(f, List(f, g, h).dependencyOrdered().last)
    assertEquals(f, List(g, f, h).dependencyOrdered().last)
    assertEquals(f, List(h, f, g).dependencyOrdered().last)
    assertEquals(f, List(h, g, f).dependencyOrdered().last)

}
