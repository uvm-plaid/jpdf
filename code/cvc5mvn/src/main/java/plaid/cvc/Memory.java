package plaid.cvc;

import io.github.cvc5.Term;
import plaid.ast.Node;



public record Memory(String name, Term term, Node node, Integer partyIndex) {

}

