package plaid.cvc;

import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;
import plaid.constraints.ast.*;

public class ConstraintsCvcUtils {
    /**
     * define the order of a finite field
     * @param termManager CVC5 class
     * @param s the order of a finite field
     * @param i base in numeral system
     * @return finite field
     */
    public static Sort mkFiniteFieldSort(TermManager termManager, String s, int i){
        try{
            return termManager.mkFiniteFieldSort(s, i);
        } catch (CVC5ApiException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @param termManager CVC5 class
     * @param s a number that we want to represent in the finite field
     * @param sort finite field
     * @param i base in numeral system
     * @return number from finite field
     */
    public static Term mkFiniteFieldElem(TermManager termManager, String s, Sort sort, int i){
        try{
            return termManager.mkFiniteFieldElem(s, sort, i);
        } catch (CVC5ApiException e){
            throw new RuntimeException(e);
        }
    }
    /**
     * produce cvc5 constant names for all memory types in abstract syntax tree 
     * @param term Constraints abstract syntax tree
     * @return cvc5 term
     */
    public static String getConstraintsCvcName(ConstraintsTerm term){
        return switch(term){
            case MessageConstraintsTerm mem -> "m_" + mem.w() + "_" + mem.i();
            case RandomConstraintsTerm mem -> "r_" + mem.w() + "_" + mem.i();
            case SecretConstraintsTerm mem -> "s_" + mem.w() + "_" + mem.i();
            case PublicConstraintsTerm mem -> "p_" + mem.w();
            case OutputConstraintTerm mem -> "o_" + mem.i();
            default -> throw new IllegalArgumentException();
        };
    }
    
    
}
