package plaid

import plaid.OvertureParser.AssignmentContext
import plaid.OvertureParser.AtpartyContext
import plaid.OvertureParser.ProgramContext
import plaid.OvertureParser.MessageMemoryContext
import plaid.OvertureParser.RandomMemoryContext
import plaid.OvertureParser.PublicMemoryContext
import plaid.OvertureParser.SecretMemoryContext

fun AssignmentContext.destinationPartyId() =
    dest().atparty().partyId()

fun AssignmentContext.sourcePartyId() =
    source().atparty().partyId()

fun AtpartyContext.partyId(): String =
    party().VALUE().text

fun ProgramContext.assignmentCount() =
    assignment().size

fun ProgramContext.firstAssignment(): AssignmentContext =
    assignment().first()

fun MessageMemoryContext.location(): String =
    index().IDENTIFIER().text

fun PublicMemoryContext.location(): String =
    index().IDENTIFIER().text

fun RandomMemoryContext.location(): String =
    index().IDENTIFIER().text

fun SecretMemoryContext.location(): String =
    index().IDENTIFIER().text