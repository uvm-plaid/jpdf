package plaid.antlr;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import plaid.ast.ConcatExpr;
import plaid.ast.FieldSelectExpr;
import plaid.ast.Identifier;
import plaid.ast.MessageExpr;
import plaid.ast.MinusExpr;
import plaid.ast.Num;
import plaid.ast.OutputExpr;
import plaid.ast.PlusExpr;
import plaid.ast.PreludeExpression;
import plaid.ast.PublicExpr;
import plaid.ast.RandomExpr;
import plaid.ast.SecretExpr;
import plaid.ast.Str;
import plaid.ast.TimesExpr;

import static org.junit.Assert.assertEquals;

public class PreludeExpressionVisitorTest {

    private PreludeExpression ast(String src) {
        PreludeLoader loader = new PreludeLoader();
        ParseTree tree = loader.createParser(src).expr();
        return new PreludeExpressionVisitor().visit(tree);
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
     * Subtraction and addition have equal precedence.
     */
    @Test
    public void additionAssoc() {
        assertEquals(
                new PlusExpr(new MinusExpr(new Num(3), new Num(2)), new Num(1)),
                ast("3 - 2 + 1"));
        assertEquals(
                new MinusExpr(new PlusExpr(new Num(3), new Num(2)), new Num(1)),
                ast("3 + 2 - 1"));
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
     * Party indexes get stacked if needed.
     */
    @Test
    public void partyIndexesStack() {
        PreludeExpression expr = ast("(s[\"y\"] + s[\"x\"]@1)@2");
        assertEquals(new PlusExpr(
                new SecretExpr(new Str("y"), new Num(2)),
                new SecretExpr(new Str("x"), new Num(1))), expr);
    }

}
