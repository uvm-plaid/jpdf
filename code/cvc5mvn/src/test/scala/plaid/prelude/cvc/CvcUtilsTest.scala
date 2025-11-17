package plaid.prelude.cvc

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.ast.*

class CvcUtilsTest {

  /** Produces cvc5 constant names for all memory types. */
  @Test
  def memNames(): Unit =
    assertEquals("m_x_4", CvcUtils.getCvcName(MessageExpr(Str("x")), Some(4)))
    assertEquals("o_4", CvcUtils.getCvcName(OutputExpr(), Some(4)))
    assertEquals("p_x", CvcUtils.getCvcName(PublicExpr(Str("x")), Some(4)))
    assertEquals("r_x_4", CvcUtils.getCvcName(RandomExpr(Str("x")), Some(4)))
    assertEquals("s_x_4", CvcUtils.getCvcName(SecretExpr(Str("x")), Some(4)))

  /** Party index is required for message memory names. */
  @Test(expected = classOf[Exception])
  def messageNoParty(): Unit =
    assertEquals("m_x_4", CvcUtils.getCvcName(MessageExpr(Str("x")), None))

  /** Party index is required for output memory names. */
  @Test(expected = classOf[Exception])
  def outputNoParty(): Unit =
    assertEquals("o_4", CvcUtils.getCvcName(OutputExpr(), None))

  /** Party index is required for random memory names. */
  @Test(expected = classOf[Exception])
  def randomNoParty(): Unit =
    assertEquals("r_x_4", CvcUtils.getCvcName(RandomExpr(Str("x")), None))

  /** Party index is required for secret memory names. */
  @Test(expected = classOf[Exception])
  def secretNoParty(): Unit =
    assertEquals("s_x_4", CvcUtils.getCvcName(SecretExpr(Str("x")), None))

  /** Number nodes in the AST evaluate to integers. */
  @Test
  def intEval(): Unit =
    val num = Num(3)
    assertEquals(3, CvcUtils.toInt(num))

  /** Nodes in the AST that are not numbers do not evaluate to integers. */
  @Test(expected = classOf[Exception])
  def intEvalUnsupported(): Unit =
    CvcUtils.toInt(Str("x"))

  /** String nodes in the AST evaluate to strings. */
  @Test
  def stringEval(): Unit =
    val str = Str("x")
    assertEquals("x", CvcUtils.toString(str))

  /** Nodes in the AST that are not strings do not evaluate to strings. */
  @Test(expected = classOf[Exception])
  def stringEvalUnsupported(): Unit =
    assertEquals("3", CvcUtils.toString(Num(3)))
}
