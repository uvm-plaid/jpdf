package plaid.cvc;

import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;
import plaid.ast.*;
import plaid.eval.ExpressionEvaluator;

import java.util.*;

public class GenEntailVerifier {
    private static int counter;
    private final static String order = "2";
    TermManager termManager = new TermManager();
    Sort sort = termManager.mkFiniteFieldSort(order, 10);
   
    public GenEntailVerifier() throws CVC5ApiException {
        counter = 1;        
    }
    
    private static Str genFreshString(){
        Character c = (char) (36); // $
        String freshStr = String.valueOf(c).repeat(Math.max(0, counter));
        counter = counter + 1;

        // return a string of length counter
        return new Str(freshStr);
    }

    private static Num genFreshCID() {
        int freshID = -(counter);
        counter++;
        return new Num(freshID);
    }

    public static PreludeExpression genFreshValue(Type type) {
        return switch (type) {
            case StringType _ -> genFreshString();
            case PartyIndexType _ -> genFreshCID();
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
    public boolean genEntails(List<Map<Identifier, Type>> typings, ConstraintExpr e1, ConstraintExpr e2) throws CVC5ApiException {
        List<Map<Identifier, PreludeExpression>> bindingList = new ArrayList<>();
        
        // generate fresh values for variables 
        for (Map<Identifier, Type> typing : typings) {
            for (Map.Entry<Identifier, Type> entry : typing.entrySet()) {
                bindingList.add(new HashMap<Identifier, PreludeExpression>(Map.of(entry.getKey(), genFreshValue(entry.getValue()))));
            }
        }
        
//        // TODO
//        for (Map<Identifier, PreludeExpression> bindings : bindingList) {
//            for (Map.Entry<Identifier, PreludeExpression> entry : bindings.entrySet()) {
//                System.out.println("id: " + entry.getKey());
//                System.out.println("fresh: " + entry.getValue());
//            }
//        }
        
        // evaluate abstractConstraints into ground constraint
        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(new Program(List.of(), List.of(), List.of(), null, null));
        expressionEvaluator.binding_list = bindingList;
        ConstraintExpr groundE1 = expressionEvaluator.evaluate(e1); 
        ConstraintExpr groundE2 =  expressionEvaluator.evaluate(e2);
                
        // check if ground constraint1 entails ground constraint2 using Verifier entails
        TermFactory termFactory = new TermFactory(termManager, sort);
        Term groundE1Term = termFactory.constraintToTerm(groundE1);
        Term groundE2Term = termFactory.constraintToTerm(groundE2);
        Verifier verifier = new Verifier(termFactory);
        
        return verifier.entails(groundE1Term, groundE2Term); 
    }
}
