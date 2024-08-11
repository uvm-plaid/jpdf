package plaid

import plaid.OvertureParser.AssignmentContext
import plaid.OvertureParser.AtpartyContext
import plaid.OvertureParser.ProgramContext

fun AssignmentContext.destinationPartyId() =
    dest().atparty().partyId()

fun AssignmentContext.sourcePartyId() =
    atparty().partyId()

fun AtpartyContext.partyId(): String =
    party().VALUE().text

fun ProgramContext.assignmentCount() =
    assignment().size

fun ProgramContext.firstAssignment(): AssignmentContext =
    assignment().first()