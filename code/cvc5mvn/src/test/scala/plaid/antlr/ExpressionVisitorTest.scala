package plaid.antlr

import org.junit.Test
import plaid.ast._
import scala.jdk.CollectionConverters._
import org.junit.Assert.assertEquals
import scala.collection.immutable.TreeMap

class ExpressionVisitorTest {

  private def ast(src: String): Expr = Loader.toExpression(src)

  /** String literals parse, and no quotes in the tree. */
  @Test
  def stringLiteral(): Unit = {
    assertEquals(Str("asdf"), ast("\"asdf\""))
  }

  /** An expression is an expression no matter how many parens it is wrapped in. */
  @Test
  def unlimitedParens(): Unit = {
    val none = ast("\"x\"")
    val many = ast("((((((\"x\"))))))")
    assertEquals(many, none)
  }

  /** Multiplication comes before addition. */
  @Test
  def multiplyFirst(): Unit = {
    val expr = ast("3 + 2 * 9")
    assertEquals(PlusExpr(Num(3), TimesExpr(Num(2), Num(9))), expr)
  }

  /** Negation happens before party index. */
  @Test
  def minusFirst(): Unit = {
    val expr: Node = ast("-m[z]@1")
    assertEquals(MinusExpr(AtExpr(MessageExpr(Identifier("z")), Num(1))), expr)
  }

  /** Parentheses override operator precedence. */
  @Test
  def parensOverride(): Unit = {
    val expr = ast("(3 + 2) * 9")
    assertEquals(TimesExpr(PlusExpr(Num(3), Num(2)), Num(9)), expr)
  }

  /** String concatenation. */
  @Test
  def strConcat(): Unit = {
    val expr = ast("\"a\" ++ \"b\" ++ \"c\"")
    assertEquals(ConcatExpr(ConcatExpr(Str("a"), Str("b")), Str("c")), expr)
  }

  /** Field selection has higher precedence than binary ops. */
  @Test
  def selectPrecedence(): Unit = {
    val expr = ast("8 * parent.child")
    val select = FieldSelectExpr(Identifier("parent"), Identifier("child"))
    assertEquals(TimesExpr(Num(8), select), expr)
  }

  /** Parses memory expressions. */
  @Test
  def memoryExpr(): Unit = {
    assertEquals(OutputExpr(), ast("out"))
    assertEquals(SecretExpr(Str("x")), ast("s[\"x\"]"))
    assertEquals(MessageExpr(Str("x")), ast("m[\"x\"]"))
    assertEquals(RandomExpr(Str("x")), ast("r[\"x\"]"))
    assertEquals(PublicExpr(Str("x")), ast("p[\"x\"]"))
  }

  /** Multiple party indexes get assigned correctly as siblings. */
  @Test
  def partyIndexSiblings(): Unit = {
    val expr = ast("s[\"y\"]@2 + s[\"x\"]@1")
    assertEquals(
      PlusExpr(
        AtExpr(SecretExpr(Str("y")), Num(2)),
        AtExpr(SecretExpr(Str("x")), Num(1))
      ),
      expr
    )
  }

  /** Parses let expressions. */
  @Test
  def letExpr(): Unit = {
    val expr = ast("let z = 1 in 2 + z")
    assertEquals(
      LetExpr(Identifier("z"), Num(1), PlusExpr(Num(2), Identifier("z"))),
      expr
    )
  }

  /** Function calls can have zero, one, or multiple parameters. */
  @Test
  def functionCall(): Unit = {
    assertEquals(FunctionCall(Identifier("f"), List()), ast("f()"))
    assertEquals(FunctionCall(Identifier("f"), List(Num(0))), ast("f(0)"))
    assertEquals(FunctionCall(Identifier("f"), List(Num(0), Identifier("x"))), ast("f(0, x)"))
  }

  /** Fields can have zero, one, or multiple members. */
  @Test
  def fieldExpr(): Unit = {
    assertEquals(FieldExpr(new java.util.TreeMap(Map().asJava)), ast("{}"))
    assertEquals(FieldExpr(new java.util.TreeMap(Map(Identifier("a") -> Num(0)).asJava)), ast("{a=0}"))
    assertEquals(
      FieldExpr(new java.util.TreeMap(Map(Identifier("a") -> Num(0), Identifier("b") -> Num(1)).asJava)),
      ast("{a=0; b=1}")
    )
  }

  /** Identifiers can be mixed case. */
  @Test
  def mixedCaseIdentifiers(): Unit = {
    assertEquals(Identifier("aA"), ast("aA"))
  }

  /** Identifiers can start with underscores. */
  @Test
  def underscoreIdentifiers(): Unit = {
    assertEquals(Identifier("_1"), ast("_1"))
  }

  @Test
  def identifierEquality(): Unit = {
    assertEquals(Identifier("r11"), Identifier("r11"))
  }

  /** Parses OT expression */
  @Test
  def otExpr(): Unit = {
    assertEquals(
      OTExpr(
        SecretExpr(Str("foo")),
        Num(1),
        MessageExpr(Str("bar")),
        MessageExpr(Str("zoo"))
      ),
      ast("OT(s[\"foo\"]@1, m[\"bar\"], m[\"zoo\"])")
    )
  }

  /** Parses OT4 expression */
  @Test
  def otFourExpr(): Unit = {
    assertEquals(
      OTFourExpr(
        MessageExpr(Str("s1")),
        MessageExpr(Str("s2")),
        Num(1),
        SecretExpr(Str("x")),
        SecretExpr(Str("y")),
        SecretExpr(Str("z")),
        SecretExpr(Str("t"))
      ),
      ast("OT4((m[\"s1\"], m[\"s2\"])@1, s[\"x\"], s[\"y\"], s[\"z\"], s[\"t\"])")
    )
  }

}
