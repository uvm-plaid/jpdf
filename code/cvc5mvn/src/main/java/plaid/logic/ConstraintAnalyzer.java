package plaid.logic;

import io.github.cvc5.CVC5ApiException;
import plaid.ast.*;
import java.util.*;
import plaid.ScalaFunctions;

public class ConstraintAnalyzer {

    private final Program program; // provide a program to resolve function
    private final GenEntailVerifier verifier;
    private final Map<Identifier, Constraints> functionConstraints = new HashMap<>(); // data structure to store inferred pre/post conditions by FN rule
    
    private Map<Identifier, Expr> binding(List<TypedIdentifier> formal, List<Expr> actual){
        // for each actual parameter, bind it to corresponding formal parameter 
        Map<Identifier, Expr> bindingList = new HashMap<>(); 
        for(int i = 0; i < formal.size(); i++){
            bindingList.put(formal.get(i).y(), actual.get(i));
        }
        return bindingList;
    }

    public ConstraintAnalyzer(Program program, String order) throws CVC5ApiException {
        this.program = program;
        this.verifier = new GenEntailVerifier(program, order);
    }

    /**
     * calculate precondition and postcondition for function, FN rule
     */
    public Constraints inferPrePostFN(CommandFunction function) {
        ConstraintEvaluator evaluator = new ConstraintEvaluator(program);
        // infer the precondition and postcondition for the function using inferPrePostCmd
        Constraints constraints = inferPrePostCmd(function.c(), evaluator);
        // if the function does not have annotated pre/post conditions
        if (function.precond() == null && function.postcond() == null ){
            // then store inferred pre/post conditions and return them
            functionConstraints.put(function.fname(), constraints);
            return constraints;
        }
        else {
            // otherwise, use the hardpack
            // annotated precondition /\ inferred postcondition

            // These names match those in the HARDPACK rule for toplas26
            Constraint e1 = Objects.requireNonNullElse(function.precond(), new TrueConstraint());
            Constraint e2 = Objects.requireNonNullElse(function.postcond(), new TrueConstraint());
            // TODO Would be nice to have these non-nullable and TrueConstraint by default
            Constraint e1p = Objects.requireNonNullElse(constraints.getPre(), new TrueConstraint());
            Constraint e2p = Objects.requireNonNullElse(constraints.getPost(), new TrueConstraint());
            // take typings
            if (
                    verifier.genEntails(function.typedVariables(), e1, e1p) &&
                    verifier.genEntails(function.typedVariables(), new AndConstraint(e1, e2p), e2)) {
                functionConstraints.put(function.fname(), new Constraints(function.precond(), function.postcond()));
                return new Constraints(function.precond(), function.postcond());
            }
            else{
                // if the hardpack does not hold
                // return error message
                throw new RuntimeException("The hardpack does not hold for " + ScalaFunctions.prettyPrint(function.fname()));
            }
        }
    }

    /**
     * using switch, create a recursive function that infers precondition and postcondition for each command
     */
    public Constraints inferPrePostCmd(Cmd command, ConstraintEvaluator evaluator) {
        return switch(command){
            case AssignCmd assignCmd ->
                    new Constraints(
                            new TrueConstraint(),
                            new EqualConstraint(evaluator.toOverture(assignCmd.e1()), appendPartyIndex(evaluator.toOverture(assignCmd.e2()), null)));
            case AssertCmd assertCmd ->
                    new Constraints(
                            new EqualConstraint(appendPartyIndex(evaluator.toOverture(assertCmd.e1()), evaluator.toOverture(assertCmd.e3())), appendPartyIndex(evaluator.toOverture(assertCmd.e2()), evaluator.toOverture(assertCmd.e3()))),
                            new TrueConstraint());
            case LetCmd letCmd -> inferPrePostCmd(evaluator.evalInstruction(letCmd), evaluator);
            case ListCmd listCmd -> {
                // visit each command and apply inferPrePostCmd
                // for each command's precondition, join them with And
                // same for postcondition
                List<Constraints> constraints = new ArrayList<>();
                constraints.add(inferPrePostCmd(listCmd.c1(), evaluator));
                constraints.add(inferPrePostCmd(listCmd.c2(), evaluator));

                Optional<Constraint> reducedPre =
                        constraints.stream().map(Constraints::getPre).filter(Objects::nonNull).reduce(AndConstraint::new);
                Optional<Constraint> reducedPost =
                        constraints.stream().map(Constraints::getPost).filter(Objects::nonNull).reduce(AndConstraint::new);
                yield new Constraints(reducedPre.orElse(null), reducedPost.orElse(null));
            }

            case CallCmd callCmd -> {
                Identifier id = callCmd.fname();
                CommandFunction fn = program.resolveCommandFunction(id);

                // look up functionConstraints
                if (!functionConstraints.containsKey(id)) {
                    functionConstraints.put(id, inferPrePostFN(fn));
                }
                Constraints constraints = functionConstraints.get(id);

                // give the binding of actual and formal parameters for constraints evaluation
                evaluator.binding_list.add(binding(fn.typedVariables(), callCmd.parameters()));
                // use them for evaluation by the APP rule
                Constraint pre = constraints.getPre() == null ? null : evaluator.evalConstraint(constraints.getPre());
                Constraint post = constraints.getPost() == null ? null : evaluator.evalConstraint(constraints.getPost());
                evaluator.binding_list.removeLast();
                yield new Constraints(pre, post);
                        
            }
            default -> throw new RuntimeException("cannot infer precondition from invalid input");
        };
    }

    /**
     * append party index to for expressions of arithmetic operation on memories
     */
    private Expr appendPartyIndex(Expr expression, Expr partyIndex){
        return switch (expression){
            case AtExpr e -> appendPartyIndex(e.e1(), e.e2());
            case RandomExpr e -> new AtExpr(e, partyIndex);
            case SecretExpr e -> new AtExpr(e, partyIndex);
            case MessageExpr e -> new AtExpr(e, partyIndex);
            case OutputExpr e -> new AtExpr(e, partyIndex);
            case MinusExpr e -> new MinusExpr(appendPartyIndex(e.e(), partyIndex));
            case TimesExpr e -> new TimesExpr(appendPartyIndex(e.e1(), partyIndex), appendPartyIndex(e.e2(), partyIndex));
            case PlusExpr e -> new PlusExpr(appendPartyIndex(e.e1(), partyIndex), appendPartyIndex(e.e2(), partyIndex));
            case OTExpr e -> new OTExpr(
                    appendPartyIndex(e.e1(), e.i1()),
                    e.i1(),
                    appendPartyIndex(e.e2(), partyIndex),
                    appendPartyIndex(e.e3(), partyIndex));
            case OTFourExpr e -> new OTFourExpr(
                    appendPartyIndex(e.s1(), e.i1()),
                    appendPartyIndex(e.s2(), e.i1()),
                    e.i1(),
                    appendPartyIndex(e.e1(), partyIndex),
                    appendPartyIndex(e.e2(), partyIndex),
                    appendPartyIndex(e.e3(), partyIndex),
                    appendPartyIndex(e.e4(), partyIndex));
            case Expr e -> e;
        };
    }

}

