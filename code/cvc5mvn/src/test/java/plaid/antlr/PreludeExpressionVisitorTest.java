package plaid.antlr;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import plaid.ast.*;

import static org.junit.Assert.assertEquals;

public class PreludeExpressionVisitorTest {

    private PreludeExpression ast(String src) {
        PreludeLoader loader = new PreludeLoader();
        ParseTree tree = loader.createParser(src).p_expression();
        return PreludeExpressionVisitor.getInstance().visit(tree);
    }

    /**
     * String literals parse, and no quotes in the tree.
     */
    @Test
    public void stringLiteral() {
        PreludeExpression lit = ast("\"asdf\"");
        assertEquals(new Str("asdf"), lit);
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
        PreludeExpression lit = ast("3 + 2 * 9");
        assertEquals(new PlusExpr(new Num(3), new TimesExpr(new Num(2), new Num(9))), lit);
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
        PreludeExpression lit = ast("(3 + 2) * 9");
        assertEquals(new TimesExpr(new PlusExpr(new Num(3), new Num(2)), new Num(9)), lit);
    }

    /**
     * Parentheses override operator precedence.
     */
    @Test
    public void strConcat() {
        PreludeExpression lit = ast("\"a\" ++ \"b\" ++ \"c\"");
        assertEquals(new ConcatExpr(new ConcatExpr(new Str("a"), new Str("b")), new Str("c")), lit);
    }

}
