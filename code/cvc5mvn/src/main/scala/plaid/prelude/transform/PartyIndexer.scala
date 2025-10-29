package plaid.prelude.transform

import plaid.prelude.ast.*
import plaid.prelude.prettyPrint
import plaid.prelude.transform.Evaluator

object PartyIndexer {

  /** Append party index to expressions for arithmetic operations on memories */
  def appendPartyIndex(expression: Expr, partyIndex: Expr): Expr = expression match
    case e: AtExpr => appendPartyIndex(e.e1, e.e2)
    case e: RandomExpr => AtExpr(e, partyIndex)
    case e: SecretExpr => AtExpr(e, partyIndex)
    case e: MessageExpr => AtExpr(e, partyIndex)
    case e: OutputExpr => AtExpr(e, partyIndex)
    case e: MinusExpr => MinusExpr(appendPartyIndex(e.e, partyIndex))
    case e: TimesExpr => TimesExpr(appendPartyIndex(e.e1, partyIndex), appendPartyIndex(e.e2, partyIndex))
    case e: PlusExpr => PlusExpr(appendPartyIndex(e.e1, partyIndex), appendPartyIndex(e.e2, partyIndex))
    case e: OTExpr =>
      OTExpr(
        appendPartyIndex(e.e1, e.i1),
        e.i1,
        appendPartyIndex(e.e2, partyIndex),
        appendPartyIndex(e.e3, partyIndex)
      )
    case e: OTFourExpr =>
      OTFourExpr(
        appendPartyIndex(e.s1, e.i1),
        appendPartyIndex(e.s2, e.i1),
        e.i1,
        appendPartyIndex(e.e1, partyIndex),
        appendPartyIndex(e.e2, partyIndex),
        appendPartyIndex(e.e3, partyIndex),
        appendPartyIndex(e.e4, partyIndex)
      )
    case e: Expr => e
}
