package plaid.logic;

import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;
import plaid.ast.*;
import plaid.cvc.GenEntailVerifier;
import plaid.eval.Evaluator;
import plaid.cvc.TermFactory;

import java.util.*;

public class ConstraintAnalyzer {

    private final Program program; // provide a program to resolve function
    private final Evaluator evaluator;
    private final TermManager termManager = new TermManager();
    private final Sort sort = termManager.mkFiniteFieldSort("2", 10);
    private final TermFactory termFactory = new TermFactory(termManager, sort);
    private final GenEntailVerifier verifier = new GenEntailVerifier();
    private final List<Map<PreludeExpression, Constraints>> functionConstraints; // data structure to store inferred pre/post conditions by FN rule

    public ConstraintAnalyzer(Program program) throws CVC5ApiException {
        this.program = program;
        this.evaluator = new Evaluator(program);
        functionConstraints = new LinkedList<>();
        functionConstraints.add(new HashMap<>());
    }

    /**
     * calculate precondition and postcondition for function, FN rule
     */
    public Constraints inferPrePostFN(CommandFunction function) throws CVC5ApiException{
        // infer the precondition and postcondition for the function using inferPrePostCmd
        Constraints constraints = inferPrePostCmd(function.c());
        
        // if the function does not have annotated pre/post conditions
        if (function.precond() == null && function.postcond() == null ){
            // then store inferred pre/post conditions and return them
            functionConstraints.add(Map.of(function.fname(), constraints));
            return constraints;
        }
        else {
            // otherwise, use the hardpack
            // annotated precondition /\ inferred postcondition
            ConstraintExpr pre = function.precond() == null? constraints.getPost() : 
                    new AndConstraintExpr(function.precond(), constraints.getPost());
            // inferred precondition /\ annotated postcondition
            ConstraintExpr post = function.postcond() == null? constraints.getPre() :
                    new AndConstraintExpr(constraints.getPre(), function.postcond());
            // take typings
            if (verifier.genEntails(function.typedVariables(), pre, post)){
                functionConstraints.add(Map.of(function.fname(), new Constraints(function.precond(), function.postcond())));
                return new Constraints(function.precond(), function.postcond());
            }
            else{
                // if the hardpack does not hold
                // return error message
                throw new RuntimeException("The hardpack does not hold!");
            }
        }
    }


    /**
     * using switch, create a recursive function that infers precondition and postcondition for each command
     */
    public Constraints inferPrePostCmd(PreludeCommand command) throws CVC5ApiException{
        return switch(command){
            case AssignCommand assignCommand ->
                    new Constraints(
                            new TrueConstraintExpr(),
                            new EqualConstraintExpr(assignCommand.e1(), appendPartyIndex(assignCommand.e2())));
            case AssertCommand assertCommand ->
                    new Constraints(
                            new EqualConstraintExpr(appendPartyIndex(assertCommand.e1(), assertCommand.e3()), appendPartyIndex(assertCommand.e2(), assertCommand.e3())),
                            new TrueConstraintExpr());
            case LetCommand letCommand -> inferPrePostCmd(evaluator.evalInstruction(letCommand));
            case CommandList commandList -> {
                // visit each command and apply inferPrePostCmd
                // for each command's precondition, join them with And
                // same for postcondition
                List<Constraints> constraints = new ArrayList<>();
                for (PreludeCommand c : commandList.commands()){
                    constraints.add(inferPrePostCmd(c));
                }
                //List<Constraints> constraints = commandList.commands().stream().map(this::inferPrePostCmd).toList(); // for some reason, CVC5exception cannot handle with function expression
                ConstraintExpr pre_result = new AndConstraintExpr(constraints.getFirst().getPre(),constraints.get(1).getPre());
                ConstraintExpr post_result = new AndConstraintExpr(constraints.getFirst().getPost(), constraints.get(1).getPost());

                if(constraints.size() > 2) {
                    for (int i = 2; i <= constraints.size(); i++) {
                        pre_result = new AndConstraintExpr(pre_result, constraints.get(i).getPre());
                        post_result = new AndConstraintExpr(post_result, constraints.get(i).getPost());
                    }
                }
                yield new Constraints(pre_result, post_result);
            }

            case FunctionCallCommand functionCallCommand -> {
                // look up functionConstraints
                for (Map<PreludeExpression, Constraints> constraints : functionConstraints){
                    for(Map.Entry<PreludeExpression, Constraints> entry : constraints.entrySet()){
                        // if functionConstraints contains the inferred pre/post conditions for the function
                        if(entry.getKey().equals(functionCallCommand.fname())){
                            // use them for evaluation by the APP rule
                            evaluator.evalInstruction(functionCallCommand);
                            yield new Constraints(
                                    entry.getValue().getPre() == null ? null : evaluator.evalConstraint(entry.getValue().getPre()),
                                    entry.getValue().getPost() == null? null : evaluator.evalConstraint(entry.getValue().getPost()));
                        }

                    }
                }
                // otherwise, let E1, E2 = inferPrePostFN and
                // use them with the APP rule to get precondition and postcondition of the application
                evaluator.evalInstruction(functionCallCommand);
                Constraints constraints = inferPrePostFN(program.resolveCommandFunction(functionCallCommand.fname()));
  
                yield new Constraints(
                        constraints.getPre() == null ? null : evaluator.evalConstraint(constraints.getPre()),
                        constraints.getPost()== null ? null : evaluator.evalConstraint(constraints.getPost()));
            }
            default -> throw new RuntimeException("cannot infer precondition from invalid input");
        };
    }

    /**
     * append party index to sub expressions on the right side of ASSIGN using appendPartyIndex function
     */
    private PreludeExpression appendPartyIndex(PreludeExpression expression){
        if(expression instanceof AtExpr){
            return appendPartyIndex(((AtExpr) expression).e1(), ((AtExpr) expression).e2());
        }
        return null;
    }

    /**
     * append party index to for expressions of arithmetic operation on memories
     */
    private PreludeExpression appendPartyIndex(PreludeExpression expression, PreludeExpression partyIndex){
        return switch (expression){
            case RandomExpr e -> new AtExpr(e, partyIndex);
            case SecretExpr e -> new AtExpr(e, partyIndex);
            case MessageExpr e -> new AtExpr(e, partyIndex);
            case PublicExpr e -> e;
            case OutputExpr e -> new AtExpr(e, partyIndex);
            case MinusExpr e -> new MinusExpr(appendPartyIndex(e, partyIndex));
            case TimesExpr e -> new TimesExpr(appendPartyIndex(e.e1(), partyIndex), appendPartyIndex(e.e2(), partyIndex));
            case PlusExpr e -> new PlusExpr(appendPartyIndex(e.e1(), partyIndex), appendPartyIndex(e.e2(), partyIndex));
            default -> throw new RuntimeException("bad constraints");
        };
    }

}
