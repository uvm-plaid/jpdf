package plaid.cvc;

import org.junit.Test;
import plaid.constraints.ast.*;

import static org.junit.Assert.assertEquals;

public class ConstraintsCvcUtilsTest {

    /**
     * Produces cvc5 constant names for all memory types
     */
    @Test
    public void memNames(){
        assertEquals("m_x_4", ConstraintsCvcUtils.getConstraintsCvcName(new MessageConstraintsTerm("x", 4)));
        assertEquals("o_3", ConstraintsCvcUtils.getConstraintsCvcName(new OutputConstraintTerm(3)));
        assertEquals("p_x", ConstraintsCvcUtils.getConstraintsCvcName(new PublicConstraintsTerm("x")));
        assertEquals("r_x_1", ConstraintsCvcUtils.getConstraintsCvcName(new RandomConstraintsTerm("x", 1)));
        assertEquals("s_x_5", ConstraintsCvcUtils.getConstraintsCvcName(new SecretConstraintsTerm("x", 5)));
    }

    /**
     * Party indices in the AST that are not numbers do not evaluate to integers
     */


    /**
     * Memory identifiers in the AST that are not strings do not evaluate to strings
     */
}
