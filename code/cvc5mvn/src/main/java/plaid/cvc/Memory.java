package plaid.cvc;

import io.github.cvc5.Term;
import plaid.ast.MemoryExpr;
import plaid.ast.Node;



public record Memory(String name, Term term, Node node, Integer partyIndex) {

}

//// TODO: what if I remove Memory expr OR constraint terms 
//public record Memory(String name, Term term, Integer partyIndex) {
//
//}