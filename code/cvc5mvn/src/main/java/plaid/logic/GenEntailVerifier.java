package plaid.logic;

import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;

import plaid.ast.*;
import plaid.cvc.TermFactory;
import plaid.cvc.Verifier;
import plaid.eval.ConstraintChecker;

import java.util.*;

public class GenEntailVerifier {
    private int counter;
    private final TermManager termManager;
    private final Sort sort;
    //private final static String order = "2";
   
   
    private final Program program;
    
    public GenEntailVerifier(Program program, String order) throws CVC5ApiException {
        counter = 1;
        termManager  = new TermManager();
        sort = termManager.mkFiniteFieldSort(order, 10);
        this.program = program;
    }
    
    private Str genFreshString(){
        Character c = (char) (36); // $
        String freshStr = String.valueOf(c).repeat(Math.max(0, counter));
        counter = counter + 1;

        // return a string of length counter
        return new Str(freshStr);
    }

    private Num genFreshCID() {
        int freshID = -(counter);
        counter++;
        return new Num(freshID);
    }

    public PreludeExpression genFreshValue(Type type) {
        return switch (type) {
            case StringType x -> genFreshString();
            case PartyIndexType x -> genFreshCID();
            case RecordType t -> {
                TreeMap<Identifier, PreludeExpression> map = new java.util.TreeMap<>(Map.of());
                for (Map.Entry<Identifier, Type> element : t.elements().entrySet()){
                    map.put(element.getKey(), genFreshValue(element.getValue()));
                }
                yield new FieldExpr(map);
            }
            default -> throw new RuntimeException("Unsupported type " + type);
        };
    }
    

    /**
     * generalized rule for entailment, verify if precondition entails postcondition for all inputs
     * @param e1 abstract preposition
     * @param e2 abstract postcondition
     * @return true/false
     */
    public boolean genEntails(List<TypedIdentifier> typings, ConstraintExpr e1, ConstraintExpr e2) {
        Map<Identifier, PreludeExpression> bindingList = new HashMap<>();
        
        // generate fresh values for variables 
        for (TypedIdentifier typing: typings){
            bindingList.put(typing.y(), genFreshValue(typing.t()));
        }
        
        // evalConstraint abstractConstraints into ground constraint
        ConstraintEvaluator evaluator = new ConstraintEvaluator(program);
        evaluator.binding_list.add(bindingList);
        ConstraintExpr groundE1 = evaluator.evalConstraint(e1); 
        ConstraintExpr groundE2 =  evaluator.evalConstraint(e2);
        evaluator.binding_list.removeLast();

        // check if groundE1 and groundE2 are in fact ground
        if(ConstraintChecker.checkConstraint(groundE1) && ConstraintChecker.checkConstraint(groundE2)){
            // check if ground constraint1 entails ground constraint2 using Verifier entails
            TermFactory termFactory = new TermFactory(termManager, sort);
            Term groundE1Term = termFactory.constraintToTerm(groundE1);
            Term groundE2Term = termFactory.constraintToTerm(groundE2);
            Verifier verifier = new Verifier(termFactory);

            return verifier.entails(groundE1Term, groundE2Term);
        }
        
        else{
            throw new RuntimeException("constraints with fresh values are not ground!");
        }

    }
}
