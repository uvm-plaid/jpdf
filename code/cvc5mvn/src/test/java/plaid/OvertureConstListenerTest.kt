package plaid

import io.github.cvc5.Solver
import junit.framework.TestCase.assertEquals
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.junit.Test

class OvertureConstListenerTest : AbstractOvertureTest() {

    /**
     * Walking an assignment captures the destination and source memory
     * locations as cvc5 constants.
     */
    @Test
    fun constantNames() {
        val src =
            """
            out@1 := s[x]@1
            """

        val program = loadProgram(src)
        val solver = Solver()
        val sort = solver.mkFiniteFieldSort("7", 10)
        val listener = OvertureConstListener(solver, sort)
        ParseTreeWalker().walk(listener, program)

        val cvcIds = listener.memories().map { it.name }.toSet()
        assertEquals(setOf("out_1", "s_x_1"), cvcIds)
    }
}