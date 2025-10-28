package plaid.antlr

import org.junit.Test
import plaid.ast._
import org.junit.Assert.assertEquals

class CommandVisitorTest {

  private def ast(src: String): Cmd = Loader.command(src)

  /** Parses assignment commands. */
  @Test
  def assignCommand(): Unit = {
    assertEquals(
      AssignCmd(AtExpr(OutputExpr(), Num(1)), Num(3)),
      ast("out@1 := 3")
    )
  }

  /** Functions can be called with zero, one, or multiple actual parameters. */
  @Test
  def functionCall(): Unit = {
    assertEquals(
      CallCmd(Identifier("f"), List()),
      ast("f()")
    )
    assertEquals(
      CallCmd(Identifier("f"), List(Num(0))),
      ast("f(0)")
    )
    assertEquals(
      CallCmd(Identifier("f"), List(Num(0), Identifier("x"))),
      ast("f(0, x)")
    )
  }

  /** Parses assert commands. */
  @Test
  def assertCommand(): Unit = {
    assertEquals(
      AssertCmd(MessageExpr(Str("x")), MessageExpr(Str("y")), Num(5)),
      ast("""assert (m["x"] = m["y"])@5""")
    )
  }

  /** Parses let commands. */
  @Test
  def letCommand(): Unit = {
    val command = ast("let x = 4 in out@1 := x")
    assertEquals(
      LetCmd(
        Identifier("x"),
        Num(4),
        AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x"))
      ),
      command
    )
  }

  /** Let commands can have multiple other commands inside them. */
  @Test
  def letCommandScope(): Unit = {
    val command = ast("let x = 4 in out@1 := x; out@2 := x")
    assertEquals(
      LetCmd(
        Identifier("x"),
        Num(4),
        ListCmd(
          AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x")),
          AssignCmd(AtExpr(OutputExpr(), Num(2)), Identifier("x"))
        )
      ),
      command
    )
  }

  /** Parses list of commands. */
  @Test
  def commandList(): Unit = {
    val command = ast("out@1 := x; out@2 := x")
    assertEquals(
      ListCmd(
        AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x")),
        AssignCmd(AtExpr(OutputExpr(), Num(2)), Identifier("x"))
      ),
      command
    )
  }

  /** Full line comments prevent commands from being parsed. */
  @Test
  def fullLineComments(): Unit = {
    val command = ast("out@1 := x;\n//out@3 := z;\nout@2 := y")
    assertEquals(
      ListCmd(
        AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x")),
        AssignCmd(AtExpr(OutputExpr(), Num(2)), Identifier("y"))
      ),
      command
    )
  }

  /** Full line comments prevent commands from being parsed. */
  @Test
  def partialLineComments(): Unit = {
    val command = ast("out@1 := x//@1")
    assertEquals(
      AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x")),
      command
    )
  }

  /** Parses OT expression */
  @Test
  def OTCommand(): Unit = {
    assertEquals(
      AssignCmd(
        AtExpr(MessageExpr(Str("x")), Num(1)),
        AtExpr(
          OTExpr(
            SecretExpr(Str("foo")),
            Num(1),
            MessageExpr(Str("bar")),
            MessageExpr(Str("zoo"))
          ),
          Num(2)
        )
      ),
      ast("""m["x"]@1 := OT(s["foo"]@1, m["bar"], m["zoo"])@2""")
    )
  }

  /** Parses a list of commands in right-associative style. */
  @Test
  def rightAssociative(): Unit = {
    val command = ast("out@1 := x; out@2 := x; out@3 := x")
    val expected = ListCmd(
      ListCmd(
        AssignCmd(AtExpr(OutputExpr(), Num(1)), Identifier("x")),
        AssignCmd(AtExpr(OutputExpr(), Num(2)), Identifier("x"))
      ),
      AssignCmd(AtExpr(OutputExpr(), Num(3)), Identifier("x"))
    )
    assertEquals(expected, command)
  }
}
