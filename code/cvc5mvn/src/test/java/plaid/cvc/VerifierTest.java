package plaid.cvc;

import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;
import org.junit.Ignore;
import org.junit.Test;

import plaid.antlr.Loader;
import plaid.ast.AssignCommand;
import plaid.ast.ConstraintExpr;
import plaid.ast.PreludeCommand;
import plaid.ast.Program;
import plaid.eval.ProgramEvaluator;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static plaid.cvc.CvcUtils.mkFiniteFieldSort;

public class VerifierTest {

    private final TermManager termManager = new TermManager();
    private final Sort sort = mkFiniteFieldSort(termManager, "7", 10);
    private final TermFactory termFactory = new TermFactory(termManager, sort);
    private final Verifier verifier = new Verifier(termFactory);

    private static PreludeCommand evaluates(String program){
        return evaluates(Loader.toProgram(program));
    }

    private static PreludeCommand evaluates(Program program){
        ProgramEvaluator evaluator = new ProgramEvaluator(program);
        return evaluator.eval();
    }

    /**
     * satisfies a simple protocol.
     */
    @Test
    public void satisfiesCorrectProtocol() {
        assertTrue(verifier.satisfies("out@1 := 1@1; out@2 := 2@2"));
    }

    /**
     * Fails to satisfy a simple contradictory protocol.
     */
    @Test
    public void failIncorrectProtocol() {
        assertFalse(verifier.satisfies("out@1 := 1@1; out@1 := 2@1"));
    }

    /**
     * satisfies a protocol that has an assertion.
     */
    @Test
    public void assertionProtocol() {
        assertTrue(verifier.satisfies("out@1 := 1@1; assert (out = 1)@1"));
    }

    /**
     * Fails to satisfy a protocol that has a contradiction caused by an assertion.
     */
    @Test
    public void failAssertionProtocol() {
        assertFalse(verifier.satisfies("out@1 := 1@1; assert (out = 2)@1"));
    }

    @Test
    public void twoPartyAddition() {
        String protocol = """
                m["s"]@2 := (s["x"] + -r["1"])@1;
                m["s"]@1 := (s["y"] + -r["2"])@2;
                p["1"] := (m["s"] + r["1"])@1;
                p["2"] := (m["s"] + r["2"])@2;
                out@1 := (p["1"] + p["2"])@1;
                out@2 := (p["1"] + p["2"])@2
                """;

        String proposition = """
                (out@1 == s["x"]@1 + s["y"]@2) AND (out@2 == s["x"]@1 + s["y"]@2)
                """;
        assertTrue(verifier.satisfies(protocol));
        assertTrue(verifier.entails(termFactory.toTerm(Loader.toCommand(protocol)), termFactory.constraintToTerm(Loader.toConstraintExpression(proposition))));
    }


    /**
     * an overture protocol entails a correct proposition
     */
    @Test
    public void entailsCorrectProposition(){
        String protocol = """
                m["x"]@2 := (s["x"] + r["x"] + r["loc"])@1;
                m["x"]@3 := r["x"]@1
                """;
        String proposition = """
                r["x"]@1 == m["x"]@3
                """;

        assertTrue(verifier.entails(termFactory.toTerm(Loader.toCommand(protocol)), termFactory.constraintToTerm(Loader.toConstraintExpression(proposition))));
    }

