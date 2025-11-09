package plaid.prelude.logic

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader
import plaid.prelude.ast.{AndConstraint, AtExpr, EqualConstraint, Num, OutputExpr, TrueConstraint}
import plaid.prelude.logic.contract

class ContractTest {

  /** Assignment commands add equality constraints to the Hoare context. */
  @Test
  def assignmentAddsEqualityConstraint(): Unit =
    val cmd = Loader.command("out@1 := 4")
    val ctx = HoareContext().include(cmd, Nil)
    val expected = AndConstraint(TrueConstraint(), EqualConstraint(AtExpr(OutputExpr(), Num(1)), Num(4)))
    assertEquals(expected, ctx.cons)

  /** Assert commands add entailments to the Hoare context. */
  @Test
  def assertAddsEntailment(): Unit =
    val cmd = Loader.command("assert (4 = 4)@1")
    val ctx = HoareContext().include(cmd, Nil)
    val constraint = EqualConstraint(Num(4), Num(4))
    val expected = List(Entailment(cmd, TrueConstraint(), constraint))
    assertEquals(expected, ctx.ent)

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
