package plaid.prelude.ast

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader

class ConstraintTest {

  /** Expanding simplifies embedded expressions. */
  @Test
  def expandEmbeddedExpr(): Unit =
    val src = """ "a" ++ "b" == "a" ++ "b" """
    val constraint = Loader.constraint(src).expand()
    assertEquals(Loader.constraint(""" "ab" == "ab" """), constraint)

  /** Expanding substitutes constraint function calls for the appropriate body with bindings. */
  @Test
  def expandCalls(): Unit =
    val fn = Loader.constraintFunc("f(x) { x == x }")
    val constraint = Loader.constraint("NOT f(5)")
    assertEquals(
      Loader.constraint("NOT 5 == 5"),
      constraint.expand(constraintCtx = List(fn)))

  /** Constraint function call parameters get expanded when the call gets expanded. */
  @Test
  def callBindingsExpanded(): Unit =
    val fn = Loader.constraintFunc("f(x) { x == x }")
    val constraint = Loader.constraint("""NOT f("a" ++ "b")""")
    assertEquals(
      Loader.constraint(""" NOT "ab" == "ab" """),
      constraint.expand(constraintCtx = List(fn)))

  /** Constraint function calls to expression functions get expanded. */
  @Test
  def constraintsExpandExprFuncs(): Unit =
    val f = Loader.exprFunc("f(i) {out@i}")
    val constraint = Loader.constraint("f(1) == 2")
    val expected = Loader.constraint("out@1 == 2")
    assertEquals(Loader.constraint("out@1 == 2"), constraint.expand(List(f)))
}
