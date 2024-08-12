package plaid

import junit.framework.TestCase.assertEquals
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.junit.Test

class OvertureParserTest : AbstractOvertureTest() {

    /**
     * Protocols that do not have any assignments do not parse.
     */
    @Test(expected = ParseCancellationException::class)
    fun assignmentCount0() {
        loadProgram("")
    }

    /**
     * Protocols that have one assignment parse to an AST that has one
     * assignment.
     */
    @Test
    fun assignmentCount1() {
        val program = loadProgram("out@1 := 18@1")
        assertEquals(1, program.assignmentCount())
    }

    /**
     * Protocols that have more than one assignment parse to an AST that has
     * one that same number of assignments.
     */
    @Test
    fun assignmentCount2() {
        val program = loadProgram("out@1 := 18@1; out @ 2 := 18 @ 2")
        assertEquals(2, program.assignmentCount())
    }

    /**
     * The AST for an assignment contains the correct source and destination
     * parties.
     */
    @Test
    fun assignmentParties() {
        val program = loadProgram("out @ 1 := 18 @ 4")
        val assignment = program.firstAssignment()
        assertEquals("1", assignment.destinationPartyId())
        assertEquals("4", assignment.sourcePartyId())
    }
}