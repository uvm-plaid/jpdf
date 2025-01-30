package plaid.cvc;

import io.github.cvc5.*;
import org.junit.Ignore;
import org.junit.Test;
import plaid.antlr.Loader;
import plaid.ast.*;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


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
                new AssertCommand(new OutputExpr(), new Num(1), new Num(3)),
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
     * Creates term for Equal constraint
     */
    @Test
    public void equalConstraintTerm() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);

        // m_foo_3 == out_3
        factory.constraintToTerm(
                new EqualConstraintExpr(
                        new AtExpr(new MessageExpr(new Str("foo")), new Num(3)),
                        new AtExpr(new OutputExpr(), new Num(3))));

        // TODO: how to assert CVC5 terms constructed by TermFactory are expected?  
        // creates cvc5 terms for constraints containing the same memories
        //assertFalse(factory.getMemories().stream().filter(x -> x.term().equals(factory.constraintToTerm(new MessageConstraintsTerm("foo", 1)))).toList().isEmpty());
        //assertTrue(factory.getMemories().stream().anyMatch(x -> x.term().equals(factory.constraintToTerm(new OutputExpr()))));
        assertTrue(factory.getMemories().stream().anyMatch(x -> x.name().equals("m_foo_3")));
        assertTrue(factory.getMemories().stream().anyMatch(x -> x.name().equals("o_3")));

    }
   
    /**
     * creates And constraint expr 
     */
    @Test
    public void andConstraintTerm() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        
        factory.constraintToTerm(
                new AndConstraintExpr(
                        new EqualConstraintExpr(new AtExpr(new MessageExpr(new Str("x")), new Num(3)), new AtExpr(new RandomExpr(new Str("y")), new Num(5))),
                        new EqualConstraintExpr(new AtExpr(new SecretExpr(new Str("z")), new Num(1)), new PublicExpr(new Str("1"))))
        );

        // TODO Make assertion about the expression
//        assertTrue(memories.stream().anyMatch(x -> x.term().equals(factory.constraintsToTerm(new MessageConstraintsTerm("x", 3)))));
//        assertTrue(memories.stream().anyMatch(x -> x.term().equals(factory.constraintsToTerm(new RandomConstraintsTerm("y", 5)))));
//        assertTrue(memories.stream().anyMatch(x -> x.term().equals(factory.constraintsToTerm(new SecretConstraintsTerm("z", 1)))));
//        assertTrue(memories.stream().anyMatch(x -> x.term().equals(factory.constraintsToTerm(new PublicConstraintsTerm("1")))));
        
    }

    /**
     * Creates term for Not constraint
     */
    @Test
    public void notConstraintTerm() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);

        // NOT(m_foo_3 == out_3)
        factory.constraintToTerm(
                new NotConstraintExpr(
                new EqualConstraintExpr(
                        new AtExpr(new MessageExpr(new Str("foo")), new Num(3)),
                        new AtExpr(new OutputExpr(), new Num(3)))));

        // TODO: Make assertion about the expression (how to assert CVC5 terms constructed by TermFactory are expected?)  
     }
    
    
    
    /**
     * creates complex constraint expressions 
     */
    @Test
    public void complexConstraintTerms() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        PreludeExpression expr1 = new AtExpr(new MessageExpr(new Str("x")), new Num(3));
        PreludeExpression expr2 = new AtExpr(new RandomExpr(new Str("y")), new Num(5));
        PreludeExpression expr3 = new AtExpr(new SecretExpr(new Str("z")), new Num(1));
        PreludeExpression expr4 = new PublicExpr(new Str("1"));
        
        factory.constraintToTerm(new AndConstraintExpr(new NotConstraintExpr(new EqualConstraintExpr(expr1, expr2)), new EqualConstraintExpr(expr3, expr4)));

        // TODO Make assertion about the terms
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

    /**
     * CVC5 interpretation for OT
     */
    @Test
    public void OTInterpretation() throws CVC5ApiException{
        TermManager termManager = new TermManager();
        
        Sort sort = termManager.mkFiniteFieldSort("2", 10);
        
        TermFactory factory = new TermFactory(termManager, sort);
        // OT(m["x"]@3, r["y"], m["foo"])@2
        PreludeExpression expr1 = new AtExpr(new OTExpr(new MessageExpr(new Str("x")), new Num(3), new RandomExpr(new Str("y")), new MessageExpr(new Str("foo"))), new Num(2));

        // create constants
        Term m_x_3 = termManager.mkConst(sort, "m_x_3");
        Term r_y_2 = termManager.mkConst(sort, "r_y_2");
        Term m_foo_2 = termManager.mkConst(sort, "m_foo_2");
        Term one = termManager.mkFiniteFieldElem("1", sort, 10);

        // create constraints
        Term select_1 = termManager.mkTerm(Kind.FINITE_FIELD_MULT, m_x_3, m_foo_2);
        Term not_m_x_3 = termManager.mkTerm(Kind.FINITE_FIELD_ADD, m_x_3, one);
        Term select_2 = termManager.mkTerm(Kind.FINITE_FIELD_MULT, not_m_x_3, r_y_2);
        Term ot = termManager.mkTerm(Kind.FINITE_FIELD_ADD, select_1, select_2);
        
        // TODO: memory equality
        //assertEquals(ot, factory.toTerm(expr1));
        
    }


    
    

}
