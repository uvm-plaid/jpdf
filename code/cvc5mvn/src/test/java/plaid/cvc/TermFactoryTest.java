package plaid.cvc;

import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;
import org.junit.Test;
import plaid.ast.MemoryExpr;
import plaid.ast.MessageExpr;
import plaid.ast.Num;
import plaid.ast.OutputExpr;
import plaid.ast.PlusExpr;
import plaid.ast.PreludeExpression;
import plaid.ast.PublicExpr;
import plaid.ast.RandomExpr;
import plaid.ast.SecretExpr;
import plaid.ast.Str;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TermFactoryTest {

    /**
     * Registers memory nodes when they are children of other nodes.
     */
    @Test
    public void registerChildren() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory prover = new TermFactory(termManager, sort);
        MessageExpr mem = new MessageExpr(new Str("x"), new Num(3));
        PreludeExpression expr = new PlusExpr(new Num(6), mem);
        prover.createConstants(expr);

        Collection<Memory> memories = prover.getMemories();
        assertEquals(1, memories.size());
    }

    /**
     * Registers memory nodes when no other memories are registered.
     */
    @Test
    public void registerFirstMemory() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory prover = new TermFactory(termManager, sort);
        MessageExpr expr = new MessageExpr(new Str("x"), new Num(3));
        prover.createConstants(expr);

        Collection<Memory> memories = prover.getMemories();
        assertEquals("One memory", 1, memories.size());

        Memory mem = prover.getMemories().iterator().next();
        assertEquals("Name based on expression", "m_x_3", mem.name());

        MessageExpr twin = new MessageExpr(new Str("x"), new Num(3));
        assertEquals("Identical expression equality", twin, mem.node());
    }

    /**
     * Registers memory nodes when other memories exist.
     */
    @Test
    public void registerDistinctMemories() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory prover = new TermFactory(termManager, sort);
        MemoryExpr expr1 = new MessageExpr(new Str("x"), new Num(3));
        prover.createConstants(expr1);
        MemoryExpr expr2 = new RandomExpr(new Str("y"), new Num(5));
        prover.createConstants(expr2);

        Collection<Memory> memories = prover.getMemories();
        assertEquals("Two memories", 2, memories.size());
    }

    /**
     * Does not register a new cvc5 term if one already exists for the memory.
     */
    @Test
    public void reuseMemories() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory prover = new TermFactory(termManager, sort);
        MemoryExpr expr = new MessageExpr(new Str("x"), new Num(3));
        prover.createConstants(expr);
        MemoryExpr twin = new MessageExpr(new Str("x"), new Num(3));
        prover.createConstants(twin);

        Collection<Memory> memories = prover.getMemories();
        assertEquals(1, memories.size());
    }

    /**
     * Does not produce a cvc5 constant name for nodes that are not standard memory nodes.
     */
    @Test(expected = IllegalArgumentException.class)
    public void nonMemNames() {
        MemoryExpr other = List::of;
        TermFactory.getCvcName(other);
    }

    /**
     * Produces cvc5 constant names for all memory types.
     */
    @Test
    public void memNames() {
        assertEquals("m_x_4", TermFactory.getCvcName(new MessageExpr(new Str("x"), new Num(4))));
        assertEquals("o_4", TermFactory.getCvcName(new OutputExpr(new Num(4))));
        assertEquals("p_x", TermFactory.getCvcName(new PublicExpr(new Str("x"))));
        assertEquals("r_x_4", TermFactory.getCvcName(new RandomExpr(new Str("x"), new Num(4))));
        assertEquals("s_x_4", TermFactory.getCvcName(new SecretExpr(new Str("x"), new Num(4))));
    }

    /**
     * Number nodes in the AST evaluate to integers.
     */
    @Test
    public void intEval() {
        Num num = new Num(3);
        assertEquals(3, TermFactory.toInt(num));
    }

    /**
     * Nodes in the AST that are not numbers do not evaluate to integers.
     */
    @Test(expected = IllegalArgumentException.class)
    public void intEvalUnsupported() {
        TermFactory.toInt(new Str("x"));
    }

    /**
     * String nodes in the AST evaluate to strings.
     */
    @Test
    public void stringEval() {
        Str str = new Str("x");
        assertEquals("x", TermFactory.toString(str));
    }

    /**
     * Nodes in the AST that are not strings do not evaluate to strings.
     */
    @Test(expected = IllegalArgumentException.class)
    public void stringEvalUnsupported() {
        TermFactory.toString(new Num(3));
    }

}