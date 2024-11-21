package plaid.antlr;

import org.junit.Test;
import plaid.constraints.ast.*;

import static org.junit.Assert.assertEquals;

public class ConstraintsExprVisitorTest {
    /**
     * converts string src code into abstract syntax tree
     */
    private ConstraintsExpr ast(String src){
        return ConstraintsLoader.toExpr(src);
    }

    /**
     * parses assert constraints
     */
    @Test
    public void assertExpr(){
        Node expr = ast("m[\"z\"]@1 + m[\"z\"]@2 == (m[\"x\"]@1 + m[\"x\"]@2) * (m[\"y\"]@1 + m[\"y\"]@2)");
        assertEquals(
                new EqualConstraintsExpr(
                        new PlusConstraintsTerm(
                                new MessageConstraintsTerm("z", 1), 
                                new MessageConstraintsTerm("z", 2)),
                        new TimesConstraintsTerm(
                                new PlusConstraintsTerm(new MessageConstraintsTerm("x", 1), new MessageConstraintsTerm("x", 2)),
                                new PlusConstraintsTerm(new MessageConstraintsTerm("y", 1), new MessageConstraintsTerm("y", 2)))), 
                expr);
    }
    /**
     * parses And constraints
     */
    @Test
     public void andConstraints(){
         Node expected_expr = new AndConstraintsExpr(new EqualConstraintsExpr(new MessageConstraintsTerm("z", 1), new MessageConstraintsTerm("z", 1)),
                 new EqualConstraintsExpr(new MessageConstraintsTerm("y", 2), new MessageConstraintsTerm("y", 2)));
         Node actual_expr = ast("(m[\"z\"]@1==m[\"z\"]@1) AND (m[\"y\"]@2 == m[\"y\"]@2)");
         
         assertEquals(expected_expr, actual_expr);
     }
        

    /**
     * parses Not contraints
     */
    @Test
    public void notConstraints(){
        Node expected_expr = new NotConstraintsExpr(new EqualConstraintsExpr(new MessageConstraintsTerm("z", 1), new MessageConstraintsTerm("z", 1)));
        Node actual_expr = ast("NOT(m[\"z\"]@1==m[\"z\"]@1)");
        assertEquals(expected_expr, actual_expr);
    }
    
    
}
