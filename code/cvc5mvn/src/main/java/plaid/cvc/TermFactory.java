package plaid.cvc;

import io.github.cvc5.*;
import plaid.ast.*;

import java.util.*;

import static java.util.stream.StreamSupport.stream;
import static plaid.cvc.CvcUtils.getCvcName;
import static plaid.cvc.CvcUtils.mkFiniteFieldElem;

public class TermFactory {

    private final Sort sort;
    private final TermManager termManager;
    private final Set<Memory> memories = new HashSet<>();
    private final Term minusOne;

    private Integer partyIndex;

    public TermFactory(TermManager termManager, Sort sort)  {
        this.termManager = termManager;
        this.sort = sort;
        this.minusOne = mkFiniteFieldElem(termManager, "-1", sort, 10);
        Solver solver = new Solver(this.termManager);
        try {
            solver.setLogic("ALL");
        } catch (CVC5ApiException e) {
            throw new RuntimeException(e);
        }
    }


    public Sort getSort() {
        return sort;
    }

    public TermManager getTermManager() {
        return termManager;
    }

    public Collection<Memory> getMemories() {
        return memories;
    }

    private Term not(Term term){
        return termManager.mkTerm(Kind.FINITE_FIELD_ADD, term, mkFiniteFieldElem(termManager, "1", sort, 10));
    }

    /**
     * join multiple terms with AND
     * @param terms Collection<Term>>
     * @return term
     */
    public Term joinWithAnd(Collection<Term> terms){
        if(terms.size() == 1){
            return terms.iterator().next();
        }
        else{
            return getTermManager().mkTerm(Kind.AND, terms.toArray(new Term[1]));
        }
    }

    /**
     * converts Prelude commands to CVC5 Terms
     * @param command
     * @return CVC5 Terms
     */

    public Term toTerm(PreludeCommand command) {
        return switch (command) {
            case CommandList x -> joinWithAnd(List.of(toTerm(x.c1()), toTerm(x.c2())));
            case AssertCommand x -> {
                Integer partyIndex = getPartyIndex(x);
                yield joinWithAnd(List.of(termManager.mkTerm(Kind.EQUAL, toTerm(x.e1(), partyIndex), toTerm(x.e2(), partyIndex))));
            }
            case AssignCommand x -> joinWithAnd(List.of(termManager.mkTerm(Kind.EQUAL, toTerm(x.e1()), toTerm(x.e2()))));
            default -> throw new IllegalArgumentException("Not an overture command " + command.getClass().getName());
        };
    }

    public static Integer getPartyIndex(PreludeCommand command) {
        if (command instanceof AssertCommand) {
            PreludeExpression indexExpr = ((AssertCommand) command).e3();
            return ((Num) indexExpr).num();
        }
        return null;
    }

    /**
     * used to convert assert command (not allowing multiple party indices for sub expr) to CVC5 term
     * @param expr
     * @param partyIndex
     * @return
     */
    public Term toTerm(PreludeExpression expr, Integer partyIndex) {
        this.partyIndex = partyIndex;
        Term result = toTerm(expr);
        this.partyIndex = null;
        return result;
    }

    /**
     * converts prelude expressions to CVC5 terms
     * @param expr
     * @return
     */
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
                partyIndex = CvcUtils.toInt(x.e2());
                Term result = toTerm(x.e1());
                partyIndex = null;
                yield result;
            }
            case Num x -> mkFiniteFieldElem(termManager, Integer.toString(x.num()), sort, 10);
            case PlusExpr x -> termManager.mkTerm(Kind.FINITE_FIELD_ADD, toTerm(x.e1()), toTerm(x.e2()));
            case TimesExpr x -> termManager.mkTerm(Kind.FINITE_FIELD_MULT, toTerm(x.e1()), toTerm(x.e2()));
            case MinusExpr x -> termManager.mkTerm(Kind.FINITE_FIELD_MULT, toTerm(x.e()), minusOne);
            case OTExpr x -> {
                Term e1 = toTerm(x.e1());
                yield termManager.mkTerm(Kind.FINITE_FIELD_ADD,
                        termManager.mkTerm(Kind.FINITE_FIELD_MULT, e1, toTerm(x.e3())),
                        termManager.mkTerm(Kind.FINITE_FIELD_MULT, not(e1), toTerm(x.e2())));
            }
            case OTFourExpr x -> {
                Term s1 = toTerm(x.s1());
                Term s2 = toTerm(x.s2());
                Term first = termManager.mkTerm(Kind.FINITE_FIELD_MULT, termManager.mkTerm(Kind.FINITE_FIELD_MULT, s1, s2), toTerm(x.e4()));
                Term second = termManager.mkTerm(Kind.FINITE_FIELD_MULT, termManager.mkTerm(Kind.FINITE_FIELD_MULT, s1, not(s2)), toTerm(x.e3()));
                Term third = termManager.mkTerm(Kind.FINITE_FIELD_MULT, termManager.mkTerm(Kind.FINITE_FIELD_MULT, not(s1), s2), toTerm(x.e2()));
                Term fourth = termManager.mkTerm(Kind.FINITE_FIELD_MULT, termManager.mkTerm(Kind.FINITE_FIELD_MULT, not(s1), not(s2)), toTerm(x.e1()));
                yield termManager.mkTerm(Kind.FINITE_FIELD_ADD, first, termManager.mkTerm(Kind.FINITE_FIELD_ADD, second, termManager.mkTerm(Kind.FINITE_FIELD_ADD, third, fourth)));
            }
            default -> throw new IllegalArgumentException("Cannot convert " + expr.getClass().getName() + " to term");
        };
    }

    /**
     * For memory expressions, looks up the memory set or else add CVC5 term name with its CVC5 term
     * @param expr
     * @param partyIndex
     * @return
     */
    public Term lookupOrCreate(MemoryExpr expr, Integer partyIndex) {
        String name = getCvcName(expr, partyIndex);
        Memory memory = memories
                .stream()
                .filter(x -> x.name().equals(name))
                .findFirst()
                .orElse(new Memory(name, termManager.mkConst(sort, name), expr, partyIndex));
                //.orElse(new Memory(name, termManager.mkConst(sort, name), partyIndex));

        memories.add(memory);
        return memory.term();
    }

    
    /**
     * translate constraint expressions to CVC5 Term
     */
    public Term constraintToTerm(ConstraintExpr expr){
        return switch (expr) {
            case NotConstraintExpr x -> termManager.mkTerm(Kind.NOT, constraintToTerm(x.e()));
            case AndConstraintExpr x -> termManager.mkTerm(Kind.AND, constraintToTerm(x.e1()), constraintToTerm(x.e2()));
            case EqualConstraintExpr x -> termManager.mkTerm(Kind.EQUAL, toTerm(x.e1()), toTerm(x.e2()));
            case TrueConstraintExpr x -> termManager.mkTrue();
            default -> throw new IllegalArgumentException("cannot convert" + expr.getClass().getName() + "into CVC5 term");
        };
    }


    }

