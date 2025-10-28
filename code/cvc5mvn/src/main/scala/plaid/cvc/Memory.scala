package plaid.cvc

import io.github.cvc5.Term
import plaid.ast.Node

case class Memory(name: String, term: Term, node: Node, partyIndex: Integer)
