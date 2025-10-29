package plaid.prelude.cvc

import io.github.cvc5.Term
import plaid.prelude.ast.Node

case class Memory(name: String, term: Term, node: Node, partyIndex: Integer)
