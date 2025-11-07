package plaid.prelude.antlr

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.ast.{AtExpr, CallExpr, ConcatExpr, FieldExpr, FieldSelectExpr, Identifier, LetExpr, MessageExpr, MinusExpr, Node, Num, OutputExpr, PlusExpr, PublicExpr, RandomExpr, SecretExpr, Str, TimesExpr}

class ExpressionParseTest {

  /** String literals parse, and no quotes in the tree. */
  @Test
  def stringLiteral(): Unit =
    assertEquals(Str("asdf"), Loader.expression("\"asdf\""))

  /** An expression is an expression no matter how many parens it is wrapped in. */
  @Test
  def unlimitedParens(): Unit =
    val none = Loader.expression("\"x\"")
    val many = Loader.expression("((((((\"x\"))))))")
    assertEquals(many, none)

  /** Multiplication comes before addition. */
  @Test
  def multiplyFirst(): Unit =
    val expr = Loader.expression("3 + 2 * 9")
    assertEquals(PlusExpr(Num(3), TimesExpr(Num(2), Num(9))), expr)

  /** Negation happens before party index. */
  @Test
  def minusFirst(): Unit =
    val expr: Node = Loader.expression("-m[z]@1")
    assertEquals(MinusExpr(AtExpr(MessageExpr(Identifier("z")), Num(1))), expr)

  /** Parentheses override operator precedence. */
  @Test
  def parensOverride(): Unit =
    val expr = Loader.expression("(3 + 2) * 9")
    assertEquals(TimesExpr(PlusExpr(Num(3), Num(2)), Num(9)), expr)

  /** String concatenation. */
  @Test
  def strConcat(): Unit =
    val expr = Loader.expression("\"a\" ++ \"b\" ++ \"c\"")
    assertEquals(ConcatExpr(ConcatExpr(Str("a"), Str("b")), Str("c")), expr)

  /** Field selection has higher precedence than binary ops. */
  @Test
  def selectPrecedence(): Unit =
    val expr = Loader.expression("8 * parent.child")
    val select = FieldSelectExpr(Identifier("parent"), Identifier("child"))
    assertEquals(TimesExpr(Num(8), select), expr)

  /** Parses memory expressions. */
  @Test
  def memoryExpr(): Unit =
    assertEquals(OutputExpr(), Loader.expression("out"))
    assertEquals(SecretExpr(Str("x")), Loader.expression("s[\"x\"]"))
    assertEquals(MessageExpr(Str("x")), Loader.expression("m[\"x\"]"))
    assertEquals(RandomExpr(Str("x")), Loader.expression("r[\"x\"]"))
    assertEquals(PublicExpr(Str("x")), Loader.expression("p[\"x\"]"))

  /** Multiple party indexes get assigned correctly as siblings. */
  @Test
  def partyIndexSiblings(): Unit =
    val expr = Loader.expression("s[\"y\"]@2 + s[\"x\"]@1")
    assertEquals(
      PlusExpr(
        AtExpr(SecretExpr(Str("y")), Num(2)),
        AtExpr(SecretExpr(Str("x")), Num(1))),
      Loader.expression("s[\"y\"]@2 + s[\"x\"]@1"))

  /** Parses let expressions. */
  @Test
  def letExpr(): Unit =
    assertEquals(
      LetExpr(Identifier("z"), Num(1), PlusExpr(Num(2), Identifier("z"))),
      Loader.expression("let z = 1 in 2 + z"))

  /** Function calls can have zero, one, or multiple parameters. */
  @Test
  def functionCall(): Unit =
    assertEquals(CallExpr(Identifier("f"), List()), Loader.expression("f()"))
    assertEquals(CallExpr(Identifier("f"), List(Num(0))), Loader.expression("f(0)"))
    assertEquals(CallExpr(Identifier("f"), List(Num(0), Identifier("x"))), Loader.expression("f(0, x)"))

  /** Fields can have zero, one, or multiple members. */
  @Test
  def fieldExpr(): Unit =
    assertEquals(FieldExpr(Map()), Loader.expression("{}"))
    assertEquals(FieldExpr(Map(Identifier("a") -> Num(0))), Loader.expression("{a=0}"))
    assertEquals(
      FieldExpr(Map(Identifier("a") -> Num(0), Identifier("b") -> Num(1))),
      Loader.expression("{a=0; b=1}"))

  /** Identifiers can be mixed case. */
  @Test
  def mixedCaseIdentifiers(): Unit =
    assertEquals(Identifier("aA"), Loader.expression("aA"))

  /** Identifiers can start with underscores. */
  @Test
  def underscoreIdentifiers(): Unit =
    assertEquals(Identifier("_1"), Loader.expression("_1"))

  @Test
  def identifierEquality(): Unit =
    assertEquals(Identifier("r11"), Identifier("r11"))
}
