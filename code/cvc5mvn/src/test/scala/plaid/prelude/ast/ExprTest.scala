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

  /** Expanding simplifies chains of concatenated string literals. */
  @Test
  def chainedLiteralConcat(): Unit =
    val src = """ "a" ++ "b" ++ "c" ++ "d" """
    val expr = Loader.expression(src).expand()
    assertEquals(Loader.expression(""" "abcd" """), expr)

  /** Expanding simplifies mixes of literals and other expressions. */
  @Test
  def mixedConcat(): Unit =
    val src = """ i ++ "a" ++ "b" ++ j """
    val expr = Loader.expression(src).expand()
    assertEquals(Loader.expression(""" i ++ "ab" ++ j """), expr)

  /** Expanding can bind and simplify concatenations at the same time. */
  @Test
  def substituteConcat(): Unit =
    val src = """ i ++ "a" ++ "b" ++ j """
    val map = Map(Identifier("i") -> Str("x"), Identifier("j") -> Str("y"))
    val expr = Loader.expression(src).expand(bindings = map)
    assertEquals(Str("xaby"), expr)
}
