package plaid.antlr;

import org.junit.Test;
import plaid.ast.*;
import static org.junit.Assert.assertEquals; 

public class ConstraintExpressionVisitorTest {
    /**
     * converts string src code into AST
     */
    private ConstraintExpr ast(String src){return Loader.toConstraintExpression(src);}

    /**
     * parses equal constraints
     */
    @Test
    public void equalExpr(){
        Node expr = ast("m[\"z\"]@1 + m[\"z\"]@2 == (m[\"x\"]@1 + m[\"x\"]@2) * (m[\"y\"]@1 + m[\"y\"]@2)");
        assertEquals(
                new EqualConstraintExpr(
                        new PlusExpr(
                                new AtExpr(new MessageExpr(new Str("z")), new Num(1)),
                                new AtExpr(new MessageExpr(new Str("z")), new Num(2))),
                        new TimesExpr(
                                new PlusExpr(new AtExpr(new MessageExpr(new Str("x")), new Num(1)), new AtExpr(new MessageExpr(new Str("x")), new Num(2))),
                                new PlusExpr(new AtExpr(new MessageExpr(new Str("y")), new Num(1)), new AtExpr(new MessageExpr(new Str("y")), new Num(2))))),
                expr);
    }

    /**
     * parses And constraints
     */
    @Test
    public void andConstraints(){
        Node expected_expr = new AndConstraintExpr(new EqualConstraintExpr(new AtExpr(new MessageExpr(new Str("z")), new Num(1)), new AtExpr(new MessageExpr(new Str("z")), new Num(1))),
                new EqualConstraintExpr(new AtExpr(new MessageExpr(new Str("y")), new Num(2)), new AtExpr(new MessageExpr(new Str("y")), new Num(2))));
        Node actual_expr = ast("(m[\"z\"]@1==m[\"z\"]@1) AND (m[\"y\"]@2 == m[\"y\"]@2)");

        assertEquals(expected_expr, actual_expr);
    }

    /**
     * parses Not contraints
     */
    @Test
    public void notConstraints(){
        Node expected_expr = new NotConstraintExpr(new EqualConstraintExpr(new AtExpr(new MessageExpr(new Str("z")), new Num(1)), new AtExpr(new MessageExpr(new Str("z")), new Num(1))));
        Node actual_expr = ast("NOT(m[\"z\"]@1==m[\"z\"]@1)");
        assertEquals(expected_expr, actual_expr);
    }

    /**
     * A constraint expression is an expression no matter how many parens it is wrapped in 
     */
    @Test
    public void unlimitedParens(){
        Node none = ast("m[\"x\"]@1 == m[\"x\"]@1");
        Node many = ast("((((((m[\"x\"]@1 == m[\"x\"]@1))))))");
        assertEquals(none, many);
        
    }
    
    /**
     * Parenthesis override operator precedence 
     */
    @Test
    public void parensOverride(){
        // c1 AND (c2 AND c3)  
        Node constraint = ast("m[\"x\"]@1 == m[\"z\"]@3 AND (m[\"x\"]@1 == m[\"y\"]@2 AND m[\"z\"]@3 == m[\"w\"]@4)");
        assertEquals(
                new AndConstraintExpr(
                        new EqualConstraintExpr(new AtExpr(new MessageExpr(new Str("x")), new Num(1)), new AtExpr(new MessageExpr(new Str("z")), new Num(3))), 
                        new AndConstraintExpr(
                                new EqualConstraintExpr(new AtExpr(new MessageExpr(new Str("x")), new Num(1)), new AtExpr(new MessageExpr(new Str("y")), new Num(2))), 
                                new EqualConstraintExpr(new AtExpr(new MessageExpr(new Str("z")), new Num(3)), new AtExpr(new MessageExpr(new Str("w")), new Num(4))))),
                
                constraint);
        
    }

    /**
     * Negation comes before Logical And
     */
    @Test
    public void negationFirst(){
        // NOT c1 AND c2 
        Node constraint = ast("NOT m[\"x\"]@1 == m[\"z\"]@3 AND m[\"x\"]@1 == m[\"y\"]@2");
        assertEquals(
                new AndConstraintExpr(
                        new NotConstraintExpr(new EqualConstraintExpr(new AtExpr(new MessageExpr(new Str("x")), new Num(1)), new AtExpr(new MessageExpr(new Str("z")), new Num(3)))), 
                        new EqualConstraintExpr(new AtExpr(new MessageExpr(new Str("x")), new Num(1)), new AtExpr(new MessageExpr(new Str("y")), new Num(2)))),
                constraint
        );
    }

    /**
     * Equality comes before negation
     */
    @Test
    public void equalityFirst(){
        // NOT e1 == e2
        // NOT (e1 == e2) 
        Node constraint = ast("NOT m[\"x\"]@1 == m[\"z\"]@3");
        assertEquals(new NotConstraintExpr(new EqualConstraintExpr(new AtExpr(new MessageExpr(new Str("x")), new Num(1)), new AtExpr(new MessageExpr(new Str("z")), new Num(3)))),
                constraint);
        
    }
    
    
}
