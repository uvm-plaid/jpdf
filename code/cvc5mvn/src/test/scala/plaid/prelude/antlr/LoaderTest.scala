package plaid.prelude.antlr

import org.junit.Assert.assertEquals
import org.junit.Test

class LoaderTest {

  /** Program sections can appear in any order. */
  @Test
  def sectionOrderAgnostic(): Unit = {
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

}
