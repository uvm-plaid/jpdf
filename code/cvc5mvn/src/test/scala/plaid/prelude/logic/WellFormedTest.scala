package plaid.prelude.logic

import org.junit.Assert.{assertFalse, assertTrue}
import org.junit.Test
import plaid.prelude.antlr.Loader

class WellFormedTest {

  private def assertPasses(src: String): Unit =
    assertTrue(WellFormed.checkConstraint(Loader.constraint(src)))

  private def assertFails(src: String): Unit =
    assertFalse(WellFormed.checkConstraint(Loader.constraint(src)))

  /** Memories should always come with party indexes */
  @Test
  def validMemories(): Unit =
    assertFails("""m["m"]@2 == m["k"]@1 + (m["delta"]@1 * m["s"])@2""")
    assertFails("""out@1 == (m["x"] + m["y"])@1""")
    assertPasses("""m["m"]@2 == m["k"]@1 + (m["delta"]@1 * m["s"]@2)""")

  /** Constraint expression should contain only the types of nodes allowed in the grammar */
  @Test
  def constraintNodeTypesOnly(): Unit =
    assertFails("""{ mesg= m["x"] ; rand = r["x"] }.mesg@1  ==  m["x"]@1""")
    assertFails("""let x = "foo" in m[x]@1 == m[x]@1""")
    assertPasses("""m["x"]@1  ==  m["x"]@1""")

  /** Party indexes should be numbers */
  @Test
  def partyIndexNumbers(): Unit =
    assertFails("""s["w"]@i == p["w"]@1""")
    assertPasses("""s["w"]@1 == p["w"]@1""")

  /** Memory indexes should always be strings */
  @Test
  def memoryIndexesStrings(): Unit =
    assertFails("""out@1 == m[x ++ "foo"]@1""")
    assertFails("""out@1 == m[x]@1""")
    assertFails("""out@1 == m[m[x ++ "foo"]]@1""")
    assertPasses("""out@1 == m["xfoo"]@1""")

  /** At expressions cannot be nested inside one another */
  @Test
  def nestedAts(): Unit =
    assertPasses("""out@1 == (m["x"]@1 + m["y"]@1)""")
    assertFails("""out@1 == (m["x"]@1 + m["y"]@1)@1""")
}
