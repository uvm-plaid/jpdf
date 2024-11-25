package plaid.cvc;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import plaid.antlr.ConstraintsLoader;
import plaid.antlr.Loader;
import plaid.ast.PreludeCommand;
import plaid.ast.Program;
import plaid.constraints.ast.Constraints;
import plaid.eval.ProgramEvaluator;

public class VerifierTest {
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
        assertTrue(Verifier.satisfies("out@1 := 1@1; out@2 := 2@2"));
    }

    /**
     * Fails to satisfy a simple contradictory protocol.
     */
    @Test
    public void failIncorrectProtocol() {
        assertFalse(Verifier.satisfies("out@1 := 1@1; out@1 := 2@1"));
    }

    /**
     * satisfies a protocol that has an assertion.
     */
    @Test
    public void assertionProtocol() {
        assertTrue(Verifier.satisfies("out@1 := 1@1; assert (out = 1)@1"));
    }

    /**
     * Fails to satisfy a protocol that has a contradiction caused by an assertion.
     */
    @Test
    public void failAssertionProtocol() {
        assertFalse(Verifier.satisfies("out@1 := 1@1; assert (out = 2)@1"));
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
                constraints: (out@1 == s["x"]@1 + s["y"]@2) AND (out@2 == s["x"]@1 + s["y"]@2)
                """;
        assertTrue(Verifier.satisfies(protocol));
        assertTrue(Verifier.verifies(protocol, proposition));
    }


    /**
     * an overture protocol verifies a correct proposition
     */
    @Test
    public void verifiesCorrectProposition(){
        String protocol = """
                m["x"]@2 := (s["x"] + r["x"] + r["loc"])@1;
                m["x"]@3 := r["x"]@1
                """;
        String proposition = """
                constraints: r["x"]@1 == m["x"]@3
                """;

        assertTrue(Verifier.verifies(protocol, proposition));
    }

    /**
     * three-party addition
     */
    @Test
    public void verifiesThreePartyAddition(){
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
                constraints: out@1 == s["1"]@1 + s["2"]@2 + s["3"]@3
                """;
        String proposition_2 = """
                constraints: out@2 == s["1"]@1 + s["2"]@2 + s["3"]@3
                """;
        String proposition_3 = """
                constraints: out@3 == s["1"]@1 + s["2"]@2 + s["3"]@3
                """;
        String proposition_4 = """
                constraints:
                out@1 == s["1"]@1 + s["2"]@2 + s["3"]@3 AND
                out@2 == s["1"]@1 + s["2"]@2 + s["3"]@3 AND
                out@3 == s["1"]@1 + s["2"]@2 + s["3"]@3
                """;
        assertTrue(Verifier.verifies(protocol, proposition_1));
        assertTrue(Verifier.verifies(protocol, proposition_2));
        assertTrue(Verifier.verifies(protocol, proposition_3));
        assertTrue(Verifier.verifies(protocol, proposition_4));

        String proposition_5 = """
                constraints:
                out@1 == s["1"]@1 + s["2"]@2 + s["3"]@3 AND
                out@2 == s["1"]@1 + s["2"]@2 + s["3"]@3 AND
                (NOT (out@3 == s["1"]@1 + s["2"]@2 + s["3"]@3))
                """;
        String proposition_6 = """
                constraints: out@2 == s["1"]@1 + s["2"]@2 + r["x"]@3
                """;
        assertFalse(Verifier.verifies(protocol, proposition_5));
        assertFalse(Verifier.verifies(protocol, proposition_6));
    }

    /**
     * a protocol does not verify a contradictory proposition
     */
    @Test
    public void verifiesIncorrectProposition(){
        String protocol = """
                m["x"]@2 := (s["x"] + r["x"] + r["loc"])@1;
                m["x"]@3 := r["x"]@1
                """;
        String proposition_1 = """
                constraints: r["x"]@1 == m["x"]@2
                """;

        String proposition_2 = """
                constraints: r["loc"]@1 == m["x"]@3
                """;

        assertFalse(Verifier.verifies(protocol, proposition_1));
        assertFalse(Verifier.verifies(protocol, proposition_2));

    }


    /**
     * a prelude program evaluates to overture protocol, and
     * the protocol verifies a correct proposition
     */
    @Test
    public void evaluatesAndverifiesCorrectProposition(){
        String program = """
                exprfunctions:
                not(x){
                    x + 1
                }
                
                mux4(s1, s2, b1, b2, b3, b4){
                    ((s1 * s2) * b4) +
                    ((not(s1) * s2) * b3) +
                    ((s1 * not(s2)) * b2) +
                    ((not(s1) * not(s2)) * b1)
                }
                
                andtablegmw(x, y, z) {
                    let r11 = r[z] + (m[x] + 1) * (m[y] + 1) in
                    let r10 = r[z] + (m[x] + 1) * (m[y] + 0) in
                    let r01 = r[z] + (m[x] + 0) * (m[y] + 1) in
                    let r00 = r[z] + (m[x] + 0) * (m[y] + 0) in
                    { row1 = r11; row2 = r10; row3 = r01; row4 = r00 }
                }  
                
                cmdfunctions:
                andgmw(z, x, y) {
                    let table = andtablegmw(x,y,z) in
                    m[x]@1 := m[x]@2;
                    m[y]@1 := m[y]@2;
                    m[z]@2 := mux4(m[x], m[y], table.row4, table.row3, table.row2, table.row1)@1;
                    m[z]@1 := r[z]@1
                }
                
                main(){andgmw("g1","x","z")}
                """;
        // evaluate the program to overture protocol
        PreludeCommand protocol = evaluates(program);

        String evaluated_protocol = """
                m["x"]@1 := m["x"]@2;
                m["z"]@1 := m["z"]@2;
                m["g1"]@2 := (((((m["x"] * m["z"]) * (r["g1"] + ((m["x"] + 0) * (m["z"] + 0)))) +
                        (((m["x"]+1) * m["z"]) * (r["g1"] + ((m["x"] + 0) * (m["z"] + 1))))) +
                        ((m["x"] * (m["z"]+1)) * (r["g1"] + ((m["x"] + 1) * (m["z"] + 0))))) +
                        (((m["x"]+1) * (m["z"]+1)) * (r["g1"] + ((m["x"] + 1) * (m["z"] + 1)))))@1;
                m["g1"]@1 := r["g1"]@1
                """;

        // check if the protocol verifies a correct proposition
        String proposition_src = """
                constraints: (m["g1"]@1 + m["g1"]@2) == ((m["x"]@1 + m["x"]@2) * (m["z"]@1 + m["z"]@2))
                """;

        Constraints proposition = ConstraintsLoader.toConstraint(proposition_src);
        //assertTrue(Verifier.verifies(evaluated_protocol, proposition_src));
        assertTrue(Verifier.verifies(protocol, proposition));
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
        assertTrue(Verifier.equivalent(protocol_1, protocol_2));
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
        assertFalse(Verifier.equivalent(protocol_1, protocol_2));
    }
}


// TODO: what we want to test
// Prelude Command


// Overture Protocol


// proposition


// evaluate prelude command
// convert overture protocol into cvc5 language
// convert constraint (proposition) into cvc5 language
