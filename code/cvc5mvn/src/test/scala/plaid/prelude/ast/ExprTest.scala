package plaid.prelude.ast

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader

class ExprTest {

  /** Identifiers concatenated with literals do not simplify. */
  @Test
  def identifierConcat(): Unit =
    assertEquals(
      ConcatExpr(Identifier("i"), Str("x")),
      Loader.expression("i++\"x\""))
    assertEquals(
      ConcatExpr(Str("x"), Identifier("i")),
      Loader.expression("\"x\"++i"))

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

  @Test
  def simplifyGroupedConcat(): Unit =
    assertEquals(
      Str("xywz"),
      Loader.expression("(\"x\"++\"y\")++(\"w\"++\"z\")").expand())

  /** Expanding can bind and simplify concatenations at the same time. */
  @Test
  def substituteConcat(): Unit =
    val src = """ i ++ "a" ++ "b" ++ j """
    val map = Map(Identifier("i") -> Str("x"), Identifier("j") -> Str("y"))
    val expr = Loader.expression(src).expand(bindings = map)
    assertEquals(Str("xaby"), expr)

  /** If a let binding is unused, the expansion is the let body. */
  @Test
  def unusedLetSubstitution(): Unit =
    assertEquals(Num(3), Loader.expression("let r11 = 3 in r11").expand())

  /** The inner let body in nested let bindings can access both variables. */
  @Test
  def doubleLetSubstitution(): Unit =
    val letExpr = "let r11 = 3 in let r10 = 4 in r11 + r10"
    assertEquals(
      PlusExpr(Num(3), Num(4)),
      Loader.expression(letExpr).expand())

  /** The value of an outer let binding propagates to the body via an inner let binding. */
  @Test
  def letPropagation(): Unit =
    val letExpr = "let y = 3 in let x = y + 1 in x + y"
    assertEquals(
      PlusExpr(PlusExpr(Num(3), Num(1)), Num(3)),
      Loader.expression(letExpr).expand())

  /** An outer let binding variables is shadowed by an inner let binding. */
  @Test
  def letShadowing(): Unit =
    assertEquals(
      PlusExpr(Num(5), Num(3)),
      Loader.expression("let x = 3 in let x = 5 in x+3").expand())

  /** Call expressions expand to the called functions body, with parameter substitutions. */
  @Test
  def expandFunctionCall(): Unit =
    val f = Loader.exprFunc("f(x) { out@x }")
    val e = Loader.expression("f(1)")
    assertEquals(Loader.expression("out@1"), e.expand(List(f)))

  /** Variable substitution affects values in record expressions. */
  @Test
  def expandRecordValue(): Unit =
    val e = Loader.expression("let p = 1 in { x = p }")
    assertEquals(Loader.expression("{ x = 1 }"), e.expand())

  /** Variable substitution does not affect values in record expressions. */
  @Test
  def expandRecordKey(): Unit =
    val e = Loader.expression("let x = 1 in { x = 3 }")
    assertEquals(Loader.expression("{ x = 3 }"), e.expand())
}
