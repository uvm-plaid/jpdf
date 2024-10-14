package plaid.cvc;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VerifierTest {

    /**
     * Verifies a simple protocol.
     */
    @Test
    public void verifyCorrectProtocol() {
        assertTrue(Verifier.verify("out@1 := 1; out@2 := 2"));
    }

    /**
     * Fails to verify a simple contradictory protocol.
     */
    @Test
    public void failIncorrectProtocol() {
        assertFalse(Verifier.verify("out@1 := 1; out@1 := 2"));
    }

    /**
     * Verifies a protocol that has an assertion.
     */
    @Test
    public void assertionProtocol() {
        assertTrue(Verifier.verify("out@1 := 1; assert (out = 1)@1"));
    }

    /**
     * Fails to verify a protocol that has a contradiction caused by an assertion.
     */
    @Test
    public void failAssertionProtocol() {
        assertFalse(Verifier.verify("out@1 := 1; assert (out = 2)@1"));
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

        assertTrue(Verifier.verify(protocol));
    }

}
