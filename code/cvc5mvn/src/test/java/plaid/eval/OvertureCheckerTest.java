package plaid.eval;

import org.junit.Test;
import plaid.antlr.Loader;
import plaid.ast.PreludeCommand;
import plaid.ast.Program;
import scala.concurrent.impl.FutureConvertersImpl;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OvertureCheckerTest {

    private void assertPasses(String src) {
        assertTrue(OvertureChecker.checkOverture(Loader.toCommand(src)));
    }

    private void assertFails(String src) {
        assertFalse(OvertureChecker.checkOverture(Loader.toCommand(src)));
    }

    /**
     * At expressions should have numbers for party indexes.
     */
    @Test
    public void partyIndexNumbers() {
        // On the right of assignments
        assertPasses("p[\"w\"] := m[\"x\"]@2");
        assertFails("p[\"w\"] := m[\"x\"]@\"y\"");
        // On the left of assignments
        assertPasses("m[\"x\"]@2 := s[\"y\"]@1");
        assertFails("m[\"x\"]@2 := s[\"y\"]@\"z\"");
        // On asserts
        assertPasses("assert (m[\"x\"] = 13)@1");
        assertFails("assert (m[\"x\"] = 13)@\"z\"");
    }

    /**
     * Overture protocols should contain only the types of nodes that are
     * allowed in overture.
     */
    @Test
    public void overtureNodeTypesOnly() {
        // No identifiers
        assertPasses("out@1 := (4 + 2)@1");
        assertFails("out@1 := (4 + x)@1");
        // No concatenation
        assertPasses("out@1 := (m[\"x\"] + m[\"y\"])@1");
        assertFails("out@1 := (m[\"x\"] ++ m[\"y\"])@1");
    }

    /**
     * Memory indexes should always be strings.
     */
    @Test
    public void memoryIndexesStrings() {
        assertPasses("out@1 := m[\"x\"]@1");
        assertFails("out@1 := m[13]@1");
        assertFails("out@1 := m[m[\"x\"]]@1");
    }

    /**
     * The outermost expression on the right side of an assignment is at At
     * expression.
     */
    @Test
    public void rightSideAts() {
        // For expressions with memory ownership
        assertPasses("p[\"x\"] := m[\"y\"]@1");
        assertFails("p[\"x\"] := m[\"y\"]");
        // Even for constants
        assertPasses("p[\"x\"] := 4@1");
        assertFails("p[\"x\"] := 4");
    }

    /**
     * No one besides the party can write to the party's output memory.
     */
    @Test
    public void restrictedOutMemoryAccess() {
        assertPasses("out@1 := (m[\"x\"] + m[\"y\"])@1");
        assertFails("out@1 := (m[\"x\"] + m[\"y\"])@2");
    }

    /**
     * At expressions cannot be nested inside one another.
     */
    @Test
    public void nestedAts() {
        assertPasses("out@1 := (m[\"x\"] + m[\"y\"])@1");
        assertFails("out@1 := (m[\"x\"]@1 + m[\"y\"]@1)@1");
    }

    /**
     * The left hand side of assignments must be an output memory, a message
     * memory, a public memory, and nothing else.
     */
    @Test
    public void assignmentLeftSide() {
        assertPasses("out@1 := 13@1");
        assertPasses("m[\"x\"]@1 := 13@1");
        assertPasses("p[\"x\"] := 13@1");
        // Output memory must have party index
        assertFails("out := 13@1");
        // Message memory must have party index
        assertFails("m[\"x\"] := 13@1");
        // Public memory must not have party index
        assertFails("p[\"x\"]@1 := 13@1");
        // Nothing else should be allowed
        assertFails("s[\"x\"]@1 := 13@1");
        assertFails("r[\"x\"]@1 := 13@1");
        assertFails("13@1 := 13@1");
        assertFails("(p[\"x\"] + p[\"y\"])@1 := 13@1");
    }

    /**
     * OT allows nested At expressions
     */
    @Test
    public void OTNestedAtExpr(){
        assertPasses("m[\"x\"]@1 := OT(s[\"foo\"]@1, m[\"bar\"], m[\"zoo\"])@2");
    }

    /**
     * OT requires the same party index for the receiver
     */
    @Test
    public void OTPartyIndexMatch(){
        assertPasses("m[\"x\"]@1 := OT(s[\"foo\"]@1, m[\"bar\"], m[\"zoo\"])@2");
        assertFails("m[\"x\"]@1 := OT(s[\"foo\"]@3, m[\"bar\"], m[\"zoo\"])@2");
        assertFails("m[\"x\"]@1 := OT(s[\"foo\"]@j, m[\"bar\"], m[\"zoo\"])@2");
        assertFails("m[\"x\"]@1 := OT(s[\"foo\"]@y, m[\"bar\"], m[\"zoo\"])@2");
    }

    /**
     * OT4 requires the same party index for the receiver
     */
    @Test
    public void OTFourPartyIndexMatch(){
        assertPasses("m[\"x\"]@1 := OT4((m[\"s1\"], m[\"s2\"])@1, s[\"x\"], s[\"y\"], s[\"z\"], s[\"t\"])@2");
        assertFails("m[\"x\"]@1 := OT4((m[\"s1\"], m[\"s2\"])@3, s[\"x\"], s[\"y\"], s[\"z\"], s[\"t\"])@2");
        assertFails("m[\"x\"]@1 := OT4((m[\"s1\"], m[\"s2\"])@s, s[\"x\"], s[\"y\"], s[\"z\"], s[\"t\"])@2");
        assertFails("m[\"x\"]@1 := OT4((m[\"s1\"], m[\"s2\"])@y, s[\"x\"], s[\"y\"], s[\"z\"], s[\"t\"])@2");

    }

    /**
     * OT does not allow non-overture expression as subexpressions
     */
    @Test
    public void OTValidSubExpr(){
        assertPasses("m[\"x\"]@1 := OT(s[\"foo\"]@1, m[\"bar\"]+p[\"y\"], m[\"zoo\"])@2");
        assertPasses("m[\"x\"]@1 := OT(s[\"foo\"]@1, m[\"bar\"]+-p[\"y\"], m[\"zoo\"])@2");
        assertFails("m[\"x\"]@1 := OT(s[\"foo\"]@1, m[\"bar\" ++ \"y\"], m[\"zoo\"])@2");
        assertPasses("m[\"x\"]@1 := OT(s[\"foo\"]@1, m[\"bar\"], m[\"zoo\"]*p[\"y\"])@2");

    }

    /**
     * OT4 does not allow non-overture expression as subexpressions
     */
    @Test
    public void OTFourValidSubExpr(){
        assertPasses("m[\"x\"]@1 := OT4((m[\"s1\"], m[\"s2\"])@1, s[\"x\"]+r[\"bar\"], s[\"y\"], s[\"z\"], s[\"t\"])@2");
        assertPasses("m[\"x\"]@1 := OT4((m[\"s1\"], m[\"s2\"])@1, s[\"y\"], s[\"x\"]*r[\"bar\"], s[\"z\"], s[\"t\"])@2");
        assertFails("m[\"x\"]@1 := OT4((m[\"s1\"], m[\"s2\"])@1, s[\"x\" ++ \"bar\"], s[\"y\"], s[\"z\"], s[\"t\"])@2");

    }
}
