package plaid

import io.github.cvc5.Term
import org.antlr.v4.runtime.RuleContext

data class Memory(
    val name: String,
    val term: Term,
    val contexts: MutableSet<RuleContext> = HashSet())