package plaid.prelude.ast

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader

class CmdTest {

  /** Expressions simplify when an assign command is expanded. */
  @Test
  def expandAssignCommand(): Unit =
    val cmd = Loader.command(""" m["x" ++ "y"]@1 := m["u" ++ "v"]@2 """)
    val expected = List(Loader.command(""" m["xy"]@1 := m["uv"]@2 """))
    assertEquals(expected, cmd.expand(Nil))

  /** Expanding let commands substitutes the binding in the let body. */
  @Test
  def expandLetCommand(): Unit =
    val command = Loader.command("""let table = "foo" in m[table]@1 := r["x"]@2""")
    assertEquals(List(Loader.command("""m["foo"]@1 := r["x"]@2""")), command.expand(Nil))

  /** Expanding covers nested let bindings. */
  @Test
  def expandDoubleLetCommand(): Unit =
    val command = Loader.command("""let table = "foo" in let i = 2 in m[table]@1 := r["x"]@i""")
    assertEquals(List(Loader.command("""m["foo"]@1 := r["x"]@2""")), command.expand(Nil))

  /** Expressions simplify when an assert command is expanded. */
  @Test
  def expandAssertCommand(): Unit =
    val cmd = Loader.command("""assert(m["a" ++ "b"] = m["c" ++ "d"])@i1""")
    assertEquals(
      List(Loader.command("""assert(m["ab"] = m["cd"])@i1""")),
      cmd.expand(Nil))
}
