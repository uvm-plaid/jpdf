package plaid.antlr

import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.junit.Test
import plaid.ast._
import scala.jdk.CollectionConverters._
import org.junit.Assert.assertEquals

class FunctionListenerTest {

  private def assertFunctions(
    src: String,
    exprFunctions: List[ExprFunction],
    commandFunctions: List[CommandFunction]
  ): Unit = {
    val listener = FunctionListener()
    val tree: ParseTree = Loader.createParser(src).program()
    ParseTreeWalker().walk(listener, tree)

    assertEquals(exprFunctions, listener.exprFunctions)
    assertEquals(commandFunctions, listener.commandFunctions)
  }

  /** Parses expression functions of zero, one, and multiple parameters. */
  @Test
  def exprFunctions(): Unit = {
    val f = ExprFunction(Identifier("f"), List(), Num(0))
    val g = ExprFunction(Identifier("g"), List(Identifier("x")), Num(1))
    val h = ExprFunction(Identifier("h"), List(Identifier("x"), Identifier("y")), Num(2))

    assertFunctions(
      "exprfunctions: f() { 0 } g(x) { 1 } h(x, y) { 2 }",
      List(f, g, h),
      List()
    )
  }

  /** Parses command functions of zero, one, and multiple parameters. */
  @Test
  def commandFunctions(): Unit = {
    val f = CommandFunction(
      Identifier("f"),
      List(),
      AssignCmd(OutputExpr(), Num(2)),
      null,
      null
    )

    val g = CommandFunction(
      Identifier("g"),
      List(TypedIdentifier(Identifier("x"), StringType())),
      AssignCmd(OutputExpr(), Num(2)),
      null,
      null
    )

    val h = CommandFunction(
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
    val f = ExprFunction(Identifier("f"), List(), Num(3))
    val g = CommandFunction(Identifier("g"), List(), AssignCmd(OutputExpr(), Num(2)), null, null)

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

    val f = CommandFunction(
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
}
