package plaid.cvc;

import org.junit.Test;
import plaid.ast.MemoryExpr;
import plaid.ast.MessageExpr;
import plaid.ast.Num;
import plaid.ast.OutputExpr;
import plaid.ast.PublicExpr;
import plaid.ast.RandomExpr;
import plaid.ast.SecretExpr;
import plaid.ast.Str;

import static org.junit.Assert.assertEquals;

public class SolverTest {

    /**
     * Does not produce a cvc5 constant name for nodes that are not standard memory nodes.
     */
    @Test(expected = IllegalArgumentException.class)
    public void nonMemNames() {
        MemoryExpr other = new MemoryExpr() { /* Empty */ };
        Solver.getCvcName(other);
    }

    /**
     * Produces cvc5 constant names for all memory types.
     */
    @Test
    public void memNames() {
        assertEquals("m_x_4", Solver.getCvcName(new MessageExpr(new Str("x"), new Num(4))));
        assertEquals("o_4", Solver.getCvcName(new OutputExpr(new Num(4))));
        assertEquals("p_x", Solver.getCvcName(new PublicExpr(new Str("x"))));
        assertEquals("r_x_4", Solver.getCvcName(new RandomExpr(new Str("x"), new Num(4))));
        assertEquals("s_x_4", Solver.getCvcName(new SecretExpr(new Str("x"), new Num(4))));
    }

    /**
     * Number nodes in the AST evaluate to integers.
     */
    @Test
    public void intEval() {
        Num num = new Num(3);
        assertEquals(3, Solver.toInt(num));
    }

    /**
     * Nodes in the AST that are not numbers do not evaluate to integers.
     */
    @Test(expected = IllegalArgumentException.class)
    public void intEvalUnsupported() {
        Solver.toInt(new Str("x"));
    }

    /**
     * String nodes in the AST evaluate to strings.
     */
    @Test
    public void stringEval() {
        Str str = new Str("x");
        assertEquals("x", Solver.toString(str));
    }

    /**
     * Nodes in the AST that are not strings do not evaluate to strings.
     */
    @Test(expected = IllegalArgumentException.class)
    public void stringEvalUnsupported() {
        Solver.toString(new Num(3));
    }

}
