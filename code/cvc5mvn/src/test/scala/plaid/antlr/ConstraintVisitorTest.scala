package plaid.antlr

import org.junit.Test
import plaid.ast._
import org.junit.Assert.assertEquals

class ConstraintVisitorTest {

  /** converts string src code into AST */
  private def ast(src: String): Constraint = Load.constraint(src)

  /** parses equal constraints */
  @Test
  def equalExpr(): Unit = {
    val expr = ast("m[\"z\"]@1 + m[\"z\"]@2 == (m[\"x\"]@1 + m[\"x\"]@2) * (m[\"y\"]@1 + m[\"y\"]@2)")
    assertEquals(
      EqualConstraint(
        PlusExpr(
          AtExpr(MessageExpr(Str("z")), Num(1)),
          AtExpr(MessageExpr(Str("z")), Num(2))
        ),
        TimesExpr(
          PlusExpr(
            AtExpr(MessageExpr(Str("x")), Num(1)),
            AtExpr(MessageExpr(Str("x")), Num(2))
          ),
          PlusExpr(
            AtExpr(MessageExpr(Str("y")), Num(1)),
            AtExpr(MessageExpr(Str("y")), Num(2))
          )
        )
      ),
      expr
    )
  }

  /** parses And constraints */
  @Test
  def andConstraints(): Unit = {
    val expectedExpr = AndConstraint(
      EqualConstraint(
        AtExpr(MessageExpr(Str("z")), Num(1)),
        AtExpr(MessageExpr(Str("z")), Num(1))
      ),
      EqualConstraint(
        AtExpr(MessageExpr(Str("y")), Num(2)),
        AtExpr(MessageExpr(Str("y")), Num(2))
      )
    )

    val actualExpr = ast("(m[\"z\"]@1==m[\"z\"]@1) AND (m[\"y\"]@2 == m[\"y\"]@2)")
    assertEquals(expectedExpr, actualExpr)
  }

  /** parses Not constraints */
  @Test
  def notConstraints(): Unit = {
    val expectedExpr = NotConstraint(
      EqualConstraint(
        AtExpr(MessageExpr(Str("z")), Num(1)),
        AtExpr(MessageExpr(Str("z")), Num(1))
      )
    )
    val actualExpr = ast("NOT(m[\"z\"]@1==m[\"z\"]@1)")
    assertEquals(expectedExpr, actualExpr)
  }

  /** A constraint expression is an expression no matter how many parens it is wrapped in */
  @Test
  def unlimitedParens(): Unit = {
    val none = ast("m[\"x\"]@1 == m[\"x\"]@1")
    val many = ast("((((((m[\"x\"]@1 == m[\"x\"]@1))))))")
    assertEquals(none, many)
  }

  /** Parenthesis override operator precedence */
  @Test
  def parensOverride(): Unit = {
    val constraint = ast("m[\"x\"]@1 == m[\"z\"]@3 AND (m[\"x\"]@1 == m[\"y\"]@2 AND m[\"z\"]@3 == m[\"w\"]@4)")
    assertEquals(
      AndConstraint(
        EqualConstraint(AtExpr(MessageExpr(Str("x")), Num(1)), AtExpr(MessageExpr(Str("z")), Num(3))),
        AndConstraint(
          EqualConstraint(AtExpr(MessageExpr(Str("x")), Num(1)), AtExpr(MessageExpr(Str("y")), Num(2))),
          EqualConstraint(AtExpr(MessageExpr(Str("z")), Num(3)), AtExpr(MessageExpr(Str("w")), Num(4)))
        )
      ),
      constraint
    )
  }

  /** Negation comes before Logical And */
  @Test
  def negationFirst(): Unit = {
    val constraint = ast("NOT m[\"x\"]@1 == m[\"z\"]@3 AND m[\"x\"]@1 == m[\"y\"]@2")
    assertEquals(
      AndConstraint(
        NotConstraint(EqualConstraint(AtExpr(MessageExpr(Str("x")), Num(1)), AtExpr(MessageExpr(Str("z")), Num(3)))),
        EqualConstraint(AtExpr(MessageExpr(Str("x")), Num(1)), AtExpr(MessageExpr(Str("y")), Num(2)))
      ),
      constraint
    )
  }

  /** Equality comes before negation */
  @Test
  def equalityFirst(): Unit = {
    val constraint = ast("NOT m[\"x\"]@1 == m[\"z\"]@3")
    assertEquals(
      NotConstraint(EqualConstraint(AtExpr(MessageExpr(Str("x")), Num(1)), AtExpr(MessageExpr(Str("z")), Num(3)))),
      constraint
    )
  }
}
