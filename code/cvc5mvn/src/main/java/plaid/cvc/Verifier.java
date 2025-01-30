package plaid.cvc;

import io.github.cvc5.Kind;
import io.github.cvc5.Result;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import plaid.antlr.Loader;
import plaid.ast.PreludeCommand;
import plaid.ast.ConstraintExpr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Verifier {

    private final TermFactory termFactory;

    public Verifier(TermFactory termFactory) {
        this.termFactory = termFactory;
    }

    public boolean satisfies(String src) {
        return satisfies(Loader.toCommand(src));
    }

    public boolean satisfies(PreludeCommand command) {
        Collection<Term> e = termFactory.toTerms(command);
        return satisfies(joinWithAnd(e));
    }

    public Map<Term, Integer> findModelSatisfying(Term term) {
        Solver solver = new Solver(termFactory.getTermManager());
        solver.setOption("produce-models", "true");
        solver.assertFormula(term);
        Result result = solver.checkSat();
        if (!result.isSat()) {
            return null;
        }
        Map<Term, Integer> map = new HashMap<>();
        int mod = Integer.parseInt(termFactory.getSort().getFiniteFieldSize());
        for (Memory memory : termFactory.getMemories()) {
            Term value = solver.getValue(memory.term());
            int finiteFieldValue = Integer.parseInt(CvcUtils.finiteFieldValue(value));
            map.put(memory.term(), Math.floorMod(finiteFieldValue, mod));
        }
        return map;
    }

    public boolean satisfies(Term e) {
        return findModelSatisfying(e) != null;
    }

    /**
     * when E1 entails E2, there doesn't exist a model that satisfies (E1 and not E2)
     * @param e1
     * @param e2
     * @return
     */
    public boolean entails(Term e1, Term e2) {
        Term notE2 = termFactory.getTermManager().mkTerm(Kind.NOT, e2);
        Term e1_entails_notE2 = termFactory.getTermManager().mkTerm(Kind.AND, e1, notE2);
        return !satisfies(e1_entails_notE2);
    }

    public boolean entails(Collection<Term> e1s, Collection<Term> e2s) {
        if (e2s.isEmpty()) {
            return true;
        } else if (e1s.isEmpty()) {
            return satisfies(joinWithAnd(e2s));
        } else {
            return entails(joinWithAnd(e1s), joinWithAnd(e2s));
        }
    }
    
    public boolean entails(Collection<Term> e1s, Term e2){
        if(e1s.isEmpty()){
            return satisfies(e2);
        }
        else{
            return entails(joinWithAnd(e1s), e2);
        }
    }
    /**
     * check if an overture protocol entails a proposition
     * @param src1 String type of Overture
     * @param src2 String type of Constraint
     * @return true/false
     */
    public boolean verifies(String src1, String src2){
        return verifies(Loader.toCommand(src1), Loader.toConstraintExpression(src2));
    }

    /**
     * check if an overture protocol entails a proposition (verify correctness)
     * @param command Overture
     * @param proposition Constraint
     * @return true/false
     */
    public boolean verifies(PreludeCommand command, ConstraintExpr proposition){
        Collection<Term> e1s = termFactory.toTerms(command);
        //Collection<Term> e2s = termFactory.constraintToTerm(proposition);
        Term e2s = termFactory.constraintToTerm(proposition);
        return entails(e1s, e2s);
    }

    public boolean equivalent(String src1, String src2){
        return equivalent(Loader.toCommand(src1), Loader.toCommand(src2));
    }

    /**
     * Two protocols are equivalent if they entail each other
     * @param c1 Overture
     * @param c2 Overture
     * @return true/false
     */
    public boolean equivalent(PreludeCommand c1, PreludeCommand c2) {
        Collection<Term> e1s = termFactory.toTerms(c1);
        Collection<Term> e2s = termFactory.toTerms(c2);

        return entails(joinWithAnd(e1s), joinWithAnd(e2s)) && entails(joinWithAnd(e2s), joinWithAnd(e1s));
    }

    /**
     * join multiple terms with AND
     * @param terms Collection<Term>>
     * @return term
     */
    private Term joinWithAnd(Collection<Term> terms){
        if(terms.size() == 1){
            return terms.iterator().next();
        }
        else{
            return termFactory.getTermManager().mkTerm(Kind.AND, terms.toArray(new Term[1]));
        }
    }
}

