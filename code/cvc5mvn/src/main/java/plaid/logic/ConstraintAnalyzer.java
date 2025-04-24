package plaid.logic;

import io.github.cvc5.CVC5ApiException;
import plaid.ast.*;
import java.util.*;
import plaid.ScalaFunctions;

public class ConstraintAnalyzer {

    private final Program program; // provide a program to resolve function
    private final GenEntailVerifier verifier;
    private final Map<Identifier, Constraints> functionConstraints = new HashMap<>(); // data structure to store inferred pre/post conditions by FN rule
    
    private Map<Identifier, PreludeExpression> binding(List<TypedIdentifier> formal, List<PreludeExpression> actual){
        // for each actual parameter, bind it to corresponding formal parameter 
        Map<Identifier, PreludeExpression> bindingList = new HashMap<>(); 
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
        System.out.println("Inferred pre for " + function.fname().name() + ": " + ScalaFunctions.prettyPrint(constraints.getPre()));
        System.out.println("Inferred post for " + function.fname().name() + ": "  + ScalaFunctions.prettyPrint(constraints.getPost()));
        // if the function does not have annotated pre/post conditions
        if (function.precond() == null && function.postcond() == null ){
            // then store inferred pre/post conditions and return them
            functionConstraints.put(function.fname(), constraints);
            return constraints;
        }
        else {
            // otherwise, use the hardpack
            // annotated precondition /\ inferred postcondition
            ConstraintExpr pre =
                    function.precond() == null? constraints.getPost()
                    : constraints.getPost() == null? function.precond()
                    : new AndConstraintExpr(function.precond(), constraints.getPost());
            // inferred precondition /\ annotated postcondition
            ConstraintExpr post =
                    function.postcond() == null? constraints.getPre()
                    : constraints.getPre() == null ? function.postcond()
                    : new AndConstraintExpr(constraints.getPre(), function.postcond());
            // take typings
            if (verifier.genEntails(function.typedVariables(), pre, post)){
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
    public Constraints inferPrePostCmd(PreludeCommand command, ConstraintEvaluator evaluator) {
        return switch(command){
            case AssignCommand assignCommand ->
                    new Constraints(
                            new TrueConstraintExpr(),
                            new EqualConstraintExpr(assignCommand.e1(), appendPartyIndex(assignCommand.e2(), null)));
            case AssertCommand assertCommand ->
                    new Constraints(
                            new EqualConstraintExpr(appendPartyIndex(assertCommand.e1(), assertCommand.e3()), appendPartyIndex(assertCommand.e2(), assertCommand.e3())),
                            new TrueConstraintExpr());
            case LetCommand letCommand -> inferPrePostCmd(evaluator.evalInstruction(letCommand), evaluator);
            case CommandList commandList -> {
                // visit each command and apply inferPrePostCmd
                // for each command's precondition, join them with And
                // same for postcondition
                List<Constraints> constraints = new ArrayList<>();
                constraints.add(inferPrePostCmd(commandList.c1(), evaluator));
                constraints.add(inferPrePostCmd(commandList.c2(), evaluator));

                Optional<ConstraintExpr> reducedPre =
                        constraints.stream().map(Constraints::getPre).filter(Objects::nonNull).reduce(AndConstraintExpr::new);
                Optional<ConstraintExpr> reducedPost =
                        constraints.stream().map(Constraints::getPost).filter(Objects::nonNull).reduce(AndConstraintExpr::new);
                yield new Constraints(reducedPre.orElse(null), reducedPost.orElse(null));
            }

            case FunctionCallCommand functionCallCommand -> {
                Identifier id = functionCallCommand.fname();
                CommandFunction fn = program.resolveCommandFunction(id);

                // look up functionConstraints
                if (!functionConstraints.containsKey(id)) {
                    functionConstraints.put(id, inferPrePostFN(fn));
                }
                Constraints constraints = functionConstraints.get(id);

                // give the binding of actual and formal parameters for constraints evaluation
                evaluator.binding_list.add(binding(fn.typedVariables(), functionCallCommand.parameters()));
                // use them for evaluation by the APP rule
                ConstraintExpr pre = constraints.getPre() == null ? null : evaluator.evalConstraint(constraints.getPre());
                ConstraintExpr post = constraints.getPost() == null ? null : evaluator.evalConstraint(constraints.getPost());
                evaluator.binding_list.removeLast();
                yield new Constraints(pre, post);
                        
            }
            default -> throw new RuntimeException("cannot infer precondition from invalid input");
        };
    }

    /**
     * append party index to for expressions of arithmetic operation on memories
     */
    private PreludeExpression appendPartyIndex(PreludeExpression expression, PreludeExpression partyIndex){
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
            case PreludeExpression e -> e;
        };
    }

}

