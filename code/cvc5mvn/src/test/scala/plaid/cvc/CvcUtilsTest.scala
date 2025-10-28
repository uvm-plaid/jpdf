package plaid.cvc

import org.junit.Test
import plaid.ast._
import org.junit.Assert.assertEquals

class CvcUtilsTest {

  /** Produces cvc5 constant names for all memory types. */
  @Test
  def memNames(): Unit = {
    assertEquals("m_x_4", CvcUtils.getCvcName(MessageExpr(Str("x")), 4))
    assertEquals("o_4", CvcUtils.getCvcName(OutputExpr(), 4))
    assertEquals("p_x", CvcUtils.getCvcName(PublicExpr(Str("x")), 4))
    assertEquals("r_x_4", CvcUtils.getCvcName(RandomExpr(Str("x")), 4))
    assertEquals("s_x_4", CvcUtils.getCvcName(SecretExpr(Str("x")), 4))
  }

  /** Number nodes in the AST evalConstraint to integers. */
  @Test
  def intEval(): Unit = {
    val num = Num(3)
    assertEquals(3, CvcUtils.toInt(num))
  }

  /** Nodes in the AST that are not numbers do not evalConstraint to integers. */
  @Test(expected = classOf[IllegalArgumentException])
  def intEvalUnsupported(): Unit = {
    CvcUtils.toInt(Str("x"))
  }

  /** String nodes in the AST evalConstraint to strings. */
  @Test
  def stringEval(): Unit = {
    val str = Str("x")
    assertEquals("x", CvcUtils.toString(str))
  }

  /** Nodes in the AST that are not strings do not evalConstraint to strings. */
  @Test(expected = classOf[IllegalArgumentException])
  def stringEvalUnsupported(): Unit = {
    CvcUtils.toString(Num(3))
  }
}
