package plaid.eval;

//import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import plaid.ast.*;
import plaid.eval.ExpressionEvaluator;
import plaid.antlr.Loader;

import java.util.*;

import static org.junit.Assert.assertEquals;


public class ExpressionEvaluatorTest {

    private PreludeExpression eval(String src, List<ExprFunction> exprFunctions){
        PreludeExpression ast = Loader.toExpression(src);
        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(new Program(List.of(), exprFunctions, List.of(), null, null));
        return expressionEvaluator.toOverture(ast);
    }
    /**
     * String literal is a value
     */
    @Test
    public void evalString(){
        assertEquals(new Str("foo"), eval("\"foo\"", List.of()));
    }

    /**
     * evaluate concatenation
     */
    @Test
    public void evalConcat(){
        //assertEquals(new Str(String.valueOf(new Str("x")) + String.valueOf(new Str("y"))), eval("\"x\"++\"y\"", List.of()));
        assertEquals(new Str("xy"), eval("\"x\"++\"y\"", List.of()));
    }

    /**
     * let y = v in e
     * v substitute for y occurrences in e
     */
    @Test
    public void letSubstitution(){
        PreludeExpression letExpr = Loader.toExpression("let r11 = 3 in r11");
        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(new Program(List.of(), List.of(), List.of(), null, null));

        PreludeExpression result = expressionEvaluator.toOverture(letExpr);
        assertEquals(new Num(3), result);
    }

    /**
     * the expression in 'in' area can be let expression
     */
    @Test
    public void doubleLetSubstitution(){
        PreludeExpression letExpr = Loader.toExpression("let r11 = 3 in let r10 = 4 in r11 + r10");
        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(new Program(List.of(), List.of(), List.of(), null, null));

        PreludeExpression result = expressionEvaluator.toOverture(letExpr);
        assertEquals(new PlusExpr(new Num(3), new Num(4)), result);
    }

    /**
     * expression in let area should evaluate to value before substitution
     */
    @Test
    public void evalLetExpr(){
        PreludeExpression letExpr = Loader.toExpression("let y = 3 in let x = y + 1 in x + y ");
        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(new Program(List.of(), List.of(), List.of(), null, null));
        PreludeExpression result = expressionEvaluator.toOverture(letExpr);
        assertEquals(new PlusExpr(new PlusExpr(new Num(3), new Num(1)), new Num(3)), result);
    }

    /**
     * evaluator should support shadowing
     */
    @Test
    public void evalShadowing() {
        assertEquals(new PlusExpr(new Num(5), new Num(3)) , eval("let x = 3 in let x = 5 in x+3", List.of()));
    }
    /**
     * evaluate function call (with let expr, field expr, field selection)
     */
    @Test
    public void evalfunctionCall(){

        List<Identifier> parameters = List.of(new Identifier("x"), new Identifier("y"), new Identifier("z"));
        PreludeExpression expr =
                Loader.toExpression("    let r11 = r[z] + (m[x] + 1) * (m[y] + 1) in\n" +
                "    let r10 = r[z] + (m[x] + 1) * (m[y] + 0) in\n" +
                "    let r01 = r[z] + (m[x] + 0) * (m[y] + 1) in\n" +
                "    let r00 = r[z] + (m[x] + 0) * (m[y] + 0) in\n" +
                "    { row1 = r11; row2 = r10; row3 = r01; row4 = r00 }");
        ExprFunction exprFunction = new ExprFunction(new Identifier("andtablegmw"), parameters, expr);

        PreludeExpression result = eval("""
                andtablegmw("foo", "bar", "barz")""", List.of(exprFunction));
        PreludeExpression expectedResult =
                Loader.toExpression("""
                {row1 = r["barz"] + (m["foo"] + 1) * (m["bar"] + 1); 
                row2 = r["barz"] + (m["foo"] + 1) * (m["bar"] + 0); 
                row3 = r["barz"] + (m["foo"] + 0) * (m["bar"] + 1); 
                row4 = r["barz"] + (m["foo"] + 0) * (m["bar"] + 0) }""");
        assertEquals(expectedResult , result);
    }

