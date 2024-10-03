package plaid.antlr;

import org.junit.Test;
import plaid.ast.ConcatExpr;
import plaid.ast.FieldExpr;
import plaid.ast.FieldSelectExpr;
import plaid.ast.FunctionCallExpr;
import plaid.ast.Identifier;
import plaid.ast.LetExpr;
import plaid.ast.MessageExpr;
import plaid.ast.MinusExpr;
import plaid.ast.Node;
import plaid.ast.Num;
import plaid.ast.OutputExpr;
import plaid.ast.PlusExpr;
import plaid.ast.PreludeExpression;
import plaid.ast.PublicExpr;
import plaid.ast.RandomExpr;
import plaid.ast.SecretExpr;
import plaid.ast.Str;
import plaid.ast.TimesExpr;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ExpressionVisitorTest {

    private PreludeExpression ast(String src) {
        return Loader.toExpression(src);
    }

    /**
     * String literals parse, and no quotes in the tree.
     */
    @Test
    public void stringLiteral() {
        assertEquals(new Str("asdf"), ast("\"asdf\""));
    }

    /**
     * An expression is an expression no matter how many parens it is wrapped in.
     */
    @Test
    public void unlimitedParens() {
        PreludeExpression none = ast("\"x\"");
        PreludeExpression many = ast("((((((\"x\"))))))");
        assertEquals(many, none);
    }

    /**
     * Multiplication comes before addition.
     */
    @Test
    public void multiplyFirst() {
        PreludeExpression expr = ast("3 + 2 * 9");
        assertEquals(new PlusExpr(new Num(3), new TimesExpr(new Num(2), new Num(9))), expr);
    }

    /**
     * Negation happens before party index.
     */
    @Test
    public void minusFirst() {
        Node expr = ast("-m[z]@1");
        assertEquals(new MinusExpr(new MessageExpr(new Identifier("z"), new Num(1))), expr);
    }

    /**
     * Parentheses override operator precedence.
     */
    @Test
    public void parensOverride() {
        PreludeExpression expr = ast("(3 + 2) * 9");
        assertEquals(new TimesExpr(new PlusExpr(new Num(3), new Num(2)), new Num(9)), expr);
    }

    /**
     * Parentheses override operator precedence.
     */
    @Test
    public void strConcat() {
        PreludeExpression expr = ast("\"a\" ++ \"b\" ++ \"c\"");
        assertEquals(new ConcatExpr(new ConcatExpr(new Str("a"), new Str("b")), new Str("c")), expr);
    }

    /**
     * Field selection has higher precedence than binary ops.
     */
    @Test
    public void selectPrecedence() {
        PreludeExpression expr = ast("8 * parent.child");
        PreludeExpression select = new FieldSelectExpr(new Identifier("parent"), new Identifier("child"));
        assertEquals(new TimesExpr(new Num(8), select), expr);
    }

    /**
     * Parses memory expressions with index if needed.
     */
    @Test
    public void memoryExpr() {
        assertEquals(new OutputExpr(new Num(1)), ast("out@1"));
        assertEquals(new SecretExpr(new Str("x"), new Num(1)), ast("s[\"x\"]@1"));
        assertEquals(new MessageExpr(new Str("x"), new Num(1)), ast("m[\"x\"]@1"));
        assertEquals(new RandomExpr(new Str("x"), new Num(1)), ast("r[\"x\"]@1"));
        assertEquals(new PublicExpr(new Str("x")), ast("p[\"x\"]@1"));
    }

    /**
     * Multiple party indexes get assigned correctly as siblings.
     */
    @Test
    public void partyIndexSiblings() {
        PreludeExpression expr = ast("s[\"y\"]@2 + s[\"x\"]@1");
        assertEquals(new PlusExpr(
                new SecretExpr(new Str("y"), new Num(2)),
                new SecretExpr(new Str("x"), new Num(1))), expr);
    }

    /**
     * Party indexes must not be ambiguous.
     */
    @Test(expected = IllegalStateException.class)
    public void partyIndexesDoNotStack() {
        ast("(s[\"y\"] + s[\"x\"]@1)@2");
    }

    /**
     * Party index required for memory expression.
     */
    @Test(expected = IllegalStateException.class)
    public void memoryPartyIndexRequired() {
        ast("m[\"y\"]");
    }

    /**
     * Party index required for secret expression.
     */
    @Test(expected = IllegalStateException.class)
    public void secretPartyIndexRequired() {
        ast("s[\"y\"]");
    }

    /**
     * Party index required for random expression.
     */
    @Test(expected = IllegalStateException.class)
    public void randomPartyIndexRequired() {
        ast("r[\"y\"]");
    }

    /**
     * Party index required for output expression.
     */
    @Test(expected = IllegalStateException.class)
    public void outputPartyIndexRequired() {
        ast("out");
    }

    /**
     * Parses let expressions.
     */
    @Test
    public void letExpr() {
        PreludeExpression expr = ast("let z = 1 in 2 + z");
        assertEquals(new LetExpr(
                new Identifier("z"),
                new Num(1),
                new PlusExpr(new Num(2), new Identifier("z"))), expr);
    }

    /**
     * Function calls can have zero, one, or multiple parameters.
     */
    @Test
    public void functionCall() {
        assertEquals(new FunctionCallExpr(
                new Identifier("f"),
                List.of()), ast("f()"));
        assertEquals(new FunctionCallExpr(
                new Identifier("f"),
                List.of(new Num(0))), ast("f(0)"));
        assertEquals(new FunctionCallExpr(
                new Identifier("f"),
                List.of(new Num(0), new Identifier("x"))), ast("f(0, x)"));
    }

    /**
     * Fields can have zero, one, or multiple members.
     */
    @Test
    public void fieldExpr() {
        assertEquals(new FieldExpr(Map.of()), ast("{}"));
        assertEquals(new FieldExpr(Map.of(
                new Identifier("a"), new Num(0))), ast("{a=0}"));
        assertEquals(new FieldExpr(Map.of(
                new Identifier("a"), new Num(0),
                new Identifier("b"), new Num(1))), ast("{a=0; b=1}"));
    }

}
