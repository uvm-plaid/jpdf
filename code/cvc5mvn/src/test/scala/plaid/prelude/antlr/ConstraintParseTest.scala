package plaid.prelude.antlr

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.ast.*

class ConstraintParseTest {

  /** Parses equality constraints. */
  @Test
  def equalExpr(): Unit =
    assertEquals(
      EqualConstraint(
        PlusExpr(
          AtExpr(MessageExpr(Str("z")), Num(1)),
          AtExpr(MessageExpr(Str("z")), Num(2))),
        TimesExpr(
          PlusExpr(
            AtExpr(MessageExpr(Str("x")), Num(1)),
            AtExpr(MessageExpr(Str("x")), Num(2))),
          PlusExpr(
            AtExpr(MessageExpr(Str("y")), Num(1)),
            AtExpr(MessageExpr(Str("y")), Num(2))))),
      Loader.constraint("m[\"z\"]@1 + m[\"z\"]@2 == (m[\"x\"]@1 + m[\"x\"]@2) * (m[\"y\"]@1 + m[\"y\"]@2)"))

  /** parses And constraints */
  @Test
  def andConstraints(): Unit =
    val expectedExpr = AndConstraint(
      EqualConstraint(
        AtExpr(MessageExpr(Str("z")), Num(1)),
        AtExpr(MessageExpr(Str("z")), Num(1))),
      EqualConstraint(
        AtExpr(MessageExpr(Str("y")), Num(2)),
        AtExpr(MessageExpr(Str("y")), Num(2))))
    val actualExpr = Loader.constraint("(m[\"z\"]@1==m[\"z\"]@1) AND (m[\"y\"]@2 == m[\"y\"]@2)")
    assertEquals(expectedExpr, actualExpr)

  /** Parses negation constraints */
  @Test
  def notConstraints(): Unit =
    val expectedExpr = NotConstraint(
      EqualConstraint(
        AtExpr(MessageExpr(Str("z")), Num(1)),
        AtExpr(MessageExpr(Str("z")), Num(1))))
    val actualExpr = Loader.constraint("NOT(m[\"z\"]@1==m[\"z\"]@1)")
    assertEquals(expectedExpr, actualExpr)

  /** A constraint expression is an expression no matter how many parens it is wrapped in. */
  @Test
  def unlimitedParens(): Unit =
    val none = Loader.constraint("m[\"x\"]@1 == m[\"x\"]@1")
    val many = Loader.constraint("((((((m[\"x\"]@1 == m[\"x\"]@1))))))")
    assertEquals(none, many)

  /** Parenthesis override operator precedence. */
  @Test
  def parensOverride(): Unit =
    assertEquals(
      AndConstraint(
        EqualConstraint(AtExpr(MessageExpr(Str("x")), Num(1)), AtExpr(MessageExpr(Str("z")), Num(3))),
        AndConstraint(
          EqualConstraint(AtExpr(MessageExpr(Str("x")), Num(1)), AtExpr(MessageExpr(Str("y")), Num(2))),
          EqualConstraint(AtExpr(MessageExpr(Str("z")), Num(3)), AtExpr(MessageExpr(Str("w")), Num(4))))),
      Loader.constraint("m[\"x\"]@1 == m[\"z\"]@3 AND (m[\"x\"]@1 == m[\"y\"]@2 AND m[\"z\"]@3 == m[\"w\"]@4)"))

  /** Negation binds tighter than conjunction. */
  @Test
  def negationFirst(): Unit =
    assertEquals(
      AndConstraint(
        NotConstraint(EqualConstraint(AtExpr(MessageExpr(Str("x")), Num(1)), AtExpr(MessageExpr(Str("z")), Num(3)))),
        EqualConstraint(AtExpr(MessageExpr(Str("x")), Num(1)), AtExpr(MessageExpr(Str("y")), Num(2)))),
      Loader.constraint("NOT m[\"x\"]@1 == m[\"z\"]@3 AND m[\"x\"]@1 == m[\"y\"]@2"))

  /** Equality binds tighter than negation. */
  @Test
  def equalityFirst(): Unit =
    assertEquals(
      NotConstraint(EqualConstraint(AtExpr(MessageExpr(Str("x")), Num(1)), AtExpr(MessageExpr(Str("z")), Num(3)))),
      Loader.constraint("NOT m[\"x\"]@1 == m[\"z\"]@3"))
}
