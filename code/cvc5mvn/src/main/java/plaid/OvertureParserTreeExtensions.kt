package plaid

import plaid.OvertureParser.CommandContext
import plaid.OvertureParser.AtpartyContext
import plaid.OvertureParser.ProtocolContext
import plaid.OvertureParser.MessageMemoryContext
import plaid.OvertureParser.RandomMemoryContext
import plaid.OvertureParser.PublicMemoryContext
import plaid.OvertureParser.SecretMemoryContext

fun CommandContext.destinationPartyId() =
    dest().atparty().partyId()

fun CommandContext.sourcePartyId() =
    source().atparty().partyId()

fun AtpartyContext.partyId(): String =
    party().VALUE().text

fun ProtocolContext.assignmentCount() =
    assignment().size

fun ProtocolContext.firstAssignment(): CommandContext =
    this.
    command().first()

fun MessageMemoryContext.location(): String =
    index().STRING().text

fun RandomMemoryContext.location(): String =
    index().STRING().text

fun SecretMemoryContext.location(): String =
    index().STRING().text