package plaid.antlr;

import org.junit.Test;
import plaid.ast.*;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

public class ExpressionVisitorTest {

    private Expr ast(String src) {
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
        Expr none = ast("\"x\"");
        Expr many = ast("((((((\"x\"))))))");
        assertEquals(many, none);
    }

    /**
     * Multiplication comes before addition.
     */
    @Test
    public void multiplyFirst() {
        Expr expr = ast("3 + 2 * 9");
        assertEquals(new PlusExpr(new Num(3), new TimesExpr(new Num(2), new Num(9))), expr);

    }

    /**
     * Negation happens before party index.
     */
    @Test
    public void minusFirst() {
        Node expr = ast("-m[z]@1");
        assertEquals(new MinusExpr(new AtExpr(new MessageExpr(new Identifier("z")), new Num(1))), expr);
    }

    /**
     * Parentheses override operator precedence.
     */
    @Test
    public void parensOverride() {
        Expr expr = ast("(3 + 2) * 9");
        assertEquals(new TimesExpr(new PlusExpr(new Num(3), new Num(2)), new Num(9)), expr);
    }

    /**
     * Parentheses override operator precedence.
     */
    @Test
    public void strConcat() {
        Expr expr = ast("\"a\" ++ \"b\" ++ \"c\"");
        assertEquals(new ConcatExpr(new ConcatExpr(new Str("a"), new Str("b")), new Str("c")), expr);
    }

    /**
     * Field selection has higher precedence than binary ops.
     */
    @Test
    public void selectPrecedence() {
        Expr expr = ast("8 * parent.child");
        Expr select = new FieldSelectExpr(new Identifier("parent"), new Identifier("child"));
        assertEquals(new TimesExpr(new Num(8), select), expr);
    }

    /**
     * Parses memory expressions.
     */
    @Test
    public void memoryExpr() {
        assertEquals(new OutputExpr(), ast("out"));
        assertEquals(new SecretExpr(new Str("x")), ast("s[\"x\"]"));
        assertEquals(new MessageExpr(new Str("x")), ast("m[\"x\"]"));
        assertEquals(new RandomExpr(new Str("x")), ast("r[\"x\"]"));
        assertEquals(new PublicExpr(new Str("x")), ast("p[\"x\"]"));
    }

    /**
     * Multiple party indexes get assigned correctly as siblings.
     */
    @Test
    public void partyIndexSiblings() {
        Expr expr = ast("s[\"y\"]@2 + s[\"x\"]@1");
        assertEquals(new PlusExpr(
                new AtExpr(new SecretExpr(new Str("y")), new Num(2)),
                new AtExpr(new SecretExpr(new Str("x")), new Num(1))), expr);
    }

    /**
     * Parses let expressions.
     */
    @Test
    public void letExpr() {
        Expr expr = ast("let z = 1 in 2 + z");
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
        assertEquals(new FieldExpr(new TreeMap<>(Map.of())), ast("{}"));
        assertEquals(new FieldExpr(new TreeMap<>(Map.of(
                new Identifier("a"), new Num(0)))), ast("{a=0}"));
        assertEquals(new FieldExpr(new TreeMap<>(Map.of(
                new Identifier("a"), new Num(0),
                new Identifier("b"), new Num(1)))), ast("{a=0; b=1}"));
    }

    /**
     * Identifiers can be mixed case.
     */
    @Test
    public void mixedCaseIdentifiers() {
        assertEquals(new Identifier("aA"), ast("aA"));
    }

    /**
     * Identifiers can start with underscores.
     */
    @Test
    public void underscoreIdentifiers() {
        assertEquals(new Identifier("_1"), ast("_1"));
    }

    @Test
    public void identifierEquality() {
        assertEquals(new Identifier("r11"), new Identifier("r11"));
    }

    /**
     * Parses OT expression
     */
    @Test
    public void OTExpr(){
        assertEquals(new OTExpr(new SecretExpr(new Str("foo")), new Num(1), new MessageExpr(new Str("bar")), new MessageExpr(new Str("zoo"))),
                ast("OT(s[\"foo\"]@1, m[\"bar\"], m[\"zoo\"])"));
    }

    /**
     *  Parses OT4 expression
     */
    @Test
    public void OTFourExpr(){
        assertEquals(new OTFourExpr(new MessageExpr(new Str("s1")) , new MessageExpr(new Str("s2")) , new Num(1), new SecretExpr(new Str("x")), new SecretExpr(new Str("y")), new SecretExpr(new Str("z")), new SecretExpr(new Str("t"))),
                ast("OT4((m[\"s1\"], m[\"s2\"])@1, s[\"x\"], s[\"y\"], s[\"z\"], s[\"t\"])"));
    }

}
