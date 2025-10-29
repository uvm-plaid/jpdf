package plaid.prelude.logic

import io.github.cvc5.CVC5ApiException
import org.junit.Assert.assertEquals
import org.junit.{Ignore, Test}
import plaid.prelude.antlr.Loader
import plaid.prelude.ast.*
import plaid.prelude.transform.Evaluator

class ConstraintAnalyzerTest {

  @throws[CVC5ApiException]
  private def inferPrePostCmd(src: String, program: String): Constraints = {
    val ast: Program = Loader.program(program)
    val analyzer = new ConstraintAnalyzer(ast, "2")
    analyzer.inferPrePostCmd(Loader.command(src), Evaluator(ast))
  }

  @throws[CVC5ApiException]
  private def inferPrePostFN(fName: String, src: String): Constraints = {
    val program = Loader.program(src)
    val analyzer = new ConstraintAnalyzer(program, "2")
    analyzer.inferPrePostFN(program.resolveCommandFunction(Loader.expression(fName)))
  }

  /** infer precondition/postcondition for message assignment */
  @Test
  @throws[CVC5ApiException]
  def inferAssignment(): Unit = {
    val src = """m[x]@1 := (s[x]+r[x])@2"""
    val expected = Constraints(
      TrueConstraint(),
      Loader.constraint("""m[x]@1 == s[x]@2 + r[x]@2""")
    )
    assertEquals(expected.precondition, inferPrePostCmd(src, "").precondition)
    assertEquals(expected.postcondition, inferPrePostCmd(src, "").postcondition)
  }

  /** infer precondition for assert */
  @Test
  @throws[CVC5ApiException]
  def inferAssert(): Unit = {
    val src = """assert (m[x] = m[y])@1"""
    val expected = Constraints(
      Loader.constraint("""m[x]@1 == m[y]@1"""),
      TrueConstraint()
    )
    assertEquals(expected.precondition, inferPrePostCmd(src, "").precondition)
    assertEquals(expected.postcondition, inferPrePostCmd(src, "").postcondition)
  }

  /** infer precondition for let */
  @Test
  @throws[CVC5ApiException]
  def inferLet(): Unit = {
    val src = """let i = 1 in m[x]@i := m[x]@2"""
    val expected = Constraints(
      TrueConstraint(),
      Loader.constraint("""m[x]@1 == m[x]@2""")
    )
    assertEquals(expected.precondition, inferPrePostCmd(src, "").precondition)
    assertEquals(expected.postcondition, inferPrePostCmd(src, "").postcondition)
  }

  /** infer precondition for commandList */
  @Test
  @throws[CVC5ApiException]
  def inferCommandList(): Unit = {
    val src = """m[x]@1 := m[x]@2; assert(m[y]=m[x])@i"""
    val expected = Constraints(
      Loader.constraint("""T AND m[y]@i==m[x]@i"""),
      Loader.constraint("""m[x]@1 == m[x]@2 AND T""")
    )
    assertEquals(expected.precondition, inferPrePostCmd(src, "").precondition)
    assertEquals(expected.postcondition, inferPrePostCmd(src, "").postcondition)
  }

  /** infer precondition and postcondition for function call */
  @Test
  @throws[CVC5ApiException]
  def inferFunctionCall(): Unit = {
    val program =
      """cmdfunctions:
        |f(i:cid) {assert (m["x"] = m["x"])@i}
        |
        |main(){f(1)}
        |""".stripMargin

    val expected = Constraints(
      Loader.constraint("""m["x"]@1 == m["x"]@1"""),
      TrueConstraint()
    )

    val actual = inferPrePostCmd("f(1)", program)
    assertEquals(expected.precondition, actual.precondition)
    assertEquals(expected.postcondition, actual.postcondition)
  }