    /**
     * three-party addition
     */
    @Test
    public void entailsThreePartyAddition(){
        String protocol = """
                m["s1"]@2 := ((s["1"] + -r["local"]) + -r["x"])@1;
                m["s1"]@3 := r["x"]@1;
                m["s2"]@1 := ((s["2"] + -r["local"]) + -r["x"])@2;
                m["s2"]@3 := r["x"]@2;
                m["s3"]@1 := ((s["3"] + -r["local"]) + -r["x"])@3;
                m["s3"]@2 := r["x"]@3;
                p["1"] := (r["local"] + m["s2"] + m["s3"])@1;
                p["2"] := (m["s1"] + r["local"] + m["s3"])@2;
                p["3"] := (m["s1"] + m["s2"] + r["local"])@3;
                out@1 := (p["1"] + p["2"] + p["3"])@1;
                out@2 := (p["1"] + p["2"] + p["3"])@2;
                out@3 := (p["1"] + p["2"] + p["3"])@3
                """;

        String proposition_1 = """
                out@1 == s["1"]@1 + s["2"]@2 + s["3"]@3
                """;
        String proposition_2 = """
                out@2 == s["1"]@1 + s["2"]@2 + s["3"]@3
                """;
        String proposition_3 = """
                out@3 == s["1"]@1 + s["2"]@2 + s["3"]@3
                """;
        String proposition_4 = """
                out@1 == s["1"]@1 + s["2"]@2 + s["3"]@3 AND
                out@2 == s["1"]@1 + s["2"]@2 + s["3"]@3 AND
                out@3 == s["1"]@1 + s["2"]@2 + s["3"]@3
                """;
        assertTrue(verifier.entails(termFactory.toTerm(Loader.toCommand(protocol)), termFactory.constraintToTerm(Loader.toConstraintExpression(proposition_1))));
        assertTrue(verifier.entails(termFactory.toTerm(Loader.toCommand(protocol)), termFactory.constraintToTerm(Loader.toConstraintExpression(proposition_2))));
        assertTrue(verifier.entails(termFactory.toTerm(Loader.toCommand(protocol)), termFactory.constraintToTerm(Loader.toConstraintExpression(proposition_3))));
        assertTrue(verifier.entails(termFactory.toTerm(Loader.toCommand(protocol)), termFactory.constraintToTerm(Loader.toConstraintExpression(proposition_4))));

        String proposition_5 = """
                out@1 == s["1"]@1 + s["2"]@2 + s["3"]@3 AND
                out@2 == s["1"]@1 + s["2"]@2 + s["3"]@3 AND
                (NOT (out@3 == s["1"]@1 + s["2"]@2 + s["3"]@3))
                """;
        String proposition_6 = """
                 out@2 == s["1"]@1 + s["2"]@2 + r["x"]@3
                """;
        assertFalse(verifier.entails(termFactory.toTerm(Loader.toCommand(protocol)), termFactory.constraintToTerm(Loader.toConstraintExpression(proposition_5))));
        assertFalse(verifier.entails(termFactory.toTerm(Loader.toCommand(protocol)), termFactory.constraintToTerm(Loader.toConstraintExpression(proposition_6))));
    }

    /**
     * a protocol does not verify a contradictory proposition
     */
    @Test
    public void entailsIncorrectProposition(){
        String protocol = """
                m["x"]@2 := (s["x"] + r["x"] + r["loc"])@1;
                m["x"]@3 := r["x"]@1
                """;
        String proposition_1 = """
                r["x"]@1 == m["x"]@2
                """;

        String proposition_2 = """
                r["loc"]@1 == m["x"]@3
                """;

        assertFalse(verifier.entails(termFactory.toTerm(Loader.toCommand(protocol)), termFactory.constraintToTerm(Loader.toConstraintExpression(proposition_1))));
        assertFalse(verifier.entails(termFactory.toTerm(Loader.toCommand(protocol)), termFactory.constraintToTerm(Loader.toConstraintExpression(proposition_2))));
    }
    

    /**
     * two protocols are equivalent
     */
    @Test
    public void equivalent(){
        String protocol_1 = """
                m["x"]@2 := (s["x"] + r["x"] + r["loc"])@1;
                m["x"]@3 := r["x"]@1
                """;
        String protocol_2 = """
                m["x"]@3 := r["x"]@1;
                m["x"]@2 := (s["x"] + r["x"] + r["loc"])@1
                """;
        assertTrue(verifier.equivalent(protocol_1, protocol_2));
    }

    /**
     * two protocols are not equivalent
     */
    @Test
    public void notEquivalent(){
        String protocol_1 = """
                m["x"]@2 := (s["x"] + r["x"] + r["loc"])@1;
                m["x"]@3 := r["x"]@1
                """;
        String protocol_2 = """
                r["x"]@1 := m["x"]@3
                """;
        assertFalse(verifier.equivalent(protocol_1, protocol_2));
    }

    /**
     * The truth value when nothing entails some predicate depends on the
     * truth value of the predicate.
     */
    @Test
    public void nothingEntailsSomething() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        Term trueTerm = factory.toTerm(Loader.toCommand("out@1 := 1@1"));
        assertTrue(verifier.entails(Collections.emptyList(), trueTerm));
        Term falseTerm = factory.toTerm(Loader.toCommand("out@1 := 1@1; out@1 := 2@1"));
        assertFalse(verifier.entails(Collections.emptyList(), falseTerm));
    }

    /**
     * The truth value when some predicate entails nothing is always true.
     */
    @Test
    public void somethingEntailsNothing() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Sort sort = termManager.mkFiniteFieldSort("7", 10);
        TermFactory factory = new TermFactory(termManager, sort);
        Term trueTerm = factory.toTerm(Loader.toCommand("out@1 := 1@1"));
        assertTrue(verifier.entails(trueTerm, null));
        Term falseTerm = factory.toTerm(Loader.toCommand("out@1 := 1@1; out@1 := 2@1"));
        assertTrue(verifier.entails(falseTerm, null));
    }

    /**
     * Overture protocols with only one command in them can be satisfied.
     */
    @Test
    public void oneCommandProtocolSatisfaction() {
        PreludeCommand command = Loader.toCommand("out@1 := 1@1");
        assertTrue(command instanceof AssignCommand);
        assertTrue(verifier.satisfies(command));
    }
    

}



