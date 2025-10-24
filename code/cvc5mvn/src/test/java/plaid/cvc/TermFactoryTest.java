package plaid.cvc;

import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Kind;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;
import org.junit.Test;
import plaid.antlr.Loader;
import plaid.ast.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TermFactoryTest {

    private Map<Term, Integer> mockModel(TermFactory factory, Map<String, Integer> nameBasedModel) {
        Map<Term, Integer> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : nameBasedModel.entrySet()) {
            Term term = factory
                    .getMemories()
                    .stream()
                    .filter(x -> x.name().equals(entry.getKey()))
                    .findFirst()
                    .orElseThrow()
                    .term();

            result.put(term, entry.getValue());
        }
        return result;
    }

    /**
     * Party indexes must not be ambiguous.
     */
    @Test(expected = IllegalStateException.class)
    public void partyIndexesDoNotStack() throws CVC5ApiException {
        Expr expr = Loader.toExpression("(s[\"y\"] + s[\"x\"]@1)@2");
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
        Expr expr = Loader.toExpression("m[\"y\"]");
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
        Expr expr = Loader.toExpression("s[\"y\"]");
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
        Expr expr = Loader.toExpression("r[\"y\"]");
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
        Expr expr = Loader.toExpression("out");
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
        Term terms = factory.toTerm(new CommandList(
                new AssertCommand(new OutputExpr(), new Num(1), new Num(3)),
                new AssignCommand(new AtExpr(new OutputExpr(), new Num(1)), new Num(3))
        ));
        //assertEquals(2, terms.size());
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
        Expr mem = new AtExpr(new MessageExpr(new Str("x")), new Num(3));
        Expr expr = new PlusExpr(new Num(6), mem);
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
        Expr expr = new AtExpr(new MessageExpr(new Str("x")), new Num(3));
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
        Expr expr1 = new AtExpr(new MessageExpr(new Str("x")), new Num(3));
        factory.toTerm(expr1);
        Expr expr2 = new AtExpr(new RandomExpr(new Str("y")), new Num(5));
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
        Constraint ast = Loader.toConstraintExpression("m[\"foo\"]@3 == 5");
        Term term = factory.constraintToTerm(ast);
        Verifier verifier = new Verifier(factory);
        Map<Term, Integer> actual = verifier.findModelSatisfying(term);
        Map<String, Integer> expected = Map.of("m_foo_3", 5);
        assertEquals(mockModel(factory, expected), actual);
    }
   
    /**
     * creates And constraint expr 
     */
    @Test
    public void andConstraintTerm() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);

        // m["x"]@3 == r["y"]@5 AND r["y"]@5 == 1
        Constraint ast = new AndConstraint(
                new EqualConstraint(new AtExpr(new MessageExpr(new Str("x")), new Num(3)), new AtExpr(new RandomExpr(new Str("y")), new Num(5))),
                new EqualConstraint(new AtExpr(new RandomExpr(new Str("y")), new Num(5)), new Num(1)));

        Term term = factory.constraintToTerm(ast);
        Verifier verifier = new Verifier(factory);
        Map<Term, Integer> actual = verifier.findModelSatisfying(term);
        Map<String, Integer> expected = Map.of("m_x_3", 1, "r_y_5", 1);
        assertEquals(mockModel(factory, expected), actual);
    }

    /**
     * Creates term for Not constraint
     */
    @Test
    public void notConstraintTerm() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("2", 10);
        TermFactory factory = new TermFactory(termManager, sort);

        // NOT(m_foo_3 == 0)
        Term term = factory.constraintToTerm(
                new NotConstraint(
                new EqualConstraint(
                        new AtExpr(new MessageExpr(new Str("foo")), new Num(3)),
                        new Num(0))));

        Verifier verifier = new Verifier(factory);
        Map<Term, Integer> actual = verifier.findModelSatisfying(term);
        Map<String, Integer> expected = Map.of("m_foo_3", 1);
        assertEquals(mockModel(factory, expected), actual);
     }

    /**
     * Creates term for True constraint 
     */
    @Test
    public void trueConstraintTerm() throws CVC5ApiException{
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("2", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        
        Term term = factory.constraintToTerm(new TrueConstraint());
        assertEquals(termManager.mkTrue() ,term);
    }

    /**
     * creates complex constraint expressions 
     */
    @Test
    public void complexConstraintTerms() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        Constraint ast = Loader.toConstraintExpression("out@1 == out@2 + 1 AND out@2 == out@3 + 2 AND out@3 == 4");
        Term term = factory.constraintToTerm(ast);
        Verifier verifier = new Verifier(factory);
        Map<Term, Integer> actual = verifier.findModelSatisfying(term);
        Map<String, Integer> expected = Map.of("o_3", 4, "o_2", 6, "o_1", 0);
        assertEquals(mockModel(factory, expected), actual);
    }

    /**
     * Does not register a new cvc5 term if one already exists for the memory.
     */
    @Test
    public void reuseMemories() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        Expr expr = new AtExpr(new MessageExpr(new Str("x")), new Num(3));
        factory.toTerm(expr);
        Expr twin = new AtExpr(new MessageExpr(new Str("x")), new Num(3));
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
        factory.toTerm(new FunctionCallCommand(new Identifier("f"), List.of()));
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
        PreludeCommand command0 = Loader.toCommand("out@1 := OT(0@1, 1, 0)@2");
        PreludeCommand command1 = Loader.toCommand("out@1 := OT(1@1, 1, 0)@2");
        Verifier verifier = new Verifier(factory);
        
        //verifier.findModelSatisfying(verifier. factory.toTerm(command0));
        Expr expr1 = new AtExpr(new OTExpr(new MessageExpr(new Str("x")), new Num(3), new RandomExpr(new Str("y")), new MessageExpr(new Str("foo"))), new Num(2));

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
