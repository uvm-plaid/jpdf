package plaid.eval;

import org.junit.Test;
import plaid.antlr.Loader;
import plaid.ast.*;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class EvaluatorTest {

    private Expr evalExpr(String src, List<ExprFunction> exprFunctions) {
        Expr ast = Loader.toExpression(src);
        Evaluator evaluator = new Evaluator(new Program(List.of(), exprFunctions, List.of(), null, null));
        return evaluator.toOverture(ast);
    }


    private Cmd evalCommand(String src, List<CommandFunction> commandFunctions) {
        Cmd ast = Loader.toCommand(src);
        Evaluator evaluator = new Evaluator(new Program(commandFunctions, List.of(), List.of(), null, null));
        return evaluator.evalInstruction(ast);
    }

    /**
     * String literal is a value
     */
    @Test
    public void evalString() {
        assertEquals(new Str("foo"), evalExpr("\"foo\"", List.of()));
    }

    /**
     * evalConstraint concatenation
     */
    @Test
    public void evalConcat() {
        //assertEquals(new Str(String.valueOf(new Str("x")) + String.valueOf(new Str("y"))), eval("\"x\"++\"y\"", List.of()));
        assertEquals(new Str("xy"), evalExpr("\"x\"++\"y\"", List.of()));
    }

    /**
     * let y = v in e
     * v substitute for y occurrences in e
     */
    @Test
    public void letSubstitution() {
        assertEquals(new Num(3), evalExpr("let r11 = 3 in r11", List.of()));
    }

    /**
     * the expression in 'in' area can be let expression
     */
    @Test
    public void doubleLetSubstitution() {
        String letExpr = ("let r11 = 3 in let r10 = 4 in r11 + r10");

        assertEquals(new PlusExpr(new Num(3), new Num(4)), evalExpr(letExpr, List.of()));
    }

    /**
     * expression in let area should evalConstraint to value before substitution
     */
    @Test
    public void evalLetExpr() {
        String letExpr = "let y = 3 in let x = y + 1 in x + y ";

        assertEquals(new PlusExpr(new PlusExpr(new Num(3), new Num(1)), new Num(3)), evalExpr(letExpr, List.of()));
    }

    /**
     * evaluator should support shadowing
     */
    @Test
    public void evalShadowing() {
        assertEquals(new PlusExpr(new Num(5), new Num(3)), evalExpr("let x = 3 in let x = 5 in x+3", List.of()));
    }

    /**
     * evalConstraint function call (with let expr, field expr, field selection)
     */
    @Test
    public void evalfunctionCall() {

        List<Identifier> parameters = List.of(new Identifier("x"), new Identifier("y"), new Identifier("z"));
        Expr expr =
                Loader.toExpression("    let r11 = r[z] + (m[x] + 1) * (m[y] + 1) in\n" +
                        "    let r10 = r[z] + (m[x] + 1) * (m[y] + 0) in\n" +
                        "    let r01 = r[z] + (m[x] + 0) * (m[y] + 1) in\n" +
                        "    let r00 = r[z] + (m[x] + 0) * (m[y] + 0) in\n" +
                        "    { row1 = r11; row2 = r10; row3 = r01; row4 = r00 }");
        ExprFunction exprFunction = new ExprFunction(new Identifier("andtablegmw"), parameters, expr);

        Expr result = evalExpr("""
                andtablegmw("foo", "bar", "barz")""", List.of(exprFunction));
        Expr expectedResult =
                Loader.toExpression("""
                        {row1 = r["barz"] + (m["foo"] + 1) * (m["bar"] + 1); 
                        row2 = r["barz"] + (m["foo"] + 1) * (m["bar"] + 0); 
                        row3 = r["barz"] + (m["foo"] + 0) * (m["bar"] + 1); 
                        row4 = r["barz"] + (m["foo"] + 0) * (m["bar"] + 0) }""");
        assertEquals(expectedResult, result);
    }


    /**
     * When a constraint contains a prelude expression that uses string
     * concatenation, the prelude expression is reduced to a single string
     * when the constraint is evaluated.
     */
    @Test
    public void constraintEvaluationReducesConcatenation() {
        Program program = new Program(List.of(), List.of(), List.of(), null, null);
        Constraint expr = Loader.toConstraintExpression("2 == m[\"x\" ++ \"y\"]@1");
        Evaluator evaluator = new Evaluator(program);
        Constraint actual = evaluator.evalConstraint(expr);
        Constraint expected = Loader.toConstraintExpression("2 == m[\"xy\"]@1");
        assertEquals(expected, actual);
    }

    /**
     * Prelude function calls can be embedded in the prelude expressions of
     * constraints, and they reduce during constraint evaluation.
     */
    @Test
    public void constraintEvaluationPreludeFunctions() {
        Program program = Loader.toProgram("exprfunctions: f(i) {out@i}");
        Constraint expr = Loader.toConstraintExpression("f(1) == 2");
        Evaluator evaluator = new Evaluator(program);
        Constraint actual = evaluator.evalConstraint(expr);
        Constraint expected = Loader.toConstraintExpression("out@1 == 2");
        assertEquals(expected, actual);
    }

    /**
     * The evaluation of prelude expressions still happens even when the
     * prelude expressions are embedded within complex constraints.
     */
    @Test
    public void constraintEvaluationPropagation() {
        Program program = Loader.toProgram("exprfunctions: f() {1}");
        Evaluator evaluator = new Evaluator(program);

        // Left side of AND
        assertEquals(
                Loader.toConstraintExpression("1 == 2 AND 3 == 3"),
                evaluator.evalConstraint(Loader.toConstraintExpression("f() == 2 AND 3 == 3")));

        // Right side of AND
        assertEquals(
                Loader.toConstraintExpression("3 == 3 AND 1 == 2"),
                evaluator.evalConstraint(Loader.toConstraintExpression("3 == 3 AND f() == 2")));

        // NOT
        assertEquals(
                Loader.toConstraintExpression("NOT (1 == 2)"),
                evaluator.evalConstraint(Loader.toConstraintExpression("NOT (f() == 2)")));
    }

    /**
     * Constraint valued functions get called during constraint evaluation.
     */
    @Test
    public void constraintValuedFunctions() {
        Program program = Loader.toProgram("constraintfunctions: g(i) {3 == out@i}");
        Constraint expr = Loader.toConstraintExpression("NOT g(1)");
        Evaluator evaluator = new Evaluator(program);
        Constraint actual = evaluator.evalConstraint(expr);
        Constraint expected = Loader.toConstraintExpression("NOT (3 == out@1)");
        assertEquals(expected, actual);
    }

    /**
     * The prelude inside constraint valued functions get called during
     * constraint evaluation.
     */
    @Test
    public void constraintValuedFunctionsContainPrelude() {
        Program program = Loader.toProgram("exprfunctions: f(i) {out@i} constraintfunctions: g(i) {3 == f(i)}");
        Constraint expr = Loader.toConstraintExpression("NOT g(1)");
        Evaluator evaluator = new Evaluator(program);
        Constraint actual = evaluator.evalConstraint(expr);
        Constraint expected = Loader.toConstraintExpression("NOT (3 == out@1)");
        assertEquals(expected, actual);
    }

    /**
     * evalConstraint assign command
     */
    @Test
    public void evalAssignCommand() {
        Cmd command = evalCommand("r[\"x\"]@1 := 3", List.of());
        assertEquals(new AssignCmd(
                new AtExpr(new RandomExpr(new Str("x")), new Num(1)),
                new Num(3)), command);
    }

    /**
     * evalConstraint function call command
     */
    @Test
    public void evalFunctionCallCommand() {
        List<TypedIdentifier> parameters = List.of(new TypedIdentifier(new Identifier("n"), new StringType()),
                new TypedIdentifier(new Identifier("i1"), new PartyIndexType()),
                new TypedIdentifier(new Identifier("i2"), new PartyIndexType()));
        Cmd commands = Loader.toCommand("m[n]@i2 := (s[n] + r[n])@i1;\n" + "m[n]@i1 := r[n]@i1");
        CommandFunction commandFunction = new CommandFunction(new Identifier("encodegmw"), parameters, commands, null, null);
        List<CommandFunction> functionContext = List.of(commandFunction);
        Cmd command = evalCommand("encodegmw(\"x\", 2, 1)", functionContext);

        assertEquals(Loader.toCommand("m[\"x\"]@1 := (s[\"x\"] + r[\"x\"])@2;\n" + "m[\"x\"]@2 := r[\"x\"]@2"),
                command);
    }

    /**
     * evalConstraint let command
     */
    @Test
    public void evalLetCommand() {
        Cmd command = evalCommand("let table = \"foo\" in m[table]@1 := r[\"x\"]@2", List.of());
        assertEquals(Loader.toCommand("m[\"foo\"]@1 := r[\"x\"]@2"), command);
    }

    /**
     * evalConstraint a double let commands
     */
    @Test
    public void evalDoubleLetCommand() {
        Cmd command = evalCommand("let table = \"foo\" in let i = 2 in m[table]@1 := r[\"x\"]@i", List.of());
        assertEquals(Loader.toCommand("m[\"foo\"]@1 := r[\"x\"]@2"), command);
    }

    /**
     * evalConstraint a list of commands
     */
    @Test
    public void evalCommandList() {
        Cmd commandList = evalCommand("let x = \"x\" in m[x]@1 := m[x]@2; \n let y = \"y\" in s[y]@1 := s[y]@2", List.of());
        assertEquals(Loader.toCommand("m[\"x\"]@1:=m[\"x\"]@2; s[\"y\"]@1 := s[\"y\"]@2"), commandList);
    }

    /**
     * evalConstraint assert command
     */
    @Test
    public void evalAssertCommand() {
        // command function context
        List<TypedIdentifier> formal_parameters = List.of(
                new TypedIdentifier(new Identifier("x"), new StringType()),
                new TypedIdentifier(new Identifier("i1"), new PartyIndexType()),
                new TypedIdentifier(new Identifier("i2"), new PartyIndexType()));
        Cmd functionBody =
                Loader.toCommand("m[x++\"exts\"]@i1 := m[x++\"s\"]@i2;\n" +
                        "m[x++\"extm\"]@i1 := m[x++\"m\"]@i2;\n" +
                        "assert(m[x++\"extm\"] = m[x++\"k\"] + (m[\"delta\"] * m[x++\"exts\"]))@i1;\n" +
                        "m[x]@i1 := (m[x++\"exts\"] + m[x++\"s\"])@i1");
        CommandFunction functionContext = new CommandFunction(new Identifier("_open"), formal_parameters, functionBody, null, null);

        // evalConstraint function body with function call
        Cmd command = evalCommand("_open(\"foo\", 1, 2)", List.of(functionContext));
        assertEquals(Loader.toCommand("m[\"fooexts\"]@1 := m[\"foos\"]@2;\n" +
                "m[\"fooextm\"]@1 := m[\"foom\"]@2;\n" +
                "assert(m[\"fooextm\"] = m[\"fook\"] + (m[\"delta\"] * m[\"fooexts\"]))@1;\n" +
                "m[\"foo\"]@1 := (m[\"fooexts\"] + m[\"foos\"])@1"), command);

    }

}
