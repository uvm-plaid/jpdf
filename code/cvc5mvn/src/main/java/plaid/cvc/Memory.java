package plaid.cvc;

import io.github.cvc5.Term;
import plaid.ast.MemoryExpr;

public record Memory(String name, Term term, MemoryExpr node) {

}
