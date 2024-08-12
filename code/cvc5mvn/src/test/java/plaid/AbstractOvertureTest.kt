package plaid

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BailErrorStrategy
import org.antlr.v4.runtime.CommonTokenStream
import plaid.OvertureParser.ProtocolContext

abstract class AbstractOvertureTest {

    fun loadProgram(src: String): ProtocolContext {
        val input = ANTLRInputStream(src)
        val lexer = OvertureLexer(input)
        val tokens = CommonTokenStream(lexer)
        val parser = OvertureParser(tokens)
        parser.buildParseTree = true
        // TODO Would be nice to see error AND throw exception...
        parser.errorHandler = BailErrorStrategy()

        val p = parser.protocol()
        p.
        return parser.protocol()
    }
}