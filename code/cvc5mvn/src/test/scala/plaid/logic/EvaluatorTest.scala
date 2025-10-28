package plaid.logic

import org.junit.Test
import org.junit.Assert.assertEquals
import plaid.antlr.Loader
import plaid.ast._

class EvaluatorTest {

  private def evalExpr(src: String, exprFunctions: List[ExprFunction]): Expr = {
    val ast = Loader.expression(src)
    val evaluator = Evaluator(Program(List(), exprFunctions, List()))
    evaluator.expression(ast)
  }

  private def evalCommand(src: String, commandFunctions: List[CommandFunction]): Cmd = {
    val ast = Loader.command(src)
    val evaluator = Evaluator(Program(commandFunctions, List(), List()))
    evaluator.command(ast)
  }

  @Test
  def evalString(): Unit = {
    assertEquals(Str("foo"), evalExpr("\"foo\"", List()))
  }

  @Test
  def literalConcat(): Unit = {
    assertEquals(Str("xy"), evalExpr("\"x\"++\"y\"", List()))
  }

  @Test
  def identifierConcat(): Unit = {
    assertEquals(
      ConcatExpr(Identifier("i"), Str("x")),
      evalExpr("i++\"x\"", List())
    )
    assertEquals(
      ConcatExpr(Str("x"), Identifier("i")),
      evalExpr("\"x\"++i", List())
    )
  }

  @Test
  def evalGroupedConcat(): Unit = {
    assertEquals(Str("xywz"), evalExpr("(\"x\"++\"y\")++(\"w\"++\"z\")", List()))
  }

  @Test
  def letSubstitution(): Unit = {
    assertEquals(Num(3), evalExpr("let r11 = 3 in r11", List()))
  }

  @Test
  def doubleLetSubstitution(): Unit = {
    val letExpr = "let r11 = 3 in let r10 = 4 in r11 + r10"
    assertEquals(PlusExpr(Num(3), Num(4)), evalExpr(letExpr, List()))
  }

  @Test
  def evalLetExpr(): Unit = {
    val letExpr = "let y = 3 in let x = y + 1 in x + y"
    assertEquals(PlusExpr(PlusExpr(Num(3), Num(1)), Num(3)), evalExpr(letExpr, List()))
  }

  @Test
  def evalShadowing(): Unit = {
    assertEquals(PlusExpr(Num(5), Num(3)), evalExpr("let x = 3 in let x = 5 in x+3", List()))
  }

  @Test
  def evalfunctionCall(): Unit = {
    val parameters = List(Identifier("x"), Identifier("y"), Identifier("z"))
    val expr = Loader.expression(
      """let r11 = r[z] + (m[x] + 1) * (m[y] + 1) in
        |let r10 = r[z] + (m[x] + 1) * (m[y] + 0) in
        |let r01 = r[z] + (m[x] + 0) * (m[y] + 1) in
        |let r00 = r[z] + (m[x] + 0) * (m[y] + 0) in
        |{ row1 = r11; row2 = r10; row3 = r01; row4 = r00 }""".stripMargin)
    val exprFunction = ExprFunction(Identifier("andtablegmw"), parameters, expr)

    val result = evalExpr("""andtablegmw("foo", "bar", "barz")""", List(exprFunction))
    val expectedResult = Loader.expression(
      """{row1 = r["barz"] + (m["foo"] + 1) * (m["bar"] + 1);
        |row2 = r["barz"] + (m["foo"] + 1) * (m["bar"] + 0);
        |row3 = r["barz"] + (m["foo"] + 0) * (m["bar"] + 1);
        |row4 = r["barz"] + (m["foo"] + 0) * (m["bar"] + 0) }""".stripMargin)
    assertEquals(expectedResult, result)
  }

  @Test
  def constraintEvaluationReducesConcatenation(): Unit = {
    val program = Program(List(), List(), List())
    val expr = Loader.constraint("""2 == m["x" ++ "y"]@1""")
    val evaluator = Evaluator(program)
    val actual = evaluator.constraint(expr)
    val expected = Loader.constraint("""2 == m["xy"]@1""")
    assertEquals(expected, actual)
  }

  @Test
  def constraintEvaluationPreludeFunctions(): Unit = {
    val program = Loader.program("""exprfunctions: f(i) {out@i}""")
    val expr = Loader.constraint("f(1) == 2")
    val evaluator = Evaluator(program)
    val actual = evaluator.constraint(expr)
    val expected = Loader.constraint("out@1 == 2")
    assertEquals(expected, actual)
  }

