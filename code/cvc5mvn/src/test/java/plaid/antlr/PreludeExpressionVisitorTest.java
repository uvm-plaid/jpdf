package plaid.antlr;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import plaid.ast.PreludeExpression;
import plaid.ast.Str;

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

}
