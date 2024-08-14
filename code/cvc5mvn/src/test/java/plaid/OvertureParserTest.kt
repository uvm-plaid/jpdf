package plaid

import junit.framework.TestCase.assertEquals
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.junit.Test

class OvertureParserTest : AbstractOvertureTest() {

    /**
     * Protocols that do not have any commands do not parse.
     */
    @Test(expected = ParseCancellationException::class)
    fun commandCount0() {
        loadProtocol("")
    }

    /**
     * Protocols that have one command parse to an AST that has one
     * command.
     */
    @Test
    fun commandCount1() {
        val protocol = loadProtocol(""" out@1 := s["x"]@1 """)
        assertEquals(1, protocol.commandCount())
    }

    /**
     * Protocols that have more than one command parse to an AST that has
     * one that same number of commands.
     */
    @Test
    fun commandCount2() {
        val protocol = loadProtocol("out@1 := 18@1; out @ 2 := 18 @ 2")
        assertEquals(2, protocol.commandCount())
    }

    /**
     * The AST for a command contains the correct source and destination
     * parties.
     */
    @Test
    fun commandParties() {
        val protocol = loadProtocol("out @ 1 := 18 @ 4")
        val command = protocol.firstCommand()
        assertEquals("1", command.dest().partyId())
        assertEquals("4", command.source().partyId())
    }
}