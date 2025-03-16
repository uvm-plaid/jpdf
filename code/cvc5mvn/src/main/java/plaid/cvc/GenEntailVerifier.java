package plaid.cvc;

import plaid.ast.*;

import java.util.Map;
import java.util.TreeMap;

public class GenEntailVerifier {
    private static int counter;
    
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
     * @param abstractConstraint1 un-ground preposition
     * @param abstractConstrant2 un-ground postcondition
     * @return true/false
     */
    public boolean genEntails(ConstraintExpr abstractConstraint1, ConstraintExpr abstractConstrant2){
        // generate fresh values
        counter = 1;
        // substitute fresh values for variables 
        // evaluate abstractConstraints into ground constraint using ConstraintExpr evaluator 
        // check if ground constraint1 entails ground constraint2 using Verifier entails
        return true; 
    }
}
