package plaid.cvc;

import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;
import org.junit.Test;
import plaid.antlr.Loader;
import plaid.ast.AssertCommand;
import plaid.ast.AssignCommand;
import plaid.ast.AtExpr;
import plaid.ast.CommandList;
import plaid.ast.FunctionCallCommand;
import plaid.ast.Identifier;
import plaid.ast.MessageExpr;
import plaid.ast.Num;
import plaid.ast.OutputExpr;
import plaid.ast.PlusExpr;
import plaid.ast.PreludeExpression;
import plaid.ast.RandomExpr;
import plaid.ast.Str;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TermFactoryTest {

    /**
     * Party indexes must not be ambiguous.
     */
    @Test(expected = IllegalStateException.class)
    public void partyIndexesDoNotStack() throws CVC5ApiException {
        PreludeExpression expr = Loader.toExpression("(s[\"y\"] + s[\"x\"]@1)@2");
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        factory.toTerm(expr);
    }

    /**
     * Party index required for memory expression.
     */
    @Test(expected = IllegalStateException.class)
    public void memoryPartyIndexRequired() throws CVC5ApiException {
        PreludeExpression expr = Loader.toExpression("m[\"y\"]");
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        factory.toTerm(expr);
    }

    /**
     * Party index required for secret expression.
     */
    @Test(expected = IllegalStateException.class)
    public void secretPartyIndexRequired() throws CVC5ApiException {
        PreludeExpression expr = Loader.toExpression("s[\"y\"]");
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        factory.toTerm(expr);
    }

    /**
     * Party index required for random expression.
     */
    @Test(expected = IllegalStateException.class)
    public void randomPartyIndexRequired() throws CVC5ApiException {
        PreludeExpression expr = Loader.toExpression("r[\"y\"]");
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        factory.toTerm(expr);
    }

    /**
     * Party index required for output expression.
     */
    @Test(expected = IllegalStateException.class)
    public void outputPartyIndexRequired() throws CVC5ApiException {
        PreludeExpression expr = Loader.toExpression("out");
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        factory.toTerm(expr);
    }

    /**
     * Creates terms for all commands in a list.
     */
    @Test
    public void commandListTerms() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        Collection<Term> terms = factory.toTerms(new CommandList(List.of(
                new AssertCommand(new AtExpr(new OutputExpr(), new Num(1)), new Num(3)),
                new AssignCommand(new AtExpr(new OutputExpr(), new Num(1)), new Num(3))
        )));
        assertEquals(2, terms.size());
    }

    /**
     * Converts numbers to finite field terms.
     */
    @Test
    public void numericTerms() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        Term term = factory.toTerm(new Num(1));
        // TODO Understand why the finite field values range from -3 to 3 instead of 0 to 7
        assertNotNull(term.getFiniteFieldValue());
    }

    /**
     * Registers memory nodes when they are children of other nodes.
     */
    @Test
    public void registerChildren() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        PreludeExpression mem = new AtExpr(new MessageExpr(new Str("x")), new Num(3));
        PreludeExpression expr = new PlusExpr(new Num(6), mem);
        factory.toTerm(expr);

        Collection<Memory> memories = factory.getMemories();
        assertEquals(1, memories.size());
    }

    /**
     * Registers memory nodes when no other memories are registered.
     */
    @Test
    public void registerFirstMemory() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        PreludeExpression expr = new AtExpr(new MessageExpr(new Str("x")), new Num(3));
        factory.toTerm(expr);

        Collection<Memory> memories = factory.getMemories();
        assertEquals("One memory", 1, memories.size());

        Memory mem = factory.getMemories().iterator().next();
        assertEquals("Name based on expression", "m_x_3", mem.name());
        assertEquals("Party index captured", Integer.valueOf(3), mem.partyIndex());

        PreludeExpression twin = new MessageExpr(new Str("x"));
        assertEquals("Identical expression equality", twin, mem.node());
    }

    /**
     * Registers memory nodes when other memories exist.
     */
    @Test
    public void registerDistinctMemories() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        PreludeExpression expr1 = new AtExpr(new MessageExpr(new Str("x")), new Num(3));
        factory.toTerm(expr1);
        PreludeExpression expr2 = new AtExpr(new RandomExpr(new Str("y")), new Num(5));
        factory.toTerm(expr2);

        Collection<Memory> memories = factory.getMemories();
        assertEquals("Two memories", 2, memories.size());
    }

    /**
     * Does not register a new cvc5 term if one already exists for the memory.
     */
    @Test
    public void reuseMemories() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        PreludeExpression expr = new AtExpr(new MessageExpr(new Str("x")), new Num(3));
        factory.toTerm(expr);
        PreludeExpression twin = new AtExpr(new MessageExpr(new Str("x")), new Num(3));
        factory.toTerm(twin);

        Collection<Memory> memories = factory.getMemories();
        assertEquals(1, memories.size());
    }

    /**
     * Fails if encounters a command that is not part of overture.
     */
    @Test(expected = IllegalArgumentException.class)
    public void nonOvertureCommand() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        factory.toTerms(new FunctionCallCommand(new Identifier("f"), List.of()));
    }

}
