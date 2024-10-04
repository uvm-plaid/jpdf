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

}
