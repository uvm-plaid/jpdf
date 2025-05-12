package plaid.eval;

import org.junit.Test;
import plaid.antlr.Loader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConstraintCheckerTest {

    private void assertPasses(String src) {assertTrue(ConstraintChecker.checkConstraint(Loader.toConstraintExpression(src)));}

    private void assertFails(String src) {assertFalse(ConstraintChecker.checkConstraint(Loader.toConstraintExpression(src)));}

    /**
     * Memories should always come with party indexes.
     */
    @Test
    public void validMemories(){
        assertFails("m[\"m\"]@2 == m[\"k\"]@1 + (m[\"delta\"]@1 * m[\"s\"])@2");
        assertFails("out@1 == (m[\"x\"] + m[\"y\"])@1");
        assertPasses("m[\"m\"]@2 == m[\"k\"]@1 + (m[\"delta\"]@1 * m[\"s\"]@2)");
    }

    /**
     * Constraint expression should contain only the types of nodes that are allowed in the grammar.
     */
    @Test
    public void constraintNodeTypesOnly(){
        // No field and Let
        assertFails("{ mesg= m[\"x\"] ; rand = r[\"x\"] }.mesg@1  ==  m[\"x\"]@1");
        assertFails("let x = \"foo\" in m[x]@1 == m[x]@1");
        assertPasses("m[\"x\"]@1  ==  m[\"x\"]@1");
    }

    /**
     * Party Indexes should be numbers
     */
    @Test
    public void partyIndexNumbers(){
        assertFails("s[\"w\"]@i == p[\"w\"]@1");
        assertPasses("s[\"w\"]@1 == p[\"w\"]@1");
    }

    /**
     *  Memory indexes should always be strings.
     */
    @Test
    public void memoryIndexesStrings(){
        assertFails("out@1 == m[x ++ \"foo\"]@1");
        assertFails("out@1 == m[x]@1");
        assertFails("out@1 == m[m[x ++ \"foo\"]]@1");
        assertPasses("out@1 == m[\"xfoo\"]@1");
    }

    /**
     * At expressions cannot  be nested inside one another
     */
    @Test
    public void nestedAts(){
        assertPasses("out@1 == (m[\"x\"]@1 + m[\"y\"]@1)");
        assertFails("out@1 == (m[\"x\"]@1 + m[\"y\"]@1)@1");
    }


}
