package plaid.prelude.ast

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader

class ConstraintTest {

  /** Expanding simplifies embedded expressions. */
  @Test
  def expandPreAndPost(): Unit =
    val src = """ "ab" == "a" ++ "b" """
    val constraint = Loader.constraint(src).expand()
    assertEquals(Loader.constraint(""" "ab" == "ab" """), constraint)

  /** Expanding substitutes constraint function calls for the appropriate body with bindings. */
  @Test
  def expandCalls(): Unit =
    val fn = Loader.program("constraintfunctions: f(x) { x == x }").constraintFuncs.head
    val constraint = Loader.constraint("NOT f(5)")
    assertEquals(Loader.constraint(""" NOT 5 == 5 """), constraint.expand(constraintCtx = List(fn)))

  /** Constraint function call parameters get expanded when the call gets expanded. */
  @Test
  def callBindingsExpanded(): Unit =
    val fn = Loader.program("constraintfunctions: f(x) { x == x }").constraintFuncs.head
    val constraint = Loader.constraint("""NOT f("a" ++ "b")""")
    assertEquals(Loader.constraint(""" NOT "ab" == "ab" """), constraint.expand(constraintCtx = List(fn)))
}
