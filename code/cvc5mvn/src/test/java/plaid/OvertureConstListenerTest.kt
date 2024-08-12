package plaid

import io.github.cvc5.Solver
import jdk.jshell.spi.ExecutionControl.NotImplementedException
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
        val program = loadProgram("out@1 := s[x]@1")
        val solver = Solver()
        val sort = solver.mkFiniteFieldSort("7", 10)
        val listener = OvertureConstListener(solver, sort)
        ParseTreeWalker().walk(listener, program)

        val cvcIds = listener.memories().map { it.name }
        assertEquals(setOf("out_1", "s_x_1"), cvcIds.toSet())
    }

    /**
     * When an AST context from an assignment destination references the same
     * location in memory as an AST context from an assignment source, the two
     * contexts share the same Memory object.
     */
    @Test
    fun memoryReuse() {
        val program = loadProgram("out@1 := s[x]@1 + s[x]@1")
        val solver = Solver()
        val sort = solver.mkFiniteFieldSort("7", 10)
        val listener = OvertureConstListener(solver, sort)
        ParseTreeWalker().walk(listener, program)

        val memories = listener.memories()
        assertEquals(2, memories.size)

        val cvcIds = memories.map { it.name }
        assertEquals(setOf("out_1", "s_x_1"), cvcIds.toSet())

        val contextCounts = memories.map { it.contexts.size }
        assertEquals(setOf(1, 2), contextCounts.toSet())
    }
}