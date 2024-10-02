package plaid.cvc;

import io.github.cvc5.Term;
import plaid.ast.MemoryExpr;

import java.util.Collection;

public record Memory(String name, Term term, Collection<MemoryExpr> contexts) {

}
