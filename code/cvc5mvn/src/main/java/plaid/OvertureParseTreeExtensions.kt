package plaid

import plaid.OvertureParser.AssignmentContext

fun AssignmentContext.sourceParty() = this.atparty().party().VALUE().text
