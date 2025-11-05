package plaid.prelude.ast

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader

class ExprTest {

  /** Expanding simplifies the concatenation of two string literals. */
  @Test
  def simplifyLiteralConcat(): Unit =
    val src = """ "a" ++ "b" """
    val expr = Loader.expression(src).expand()
    assertEquals(Loader.expression(""" "ab" """), expr)

}
