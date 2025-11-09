package plaid.prelude.antlr

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.ast.*

class CmdParseTest {

  /** Parses assignment commands. */
  @Test
  def assignCommand(): Unit =
    assertEquals(
      AssignCmd(AtExpr(OutputExpr(), Num(1)), Num(3)),
      Loader.command("out@1 := 3"))

  /** Functions can be called with zero, one, or multiple actual parameters. */
  @Test
  def functionCall(): Unit =
    assertEquals(
      CallCmd(Identifier("f"), Nil),
      Loader.command("f()"))
    assertEquals(
      CallCmd(Identifier("f"), List(Num(0))),
      Loader.command("f(0)"))
    assertEquals(
      CallCmd(Identifier("f"), List(Num(0), Identifier("x"))),
      Loader.command("f(0, x)"))

  /** Parses assert commands. */
  @Test
  def assertCommand(): Unit =
    assertEquals(
      AssertCmd(MessageExpr(Str("x")), MessageExpr(Str("y")), Num(5)),
      Loader.command("""assert (m["x"] = m["y"])@5"""))

  /** Parses let commands. */
  @Test
  def letCommand(): Unit =
    assertEquals(
      LetCmd(Identifier("x"), Num(4), List(
        AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x")))),
      Loader.command("let x = 4 in out@1 := x"))

  /** Let commands can have multiple other commands inside them. */
  @Test
  def letCommandScope(): Unit =
    assertEquals(
      LetCmd(Identifier("x"), Num(4), List(
          AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x")),
          AssignCmd(AtExpr(OutputExpr(), Num(2)), Identifier("x")))),
      Loader.command("let x = 4 in out@1 := x; out@2 := x"))

  /** Full line comments prevent commands from being parsed. */
  @Test
  def fullLineComments(): Unit =
    assertEquals(
      LetCmd(Identifier("x"), Num(4), List(
        AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x")),
        AssignCmd(AtExpr(OutputExpr(), Num(2)), Identifier("y")))),
      Loader.command("let x = 4 in out@1 := x;\n//out@3 := z;\nout@2 := y"))

  /** Partial line comments prevent commands from being parsed. */
  @Test
  def partialLineComments(): Unit =
    assertEquals(
      AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x")),
      Loader.command("out@1 := x//@1"))
}
