package plaid

import org.antlr.v4.runtime.tree.ParseTree
import plaid.OvertureParser.CommandContext
import plaid.OvertureParser.ProtocolContext
import plaid.OvertureParser.MessageMemoryContext
import plaid.OvertureParser.RandomMemoryContext
import plaid.OvertureParser.SecretMemoryContext
import plaid.OvertureParser.DestContext
import plaid.OvertureParser.OutputMemoryContext
import plaid.OvertureParser.SourceContext
import plaid.OvertureParser.PublicMemoryContext

//fun ParseTree.children() =
//    (0 .. childCount).map { getChild(it) }
//
//fun DestContext.partyId(): String =
//    if (atparty() == null) "" //publicloc
//    else atparty().VALUE().text
//
//fun SourceContext.partyId(): String =
//    atparty().VALUE().text
//
//fun ProtocolContext.commands() =
//    children().filterIsInstance<CommandContext>()
//
//fun ProtocolContext.commandCount() =
//    commands().size
//
//fun ProtocolContext.firstCommand() =
//    commands().first()
//
//fun MessageMemoryContext.location(): String =
//    index().STRING().text
//
//fun RandomMemoryContext.location(): String =
//    index().STRING().text
//
//fun SecretMemoryContext.location(): String =
//    index().STRING().text
//
//fun PublicMemoryContext.location() : String =
//    index().STRING().text
//
