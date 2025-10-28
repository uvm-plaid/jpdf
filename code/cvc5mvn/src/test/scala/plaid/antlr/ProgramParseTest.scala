package plaid.antlr

import org.junit.Test
import plaid.ast.Program
import org.junit.Assert.assertNotNull

class ProgramParseTest {

  /** Parses an example program with postcondition. */
  @Test
  def programWithPostcondition(): Unit = {
    val program: Program = Load.program(
      """
        |exprfunctions:
        |
        |not(x){ x + 1 }
        |
        |cmdfunctions:
        |
        |oneTimePad(x:string, y:string, z:string) {
        |    m[z]@1 := (s[x] + not(r[y]))@2
        |}
        |
        |main(){ oneTimePad("foo", "bar", "pan") }
        |
        |postcondition: (m["pan"]@1 ==  s["foo"]@2 + (r["bar"]@2+1))
        |""".stripMargin
    )

    assertNotNull(program)
  }

  /** Parses an example program with both precondition and postcondition. */
  @Test
  def programWithPrePostcondition(): Unit = {
    val program: Program = Load.program(
      """
        |exprfunctions:
        |
        |not(x){ x + 1 }
        |
        |cmdfunctions:
        |
        |oneTimePad(x:string , y:string, z:string) {
        |    m[z]@1 := (s[x] + not(r[y]))@2
        |}
        |
        |main(){ oneTimePad("foo", "bar", "pan") }
        |""".stripMargin
    )

    assertNotNull(program)
  }
}
