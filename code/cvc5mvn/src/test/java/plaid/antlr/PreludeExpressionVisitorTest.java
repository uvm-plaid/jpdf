package plaid.antlr;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Assert;
import org.junit.Test;
import plaid.ast.PreludeExpression;

public class PreludeExpressionVisitorTest {

    private PreludeExpression ast(String src) {
        PreludeLoader loader = new PreludeLoader();
        ParseTree tree = loader.createParser(src).p_expression();
        return PreludeExpressionVisitor.getInstance().visit(tree);
    }

    /**
     * An expression is an expression no matter how many parens it is wrapped in.
     */
    @Test
    public void unlimitedParens() {
        PreludeExpression none = ast("x");
        PreludeExpression many = ast("((((((\"x\"))))))");
        Assert.assertEquals(none, many);
    }

}
