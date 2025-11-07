package plaid.prelude.antlr

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.ast.{AssignCmd, CmdFunc, ConstraintFunc, EqualConstraint, ExprFunc, Identifier, Num, Program}

class ProgramParseTest {

  /** Parses expression functions from appropriate section. */
  @Test
  def exprFuncSection(): Unit =
    assertEquals(
      Program(
        exprFuncs = List(ExprFunc(Identifier("f"), Nil, Num(12))),
        constraintFuncs = Nil,
        cmdFuncs = Nil),
      Loader.program("exprfunctions: f() { 12 }"))

  /** Parses constraint functions from appropriate section. */
  @Test
  def constraintFuncSection(): Unit =
    assertEquals(
      Program(
        exprFuncs = Nil,
        constraintFuncs = List(ConstraintFunc(Identifier("f"), Nil, EqualConstraint(Num(12), Num(12)))),
        cmdFuncs = Nil),
      Loader.program("constraintfunctions: f() { 12 == 12 }"))

  /** Parses command functions from appropriate section. */
  @Test
  def cmdFuncSection(): Unit =
    assertEquals(
      Program(
        exprFuncs = Nil,
        constraintFuncs = Nil,
        cmdFuncs = List(CmdFunc(Identifier("f"), Nil, List(AssignCmd(Num(12), Num(12))), None, None))),
      Loader.program("cmdfunctions: f() { 12 := 12 }"))

  /** Program sections can appear in any order. */
  @Test
  def sectionOrderAgnostic(): Unit =
    val exprSection = "exprfunctions: exprfn() { 12 }"
    val constraintSection = "constraintfunctions: constraintfn() { T }"
    val cmdSection = "cmdfunctions: cmdfn() { m[\"x\"]@1 := m[\"x\"]@2 }"
    val prog1 = Loader.program(exprSection + constraintSection + cmdSection)
    val prog2 = Loader.program(constraintSection + exprSection + cmdSection)
    val prog3 = Loader.program(cmdSection + exprSection + constraintSection)
    val prog4 = Loader.program(cmdSection + constraintSection + exprSection)
    assertEquals(prog1, prog2)
    assertEquals(prog2, prog3)
    assertEquals(prog3, prog4)
}
