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

}
