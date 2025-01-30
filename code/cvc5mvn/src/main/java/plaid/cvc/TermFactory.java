package plaid.cvc;

import io.github.cvc5.*;
import plaid.ast.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public TermManager getTermManager() {
        return termManager;
    }

    public Collection<Memory> getMemories() {
        return memories;
    }

    /**
     * converts Prelude commands to CVC5 Terms
     * @param command
     * @return CVC5 Terms
     */
    public Collection<Term> toTerms(PreludeCommand command) {
        return switch (command) {
            case CommandList x -> x.commands().stream().flatMap(y -> toTerms(y).stream()).toList();
            case AssertCommand x -> {
                Integer partyIndex = getPartyIndex(x);
                yield List.of(termManager.mkTerm(Kind.EQUAL, toTerm(x.e1(), partyIndex), toTerm(x.e2(), partyIndex)));
            }
            case AssignCommand x -> List.of(termManager.mkTerm(Kind.EQUAL, toTerm(x.e1()), toTerm(x.e2())));
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
                Integer receiver_partyIndex = CvcUtils.toInt(x.i1());
                Term e1 = lookupOrCreate((MemoryExpr) x.e1(), receiver_partyIndex);

                yield termManager.mkTerm(Kind.FINITE_FIELD_ADD,
                        termManager.mkTerm(Kind.FINITE_FIELD_MULT, e1, toTerm(x.e3())),
                        termManager.mkTerm(Kind.FINITE_FIELD_MULT, termManager.mkTerm(Kind.FINITE_FIELD_ADD, e1, mkFiniteFieldElem(termManager, "1", sort, 10)), toTerm(x.e2())));
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

//    /**
//     * for constraints terms, looks up the memory set with CVC5 name and return its CVC5 term
//     */
//    public Term lookupOrCreate(ConstraintsTerm term){
//        return switch(term) {
//            case MessageConstraintsTerm mem -> lookupOrCreate(new MessageExpr(new Str(mem.w())), mem.i());
//            case RandomConstraintsTerm mem -> lookupOrCreate(new RandomExpr(new Str(mem.w())), mem.i());
//            case SecretConstraintsTerm mem -> lookupOrCreate(new SecretExpr(new Str(mem.w())), mem.i());
//            case PublicConstraintsTerm mem -> lookupOrCreate(new PublicExpr(new Str(mem.w())), null);
//            case OutputConstraintTerm mem -> lookupOrCreate(new OutputExpr(), mem.i());
//            default -> throw new IllegalArgumentException();
//        };
//    }

    /**
     * translate list of constraint expressions to CVC5 Terms
     */
//    public Collection<Term> constraintsToTerms(List<ConstraintExpr> constraints){
//        return constraints.stream().map(this::constraintToTerm).toList();
//    }
    
    /**
     * translate constraint expressions to CVC5 Term
     */
    public Term constraintToTerm(ConstraintExpr expr){
        return switch (expr) {
            case NotConstraintExpr x -> termManager.mkTerm(Kind.NOT, constraintToTerm(x.e()));
            case AndConstraintExpr x -> termManager.mkTerm(Kind.AND, constraintToTerm(x.e1()), constraintToTerm(x.e2()));
            case EqualConstraintExpr x -> termManager.mkTerm(Kind.EQUAL, toTerm(x.e1()), toTerm(x.e2()));
            default -> throw new IllegalArgumentException("cannot convert" + expr.getClass().getName() + "into CVC5 term");
        };
    }

//    /**
//     * translate constraints term to CVC5 term
//     */
//    public Term constraintsToTerm(ConstraintsTerm term)  {
//        return switch (term) {
//            case MessageConstraintsTerm x -> lookupOrCreate(x);
//            case PublicConstraintsTerm x -> lookupOrCreate(x);
//            case RandomConstraintsTerm x -> lookupOrCreate(x);
//            case OutputConstraintTerm x -> lookupOrCreate(x);
//            case SecretConstraintsTerm x -> lookupOrCreate(x);
//            case MinusConstraintsTerm x -> termManager.mkTerm(Kind.FINITE_FIELD_MULT, constraintsToTerm(x.e()), minusOne);
//            case PlusConstraintsTerm x -> termManager.mkTerm(Kind.FINITE_FIELD_ADD, constraintsToTerm(x.e1()), constraintsToTerm(x.e2()));
//            case TimesConstraintsTerm x -> termManager.mkTerm(Kind.FINITE_FIELD_MULT, constraintsToTerm(x.e1()), constraintsToTerm(x.e2()));
//            default -> throw new NoSuchElementException(term.getClass().getName() + " this CVC5 term is not defined");
//        };
    }

