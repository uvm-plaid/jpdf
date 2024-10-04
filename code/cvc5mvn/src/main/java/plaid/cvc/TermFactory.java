package plaid.cvc;

import io.github.cvc5.Kind;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;
import plaid.ast.AssertCommand;
import plaid.ast.AssignCommand;
import plaid.ast.CommandList;
import plaid.ast.MemoryExpr;
import plaid.ast.MinusExpr;
import plaid.ast.Node;
import plaid.ast.Num;
import plaid.ast.PlusExpr;
import plaid.ast.PreludeCommand;
import plaid.ast.PreludeExpression;
import plaid.ast.TimesExpr;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static plaid.cvc.CvcUtils.getCvcName;
import static plaid.cvc.CvcUtils.mkFiniteFieldElem;

public class TermFactory {

    private final Sort sort;
    private final TermManager termManager;
    private final Collection<Memory> memories = new HashSet<>();
    private final Term minusOne;

    public TermFactory(TermManager termManager, Sort sort) {
        this.termManager = termManager;
        this.sort = sort;
        this.minusOne = mkFiniteFieldElem(termManager, "-1", sort, 10);
    }

    public Collection<Memory> getMemories() {
        return memories;
    }

    public Collection<Term> toTerms(PreludeCommand command) {
        createConstants(command);
        return switch (command) {
            case CommandList x -> x.getCommands().stream().flatMap(y -> toTerms(y).stream()).toList();
            // TODO Is this the right way to make terms from asserts?
            case AssertCommand x -> List.of(termManager.mkTerm(Kind.EQUAL, toTerm(x.getE1()), toTerm(x.getE2())));
            case AssignCommand x -> List.of(termManager.mkTerm(Kind.EQUAL, toTerm(x.getE1()), toTerm(x.getE2())));
            default -> throw new IllegalArgumentException("Not an overture command " + command.getClass().getName());
        };
    }

    public Term toTerm(PreludeExpression expr) {
        return switch (expr) {
            case MemoryExpr x -> lookup(x);
            case Num x -> mkFiniteFieldElem(termManager, Integer.toString(x.getNum()), sort, 10);
            case PlusExpr x -> termManager.mkTerm(Kind.FINITE_FIELD_ADD, toTerm(x.getE1()), toTerm(x.getE2()));
            case TimesExpr x -> termManager.mkTerm(Kind.FINITE_FIELD_MULT, toTerm(x.getE1()), toTerm(x.getE2()));
            case MinusExpr x -> termManager.mkTerm(Kind.FINITE_FIELD_MULT, toTerm(x.getE()), minusOne);
            default -> throw new IllegalArgumentException("Cannot convert " + expr.getClass().getName() + " to term");
        };
    }

    public void createConstants(Node node) {
        switch (node) {
            case MemoryExpr x -> createConstants(x);
            case Node x -> x.children().forEach(this::createConstants);
        }
    }

    public Term lookup(MemoryExpr expr) {
        String name = getCvcName(expr);
        Memory memory = memories
                .stream()
                .filter(x -> x.name().equals(name))
                .findFirst()
                .orElse(null);

        return memory == null ? null : memory.term();
    }

    public void createConstants(MemoryExpr expr) {
        if (lookup(expr) == null) {
            String name = getCvcName(expr);
            memories.add(new Memory(name, termManager.mkConst(sort, name), expr));
        }
    }

}
