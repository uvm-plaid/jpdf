package plaid.cvc;

import io.github.cvc5.TermManager;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;

import java.util.Set;
import java.util.HashSet;

import static plaid.cvc.ConstraintsCvcUtils.mkFiniteFieldElem;

public class ConstraintsTermFactory {
    private final Sort sort;
    private final TermManager termManager;
    private final Set<Memory> variables = new HashSet<>(); // variables
    private final Term minusOne;

    public ConstraintsTermFactory(TermManager termManager, Sort sort){
        this.termManager = termManager;
        this.sort = sort;
        this.minusOne = mkFiniteFieldElem(termManager, "-1", sort, 10);
    }


    /**
     * look up the same variable or else create a CVC5 term with the predefined Cvc name convention
     */

}
