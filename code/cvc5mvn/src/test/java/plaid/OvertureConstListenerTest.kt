package plaid

import io.github.cvc5.TermManager
import junit.framework.TestCase.assertEquals
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.junit.Test

//class OvertureConstListenerTest : AbstractOvertureTest() {
//
//    /**
//     * Walking a command captures the destination and source memory locations
//     * as cvc5 constants.
//     */
//    @Test
//    fun constantNames() {
//        val protocol = loadProtocol(""" out@1 := s["x"]@1 """)
//        val termManager = TermManager()
//        val sort = termManager.mkFiniteFieldSort("7", 10)
//        val listener = OvertureConstListener(termManager, sort)
//        ParseTreeWalker().walk(listener, protocol)
//
//        val cvcIds = listener.memories().map { it.name }
//        assertEquals(setOf("out_1", "s_x_1"), cvcIds.toSet())
//    }
//
//    /**
//     * When an AST context from an assignment destination references the same
//     * location in memory as an AST context from an assignment source, the two
//     * contexts share the same Memory object.
//     */
//    @Test
//    fun memoryReuse() {
//        val protocol = loadProtocol(""" out@1 := (s["x"] + s["x"])@1 """)
//        val termManager = TermManager()
//        val sort = termManager.mkFiniteFieldSort("7", 10)
//        val listener = OvertureConstListener(termManager, sort)
//        ParseTreeWalker().walk(listener, protocol)
//
//        val memories = listener.memories()
//        assertEquals(2, memories.size)
//
//        val cvcIds = memories.map { it.name }
//        assertEquals(setOf("out_1", "s_x_1"), cvcIds.toSet())
//
//        val contextCounts = memories.map { it.contexts.size }
//        assertEquals(setOf(1, 2), contextCounts.toSet())
//    }
//}