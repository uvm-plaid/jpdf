package plaid

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BailErrorStrategy
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.junit.Assert
import org.junit.Test
import plaid.OvertureParser.ProgramContext

class OvertureParserTest {

    private fun loadProgram(src: String): ProgramContext {
        val input = ANTLRInputStream(src)
        val lexer = OvertureLexer(input)
        val tokens = CommonTokenStream(lexer)
        val parser = OvertureParser(tokens)
        parser.buildParseTree = true
        // TODO Would be nice to see error AND throw exception...
        parser.errorHandler = BailErrorStrategy()
        return parser.program()
    }

    /**
     * Programs must have at least one assignment.
     */
    @Test(expected = ParseCancellationException::class)
    fun empty() {
        loadProgram("")
    }

    /**
     * Nonsensical assignment but similar to example in Section 2.3.
     */
    @Test
    fun assignOutput() {
        // TODO This line doesn't work when using IDENTIFIER instead of VALUE
        //ProgramContext program = loadProgram("out @ 1 := (p[1] + p[2] + p[3]) @ 4");
        val program = loadProgram("out @ 1 := (p[1] + p[2] + p[3]) @ 4")
        val assignments = program.assignment()
        Assert.assertEquals("Program should have one assignment", 1, assignments.size)

        val assignment = program.assignment().first()
        Assert.assertEquals("4", assignment.sourceParty())
    }
}
