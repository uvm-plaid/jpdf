package plaid.antlr;

import org.junit.Test;
import plaid.constraints.ast.*;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ConstraintsVisitorTest {
    /**
     * converts constraints src code into ast
     */
    private Constraints ast(String src){
        return ConstraintsLoader.toConstraints(src);
    }

    /**
     * parses a list of And constriants
     */
    @Test
    public void andListConstraints(){
        Node expected_expr = new Constraints(List.of(new AndConstraintsExpr(
                new AndConstraintsExpr(
                        new EqualConstraintsExpr(new MessageConstraintsTerm("z", 1), new MessageConstraintsTerm("z", 1)),
                        new EqualConstraintsExpr(new MessageConstraintsTerm("y", 2), new MessageConstraintsTerm("y", 2))),
                new EqualConstraintsExpr(new MessageConstraintsTerm("x", 3), new MessageConstraintsTerm("x", 3)))));
        Node actual_expr = ast("constraints: (m[\"z\"]@1==m[\"z\"]@1) AND (m[\"y\"]@2 == m[\"y\"]@2) AND (m[\"x\"]@3 == m[\"x\"]@3)");

        assertEquals(expected_expr, actual_expr);
    }
}