  /** infer function call with annotations (ignored) */
  @Ignore
  @Test
  @throws[CVC5ApiException]
  def inferFunctionCallWithAnnotations(): Unit = {
    val program =
      """cmdfunctions:
        |precondition: ( m[y]@1 == 2 AND m["z"]@1 == 3)
        |h(y : string) {
        |  m["x"]@1 := (m[y] * m["z"])@1;
        |  assert(m["x"] = m["z"] + m["z"])@1
        |}""".stripMargin

    val expected = Constraints(
      Loader.constraint("""m["y"]@1 == 2 AND m["z"]@1 == 3"""),
      null
    )

    val actual = inferPrePostCmd("""m["y"]@1 := 2; m["z"]@1 := 3; h("y")""", program)
    assertEquals(expected.precondition, actual.precondition)
    assertEquals(expected.postcondition, actual.postcondition)
  }

  /** apply FN rule to derive precondition of a function */
  @Test
  @throws[CVC5ApiException]
  def inferFunction(): Unit = {
    val program =
      """cmdfunctions:
        |f(i:cid) {assert (m["x"] = m["x"])@i}
        |
        |main(){f(1); let x = "foo" in m[x]@2 := m[x]@2}""".stripMargin

    val expected = Constraints(
      Loader.constraint("""m["x"]@1 == m["x"]@1 AND T"""),
      Loader.constraint("""T AND m["foo"]@2 == m["foo"]@2""")
    )

    val actual = inferPrePostFN("main", program)
    assertEquals(expected.precondition, actual.precondition)
    assertEquals(expected.postcondition, actual.postcondition)
  }

  /** infer function with annotations (ignored) */
  @Ignore
  @Test
  @throws[CVC5ApiException]
  def inferFunctionWithAnnotations(): Unit = {
    val program =
      """cmdfunctions:
        |g() {
        |  m["x"]@1 := (m["y"] * m["z"])@1;
        |  assert(m["x"] = m["z"] + m["z"])@1
        |}
        |
        |precondition: ( m["y"]@1 == 2 AND m["z"]@1 == 3)
        |h() { g() }""".stripMargin

    val expected = Constraints(
      Loader.constraint("""m["y"]@1 == 2 AND m["z"]@1 == 3"""),
      null
    )

    val actual = inferPrePostFN("h", program)
    assertEquals(expected.precondition, actual.precondition)
    assertEquals(expected.postcondition, actual.postcondition)
  }

  /** throws error when hardpack does not hold */
  @Test(expected = classOf[RuntimeException])
  @throws[CVC5ApiException]
  def wrongConstraintAnnotation(): Unit = {
    val program =
      """cmdfunctions:
        |precondition: (T)
        |g() {
        |  m["x"]@1 := (m["y"] * m["z"])@1;
        |  assert(m["x"] = m["z"] + m["z"])@1
        |}
        |postcondition: (T)""".stripMargin

    inferPrePostFN("g", program)
  }

  /** if function call uses concatenation expression as argument */
  @Test
  @throws[CVC5ApiException]
  def concatArg(): Unit = {
    val program =
      """cmdfunctions:
        |f(x:string) {m[x++"foo"]@1 := m[x++"foo"]@1}
        |main(){ f(x++"s") }""".stripMargin

    val expected = Constraints(
      TrueConstraint(),
      Loader.constraint("""m[x++"sfoo"]@1 == m[x++"sfoo"]@1""")
    )

    assertEquals(expected.precondition, inferPrePostFN("main", program).precondition)
    assertEquals(expected.postcondition, inferPrePostFN("main", program).postcondition)
  }

  @Test
  def concatLiterals(): Unit = {
    val program = Program(List(), List(), List())
    val evaluator = Evaluator(program)
    val input = ConcatExpr(Str("a"), Str("b"))
    val actual = evaluator.expression(input)
    val expected = Str("ab")
    assertEquals(expected, actual)
  }

  /** ("a" ++ "b") ++ ("c" ++ "d") */
  @Test
  def concatGroupedLiterals(): Unit = {
    val program = Program(List(), List(), List())
    val evaluator = Evaluator(program)
    val group1 = ConcatExpr(Str("a"), Str("b"))
    val group2 = ConcatExpr(Str("c"), Str("d"))
    val input = ConcatExpr(group1, group2)
    val actual = evaluator.expression(input)
    val expected = Str("abcd")
    assertEquals(expected, actual)
  }

}
