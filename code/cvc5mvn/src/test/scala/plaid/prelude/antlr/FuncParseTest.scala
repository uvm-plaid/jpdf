package plaid.prelude.antlr

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.ast.{AssignCmd, AtExpr, CmdFunc, EqualConstraint, ExprFunc, Identifier, Num, OutputExpr, PartyIndexType, StringType, TrueConstraint, TypedIdentifier}

class FuncParseTest {

  /** Parses expression functions of zero, one, and multiple parameters. */
  @Test
  def exprFunctions(): Unit =
    val f = ExprFunc(Identifier("f"), List(), Num(0))
    val g = ExprFunc(Identifier("g"), List(Identifier("x")), Num(1))
    val h = ExprFunc(Identifier("h"), List(Identifier("x"), Identifier("y")), Num(2))
    assertEquals(f, Loader.exprFunc("f() { 0 }"))
    assertEquals(g, Loader.exprFunc("g(x) { 1 }"))
    assertEquals(h, Loader.exprFunc("h(x, y) { 2 }"))

  /** Parses command functions of zero, one, and multiple parameters. */
  @Test
  def commandFunctions(): Unit =
    val body = List(AssignCmd(OutputExpr(), Num(2)))
    val f = CmdFunc(Identifier("f"), Nil, body, None, None)
    val g = CmdFunc(Identifier("g"),
      List(TypedIdentifier(Identifier("x"), StringType())), body, None, None)
    val h = CmdFunc(Identifier("h"),
      List(TypedIdentifier(Identifier("x"), StringType()), TypedIdentifier(Identifier("i"), PartyIndexType())),
      body, None, None)

    assertEquals(f, Loader.cmdFunc("f() { out := 2 }"))
    assertEquals(g, Loader.cmdFunc("g(x : string) { out := 2 }"))
    assertEquals(h, Loader.cmdFunc("h(x : string, i : cid) { out := 2 }"))

  /** Parses command function with precondition and postcondition. */
  @Test
  def commandFunctionWithConstraints(): Unit =
    assertEquals(
      CmdFunc(
        Identifier("f"),
        Nil,
        List(AssignCmd(AtExpr(OutputExpr(), Num(1)), AtExpr(Num(3), Num(1)))),
        Some(TrueConstraint()),
        Some(EqualConstraint(AtExpr(OutputExpr(), Num(1)), Num(3)))),
      Loader.cmdFunc("precondition: (T) f() {out@1:=3@1} postcondition: (out@1 == 3)"))

  /** Parses command function without a precondition or postcondition. */
  @Test
  def commandFunctionWithoutConstraints(): Unit =
    assertEquals(
      CmdFunc(
        Identifier("f"), Nil,
        List(AssignCmd(AtExpr(OutputExpr(), Num(1)), AtExpr(Num(3), Num(1)))),
        None, None),
      Loader.cmdFunc("f() {out@1:=3@1}"))
}
