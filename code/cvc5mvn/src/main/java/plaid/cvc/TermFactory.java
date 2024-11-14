package plaid.cvc;

import io.github.cvc5.Kind;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;
import plaid.ast.AssertCommand;
import plaid.ast.AssignCommand;
import plaid.ast.AtExpr;
import plaid.ast.CommandList;
import plaid.ast.MemoryExpr;
import plaid.ast.MinusExpr;
import plaid.ast.Num;
import plaid.ast.PlusExpr;
import plaid.ast.PreludeCommand;
import plaid.ast.PreludeExpression;
import plaid.ast.PublicExpr;
import plaid.ast.TimesExpr;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static plaid.cvc.CvcUtils.getCvcName;
import static plaid.cvc.CvcUtils.mkFiniteFieldElem;

public class TermFactory {

    private final Sort sort;
    private final TermManager termManager;
    private final Set<Memory> memories = new HashSet<>();
    private final Term minusOne;

    private Integer partyIndex;

    public TermFactory(TermManager termManager, Sort sort) {
        this.termManager = termManager;
        this.sort = sort;
        this.minusOne = mkFiniteFieldElem(termManager, "-1", sort, 10);
    }

    public Collection<Memory> getMemories() {
        return memories;
    }

    public Collection<Term> toTerms(PreludeCommand command) {
        return switch (command) {
            case CommandList x -> x.getCommands().stream().flatMap(y -> toTerms(y).stream()).toList();
            case AssertCommand x -> {
                Integer partyIndex = getPartyIndex(x);
                yield List.of(termManager.mkTerm(Kind.EQUAL, toTerm(x.getE1(), partyIndex), toTerm(x.getE2(), partyIndex)));
            }
            case AssignCommand x -> List.of(termManager.mkTerm(Kind.EQUAL, toTerm(x.getE1()), toTerm(x.getE2())));
            default -> throw new IllegalArgumentException("Not an overture command " + command.getClass().getName());
        };
    }

    public static Integer getPartyIndex(PreludeCommand command) {
        if (command instanceof AssertCommand) {
            PreludeExpression indexExpr = ((AssertCommand) command).getE3();
            return ((Num) indexExpr).getNum();
        }
        return null;
    }

    public Term toTerm(PreludeExpression expr, Integer partyIndex) {
        this.partyIndex = partyIndex;
        Term result = toTerm(expr);
        this.partyIndex = null;
        return result;
    }

    public Term toTerm(PreludeExpression expr) {
        return switch (expr) {
            case PublicExpr x -> lookupOrCreate(x, null);
            case MemoryExpr x -> {
                if (partyIndex == null) {
                    throw new IllegalStateException("Party index for memory cannot be null");
                }
                yield lookupOrCreate(x, partyIndex);
            }
            case AtExpr x -> {
                if (partyIndex != null) {
                    throw new IllegalStateException("Party index " + partyIndex + " already active");
                }
                partyIndex = CvcUtils.toInt(x.getE2());
                Term result = toTerm(x.getE1());
                partyIndex = null;
                yield result;
            }
            case Num x -> mkFiniteFieldElem(termManager, Integer.toString(x.getNum()), sort, 10);
            case PlusExpr x -> termManager.mkTerm(Kind.FINITE_FIELD_ADD, toTerm(x.getE1()), toTerm(x.getE2()));
            case TimesExpr x -> termManager.mkTerm(Kind.FINITE_FIELD_MULT, toTerm(x.getE1()), toTerm(x.getE2()));
            case MinusExpr x -> termManager.mkTerm(Kind.FINITE_FIELD_MULT, toTerm(x.getE()), minusOne);
            default -> throw new IllegalArgumentException("Cannot convert " + expr.getClass().getName() + " to term");
        };
    }

    public Term lookupOrCreate(MemoryExpr expr, Integer partyIndex) {
        String name = getCvcName(expr, partyIndex);
        Memory memory = memories
                .stream()
                .filter(x -> x.name().equals(name))
                .findFirst()
                .orElse(new Memory(name, termManager.mkConst(sort, name), expr, partyIndex));

        memories.add(memory);
        return memory.term();
    }

}