    /**
     * evaluate field record
     */
    @Test
    public void evalFieldExpr(){

    }
    /**
     * evaluate field selection
     */
    @Test
    public void evalFieldSelection(){

    }

    /**
     * When a constraint contains a prelude expression that uses string
     * concatenation, the prelude expression is reduced to a single string
     * when the constraint is evaluated.
     */
    @Test
    public void constraintEvaluationReducesConcatenation() {
        Program program = new Program(List.of(), List.of(), List.of(), null, null);
        ConstraintExpr expr = Loader.toConstraintExpression("2 == m[\"x\" ++ \"y\"]@1");
        ExpressionEvaluator evaluator = new ExpressionEvaluator(program);
        ConstraintExpr actual = evaluator.evaluate(expr);
        ConstraintExpr expected = Loader.toConstraintExpression("2 == m[\"xy\"]@1");
        assertEquals(expected, actual);
    }

    /**
     * Prelude function calls can be embedded in the prelude expressions of
     * constraints, and they reduce during constraint evaluation.
     */
    @Test
    public void constraintEvaluationPreludeFunctions() {
        Program program = Loader.toProgram("exprfunctions: f(i) {out@i}");
        ConstraintExpr expr = Loader.toConstraintExpression("f(1) == 2");
        ExpressionEvaluator evaluator = new ExpressionEvaluator(program);
        ConstraintExpr actual = evaluator.evaluate(expr);
        ConstraintExpr expected = Loader.toConstraintExpression("out@1 == 2");
        assertEquals(expected, actual);
    }

    /**
     * The evaluation of prelude expressions still happens even when the
     * prelude expressions are embedded within complex constraints.
     */
    @Test
    public void constraintEvaluationPropagation() {
        Program program = Loader.toProgram("exprfunctions: f() {1}");
        ExpressionEvaluator evaluator = new ExpressionEvaluator(program);

        // Left side of AND
        assertEquals(
                Loader.toConstraintExpression("1 == 2 AND 3 == 3"),
                evaluator.evaluate(Loader.toConstraintExpression("f() == 2 AND 3 == 3")));

        // Right side of AND
        assertEquals(
                Loader.toConstraintExpression("3 == 3 AND 1 == 2"),
                evaluator.evaluate(Loader.toConstraintExpression("3 == 3 AND f() == 2")));

        // NOT
        assertEquals(
                Loader.toConstraintExpression("NOT (1 == 2)"),
                evaluator.evaluate(Loader.toConstraintExpression("NOT (f() == 2)")));
    }

    /**
     * Constraint valued functions get called during constraint evaluation.
     */
    @Test
    public void constraintValuedFunctions() {
        Program program = Loader.toProgram("constraintfunctions: g(i) {3 == out@i}");
        ConstraintExpr expr = Loader.toConstraintExpression("NOT g(1)");
        ExpressionEvaluator evaluator = new ExpressionEvaluator(program);
        ConstraintExpr actual = evaluator.evaluate(expr);
        ConstraintExpr expected = Loader.toConstraintExpression("NOT (3 == out@1)");
        assertEquals(expected, actual);
    }

    /**
     * The prelude inside constraint valued functions get called during
     * constraint evaluation.
     */
    @Test
    public void constraintValuedFunctionsContainPrelude() {
        Program program = Loader.toProgram("exprfunctions: f(i) {out@i} constraintfunctions: g(i) {3 == f(i)}");
        ConstraintExpr expr = Loader.toConstraintExpression("NOT g(1)");
        ExpressionEvaluator evaluator = new ExpressionEvaluator(program);
        ConstraintExpr actual = evaluator.evaluate(expr);
        ConstraintExpr expected = Loader.toConstraintExpression("NOT (3 == out@1)");
        assertEquals(expected, actual);
    }

}
