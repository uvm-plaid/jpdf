package plaid

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BailErrorStrategy
import org.antlr.v4.runtime.CommonTokenStream
import plaid.OvertureParser.ProtocolContext

abstract class AbstractOvertureTest {

    fun loadProtocol(src: String): ProtocolContext {
        val input = ANTLRInputStream(src)
        val lexer = OvertureLexer(input)
        val tokens = CommonTokenStream(lexer)
        val parser = OvertureParser(tokens)
        parser.buildParseTree = true
        parser.errorHandler = BailErrorStrategy()

        return parser.protocol()
    }
}