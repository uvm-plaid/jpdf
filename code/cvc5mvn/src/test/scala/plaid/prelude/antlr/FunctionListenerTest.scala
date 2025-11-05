package plaid.prelude.antlr

import org.antlr.v4.runtime.tree.{ParseTree, ParseTreeWalker}
import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.ast.*

import scala.jdk.CollectionConverters.*

class FunctionListenerTest {

  private def assertFunctions(
                               src: String,
                               exprFunctions: List[ExprFunc],
                               commandFunctions: List[CmdFunc]
  ): Unit = {
    val listener = FunctionListener()
    val tree: ParseTree = Loader.parser(src).program()
    ParseTreeWalker().walk(listener, tree)

    assertEquals(exprFunctions, listener.exprFunctions)
    assertEquals(commandFunctions, listener.commandFunctions)
  }

  /** Parses expression functions of zero, one, and multiple parameters. */
  @Test
  def exprFunctions(): Unit = {
    val f = ExprFunc(Identifier("f"), List(), Num(0))
    val g = ExprFunc(Identifier("g"), List(Identifier("x")), Num(1))
    val h = ExprFunc(Identifier("h"), List(Identifier("x"), Identifier("y")), Num(2))

    assertFunctions(
      "exprfunctions: f() { 0 } g(x) { 1 } h(x, y) { 2 }",
      List(f, g, h),
      List()
    )
  }
/*
  /** Parses command functions of zero, one, and multiple parameters. */
  @Test
  def commandFunctions(): Unit = {
    val f = CmdFunc(
      Identifier("f"),
      List(),
      AssignCmd(OutputExpr(), Num(2)),
      null,
      null
    )

    val g = CmdFunc(
      Identifier("g"),
      List(TypedIdentifier(Identifier("x"), StringType())),
      AssignCmd(OutputExpr(), Num(2)),
      null,
      null
    )

    val h = CmdFunc(
      Identifier("h"),
      List(
        TypedIdentifier(Identifier("x"), StringType()),
        TypedIdentifier(Identifier("i"), PartyIndexType())
      ),
      AssignCmd(OutputExpr(), Num(2)),
      null,
      null
    )

    assertFunctions(
      "cmdfunctions: f() { out := 2 } g(x : string) { out := 2 } h(x : string, i : cid) { out := 2 }",
      List(),
      List(f, g, h)
    )
  }

  /** Expression functions and command functions can come in any order. */
  @Test
  def orderAgnostic(): Unit = {
    val f = ExprFunc(Identifier("f"), List(), Num(3))
    val g = CmdFunc(Identifier("g"), List(), AssignCmd(OutputExpr(), Num(2)), null, null)

    assertFunctions(
      "exprfunctions: f() { 3 } cmdfunctions: g() { out := 2 }",
      List(f),
      List(g)
    )
  }

  /** Parses command function with precondition and postcondition */
  @Test
  def commandFunctionWithConstraints(): Unit = {
    val src = "cmdfunctions: precondition: (T) f() {out@1:=3@1} postcondition: (out@1 == 3)"

    val f = CmdFunc(
      Identifier("f"),
      List(),
      AssignCmd(
        AtExpr(OutputExpr(), Num(1)),
        AtExpr(Num(3), Num(1))
      ),
      TrueConstraint(),
      EqualConstraint(AtExpr(OutputExpr(), Num(1)), Num(3))
    )

    assertFunctions(src, List(), List(f))
  }
 */
}
