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
        assertTrue(Verifier.satisfies("out@1 := 1; out@2 := 2"));
    }

    /**
     * Fails to verify a simple contradictory protocol.
     */
    @Test
    public void failIncorrectProtocol() {
        assertFalse(Verifier.satisfies("out@1 := 1; out@1 := 2"));
    }

    /**
     * Verifies a protocol that has an assertion.
     */
    @Test
    public void assertionProtocol() {
        assertTrue(Verifier.satisfies("out@1 := 1; assert (out = 1)@1"));
    }

    /**
     * Fails to verify a protocol that has a contradiction caused by an assertion.
     */
    @Test
    public void failAssertionProtocol() {
        assertFalse(Verifier.satisfies("out@1 := 1; assert (out = 2)@1"));
    }

}
