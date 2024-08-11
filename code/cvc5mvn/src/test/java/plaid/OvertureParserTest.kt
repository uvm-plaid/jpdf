package plaid

import junit.framework.TestCase.assertEquals
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.junit.Test

class OvertureParserTest : AbstractOvertureTest() {

    /**
     * Programs must have at least one assignment.
     */
    @Test(expected = ParseCancellationException::class)
    fun assignmentCount0() {
        loadProgram("")
    }

    /**
     * Recognizes programs that have one assignment.
     */
    @Test
    fun assignmentCount1() {
        val program = loadProgram("out @ 1 := 18 @ 1")
        assertEquals(1, program.assignmentCount())
    }

    /**
     * Recognizes programs that have multiple assignments.
     */
    @Test
    fun assignmentCount2() {
        val src =
            """
            out @ 1 := 18 @ 1
            out @ 2 := 18 @ 2
            """

        val program = loadProgram(src)
        assertEquals(2, program.assignmentCount())
    }

    /**
     * Recognizes the source and destination parties in an assignment.
     */
    @Test
    fun assignmentParties() {
        val program = loadProgram("out @ 1 := 18 @ 4")
        val assignment = program.firstAssignment()
        assertEquals("1", assignment.destinationPartyId())
        assertEquals("4", assignment.sourcePartyId())
    }
}