package plaid.prelude.antlr

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.ast.*

class CommandVisitorTest {
  private def ast(src: String): Cmd = Loader.command(src)

  /** Parses assignment commands. */
  @Test
  def assignCommand(): Unit =
    assertEquals(
      AssignCmd(AtExpr(OutputExpr(), Num(1)), Num(3)),
      ast("out@1 := 3"))

  /** Functions can be called with zero, one, or multiple actual parameters. */
  @Test
  def functionCall(): Unit =
    assertEquals(
      CallCmd(Identifier("f"), List()),
      ast("f()"))
    assertEquals(
      CallCmd(Identifier("f"), List(Num(0))),
      ast("f(0)"))
    assertEquals(
      CallCmd(Identifier("f"), List(Num(0), Identifier("x"))),
      ast("f(0, x)"))

  /** Parses assert commands. */
  @Test
  def assertCommand(): Unit =
    assertEquals(
      AssertCmd(MessageExpr(Str("x")), MessageExpr(Str("y")), Num(5)),
      ast("""assert (m["x"] = m["y"])@5"""))

  /** Parses let commands. */
  @Test
  def letCommand(): Unit =
    assertEquals(
      LetCmd(Identifier("x"), Num(4), List(
        AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x")))),
      ast("let x = 4 in out@1 := x"))

  /** Let commands can have multiple other commands inside them. */
  @Test
  def letCommandScope(): Unit =
    assertEquals(
      LetCmd(Identifier("x"), Num(4), List(
          AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x")),
          AssignCmd(AtExpr(OutputExpr(), Num(2)), Identifier("x")))),
      ast("let x = 4 in out@1 := x; out@2 := x"))

  /** Full line comments prevent commands from being parsed. */
  @Test
  def fullLineComments(): Unit =
    assertEquals(
      LetCmd(Identifier("x"), Num(4), List(
        AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x")),
        AssignCmd(AtExpr(OutputExpr(), Num(2)), Identifier("y")))),
      ast("let x = 4 in out@1 := x;\n//out@3 := z;\nout@2 := y"))

  /** Partial line comments prevent commands from being parsed. */
  @Test
  def partialLineComments(): Unit =
    assertEquals(
      AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x")),
      ast("out@1 := x//@1"))
}