  @Test
  def constraintEvaluationPropagation(): Unit = {
    val program = Loader.program("exprfunctions: f() {1}")
    val evaluator = Evaluator(program)

    assertEquals(
      Loader.constraint("1 == 2 AND 3 == 3"),
      evaluator.constraint(Loader.constraint("f() == 2 AND 3 == 3"))
    )
    assertEquals(
      Loader.constraint("3 == 3 AND 1 == 2"),
      evaluator.constraint(Loader.constraint("3 == 3 AND f() == 2"))
    )
    assertEquals(
      Loader.constraint("NOT (1 == 2)"),
      evaluator.constraint(Loader.constraint("NOT (f() == 2)"))
    )
  }

  @Test
  def constraintValuedFunctions(): Unit = {
    val program = Loader.program("constraintfunctions: g(i) {3 == out@i}")
    val expr = Loader.constraint("NOT g(1)")
    val evaluator = Evaluator(program)
    val actual = evaluator.constraint(expr)
    val expected = Loader.constraint("NOT (3 == out@1)")
    assertEquals(expected, actual)
  }

  @Test
  def constraintValuedFunctionsContainPrelude(): Unit = {
    val program = Loader.program("exprfunctions: f(i) {out@i} constraintfunctions: g(i) {3 == f(i)}")
    val expr = Loader.constraint("NOT g(1)")
    val evaluator = Evaluator(program)
    val actual = evaluator.constraint(expr)
    val expected = Loader.constraint("NOT (3 == out@1)")
    assertEquals(expected, actual)
  }

  @Test
  def evalAssignCommand(): Unit = {
    val command = evalCommand("""r["x"]@1 := 3""", List())
    assertEquals(AssignCmd(AtExpr(RandomExpr(Str("x")), Num(1)), Num(3)), command)
  }

  @Test
  def evalFunctionCallCommand(): Unit = {
    val parameters = List(
      TypedIdentifier(Identifier("n"), StringType()),
      TypedIdentifier(Identifier("i1"), PartyIndexType()),
      TypedIdentifier(Identifier("i2"), PartyIndexType())
    )
    val commands = Loader.command(
      """m[n]@i2 := (s[n] + r[n])@i1;
        |m[n]@i1 := r[n]@i1""".stripMargin)
    val functionContext = List(CommandFunction(Identifier("encodegmw"), parameters, commands, null, null))
    val command = evalCommand("""encodegmw("x", 2, 1)""", functionContext)

    assertEquals(
      Loader.command(
        """m["x"]@1 := (s["x"] + r["x"])@2;
          |m["x"]@2 := r["x"]@2""".stripMargin),
      command
    )
  }

  @Test
  def evalLetCommand(): Unit = {
    val command = evalCommand("""let table = "foo" in m[table]@1 := r["x"]@2""", List())
    assertEquals(Loader.command("""m["foo"]@1 := r["x"]@2"""), command)
  }

  @Test
  def evalDoubleLetCommand(): Unit = {
    val command = evalCommand("""let table = "foo" in let i = 2 in m[table]@1 := r["x"]@i""", List())
    assertEquals(Loader.command("""m["foo"]@1 := r["x"]@2"""), command)
  }

  @Test
  def evalCommandList(): Unit = {
    val commandList = evalCommand(
      """let x = "x" in m[x]@1 := m[x]@2;
        |let y = "y" in s[y]@1 := s[y]@2""".stripMargin,
      List())
    assertEquals(Loader.command("""m["x"]@1:=m["x"]@2; s["y"]@1 := s["y"]@2"""), commandList)
  }

  @Test
  def evalAssertCommand(): Unit = {
    val formalParameters = List(
      TypedIdentifier(Identifier("x"), StringType()),
      TypedIdentifier(Identifier("i1"), PartyIndexType()),
      TypedIdentifier(Identifier("i2"), PartyIndexType())
    )
    val functionBody = Loader.command(
      """m[x++"exts"]@i1 := m[x++"s"]@i2;
        |m[x++"extm"]@i1 := m[x++"m"]@i2;
        |assert(m[x++"extm"] = m[x++"k"] + (m["delta"] * m[x++"exts"]))@i1;
        |m[x]@i1 := (m[x++"exts"] + m[x++"s"])@i1""".stripMargin)
    val functionContext = List(CommandFunction(Identifier("_open"), formalParameters, functionBody, null, null))

    val command = evalCommand("""_open("foo", 1, 2)""", functionContext)
    assertEquals(
      Loader.command(
        """m["fooexts"]@1 := m["foos"]@2;
          |m["fooextm"]@1 := m["foom"]@2;
          |assert(m["fooextm"] = m["fook"] + (m["delta"] * m["fooexts"]))@1;
          |m["foo"]@1 := (m["fooexts"] + m["foos"])@1""".stripMargin),
      command
    )
  }

}
