package plaid.prelude.logic

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader
import plaid.prelude.logic.contract

class ContractTest {

  /** The default postcondition is the constraints from a function body. */
  @Test
  def defaultPostcondition(): Unit =
    val src = """ cmdfunctions: f() { m["x"]@1 := 4 } """
    val contract = Loader.program(src).cmdFuncs.head.contract(Nil)
    // Maybe the T AND part will simplify away someday...
    val post = Loader.constraint(""" T AND m["x"]@1 == 4 """)
    assertEquals(post, contract.post)

  /** The postcondition from a function call is bound to the actual parameters of the call. */
  @Test
  def postconditionBound(): Unit =
    val src = """ cmdfunctions: f(i:cid) { m["x"]@1 := i } g() { f(4) } """
    val List(f, g) = Loader.program(src).cmdFuncs.contracts()
    // Maybe the T AND part will simplify away someday...
    val post = Loader.constraint(""" T AND (T AND (m["x"]@1 == 4)) """)
    assertEquals(post, f.post)

  /**
   * Constraint functions calls with parameters in the postcondition fold into
   * the postcondition correctly.
   */
  @Test
  def postconditionCallSubstitution(): Unit =
    val constraintFn = Loader.program("""constraintfunctions: f(i) { i ++ "x" == "ax" } """).constraintFuncs.head
    val cmdFn = Loader.program("""cmdfunctions: g() { m["x"]@1 := 4 } postcondition: (f("a")) """).cmdFuncs.head
    val expanded = cmdFn.expand(constraintCtx = List(constraintFn))
    val contract = expanded.contract(Nil)
    val post = Loader.constraint(""" "ax" == "ax" """)
    assertEquals(post, contract.post)

}
