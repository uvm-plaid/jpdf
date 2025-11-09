package plaid.prelude.logic

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader
import plaid.prelude.logic.contract

class ContractTest {

  /** The default precondition is True. */
  @Test
  def defaultPrecondition(): Unit =
    val src = """ f() { m["x"]@1 := 4 } """
    val contract = Loader.cmdFunc(src).contract(Nil, Nil, Nil)
    val pre = Loader.constraint("T")
    assertEquals(pre, contract.pre)

  /** The default postcondition is the constraints from a function body. */
  @Test
  def defaultPostcondition(): Unit =
    val src = """ f() { m["x"]@1 := 4 } """
    val contract = Loader.cmdFunc(src).contract(Nil, Nil, Nil)
    // Maybe the T AND part will simplify away someday...
    val post = Loader.constraint(""" T AND m["x"]@1 == 4 """)
    assertEquals(post, contract.post)

  /** The postcondition from a function call is bound to the actual parameters of the call. */
  @Test
  def postconditionBound(): Unit =
    val src = """ cmdfunctions: f(i:cid) { m["x"]@1 := i } g() { f(4) } """
    val List(f, g) = Loader.program(src).cmdFuncs.contracts(Nil, Nil)
    // Maybe the T AND part will simplify away someday...
    val post = Loader.constraint(""" T AND (T AND (m["x"]@1 == 4)) """)
    assertEquals(post, f.post)
}
