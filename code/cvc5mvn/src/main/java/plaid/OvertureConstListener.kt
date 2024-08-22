package plaid

import io.github.cvc5.Sort
import io.github.cvc5.TermManager
import org.antlr.v4.runtime.RuleContext
import plaid.OvertureParser.DestContext
import plaid.OvertureParser.SecretMemoryContext
import plaid.OvertureParser.RandomMemoryContext
import plaid.OvertureParser.MessageMemoryContext
import plaid.OvertureParser.PublicMemoryContext
import plaid.OvertureParser.OutputMemoryContext
import plaid.OvertureParser.SourceContext

/**
 * Identifies all the memory locations in an Overture parse tree and registers
 * them as constants in a cvc5 solver.
 */
class OvertureConstListener(
    private val termManager: TermManager,
    private val sort: Sort
) : OvertureBaseListener() {

    private var partyId = ""
    private val bindings = HashMap<String, Memory>()

    private fun register(context: RuleContext, type: String, id: String) {
        val cvcId =
            if (id == "") "${type}_${partyId}"
            else if (type == "p") "${type}_${id.drop(1).dropLast(1)}_${partyId.drop(1)}"
            else "${type}_${id.drop(1).dropLast(1)}_${partyId}"

        val memory = bindings.getOrPut(cvcId) { Memory(cvcId, termManager.mkConst(sort, cvcId)) }
        memory.contexts.add(context)
    }

    fun memories() = bindings.values

    override fun enterDest(ctx: DestContext) {
        partyId = ctx.partyId()
    }

    override fun enterSource(ctx: SourceContext) {
        partyId = ctx.partyId()
    }

    override fun enterSecretMemory(ctx: SecretMemoryContext) {
        register(ctx, "s", ctx.location())
    }

    override fun enterRandomMemory(ctx: RandomMemoryContext) {
        register(ctx, "r", ctx.location())
    }

    override fun enterMessageMemory(ctx: MessageMemoryContext) {
        register(ctx, "m", ctx.location())
    }

    override fun enterPublicMemory(ctx: PublicMemoryContext) {
        register(ctx, "p", ctx.location())
    }

    override fun enterOutputMemory(ctx: OutputMemoryContext) {
        register(ctx, "out", "")
    }
}