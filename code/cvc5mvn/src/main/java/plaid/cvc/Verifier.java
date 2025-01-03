package plaid.cvc;

import io.github.cvc5.*;
import plaid.antlr.ConstraintsLoader;
import plaid.antlr.Loader;
import plaid.ast.*;
import plaid.constraints.ast.Constraints;
import plaid.eval.OvertureChecker;
import plaid.eval.ProgramEvaluator;

import java.util.Collection;

import static plaid.cvc.CvcUtils.mkFiniteFieldSort;

public class Verifier {
    private static final String order = "7";
    private static final TermManager termManager = new TermManager();
    private static final Sort sort = mkFiniteFieldSort(termManager, order, 10);
    private static final TermFactory termFactory = new TermFactory(termManager, sort);

    public static boolean satisfies(String src) {
        return satisfies(Loader.toCommand(src));
    }

    public static TermFactory getTermFactory() {
        return termFactory;
    }

    public static boolean satisfies(PreludeCommand command) {
        Collection<Term> e = termFactory.toTerms(command);
        Term term = termManager.mkTerm(Kind.AND, e.toArray(new Term[1]));
        return satisfies(term);
    }

    public static boolean satisfies(Term e) {
        Solver solver = new Solver(termManager);
        solver.assertFormula(e);
        Result result = solver.checkSat();
        return result.isSat();
    }

    /**
     * when E1 entails E2, there doesn't exist a model that satisfies (E1 and not E2)
     * @param e1
     * @param e2
     * @return
     */
    public static boolean entails(Term e1, Term e2) {
        Term notE2 = termManager.mkTerm(Kind.NOT, e2);
        Term e1_entails_notE2 = termManager.mkTerm(Kind.AND, e1, notE2);
        return !satisfies(e1_entails_notE2);

    }

    public static boolean entails(Collection<Term> e1s, Collection<Term> e2s) {
        return entails(joinWithAnd(e1s), joinWithAnd(e2s));
    }

    /**
     * check if an overture protocol entails a proposition
     * @param src1 String type of Overture
     * @param src2 String type of Constraints
     * @return true/false
     */
    public static boolean verifies(String src1, String src2){
        return verifies(Loader.toCommand(src1), ConstraintsLoader.toConstraint(src2));
    }

    /**
     * check if an overture protocol entails a proposition (verify correctness)
     * @param command Overture
     * @param proposition Constraints
     * @return true/false
     */
    public static boolean verifies(PreludeCommand command, Constraints proposition){
        Collection<Term> e1s = termFactory.toTerms(command);
        Collection<Term> e2s = termFactory.constraintsToTerms(proposition);

        return entails(e1s, e2s);
    }

    public static boolean equivalent(String src1, String src2){
        return equivalent(Loader.toCommand(src1), Loader.toCommand(src2));
    }

    /**
     * Two protocols are equivalent if they entail each other
     * @param c1 Overture
     * @param c2 Overture
     * @return true/false
     */
    public static boolean equivalent(PreludeCommand c1, PreludeCommand c2) {
        Collection<Term> e1s = termFactory.toTerms(c1);
        Collection<Term> e2s = termFactory.toTerms(c2);

        return entails(joinWithAnd(e1s), joinWithAnd(e2s)) && entails(joinWithAnd(e2s), joinWithAnd(e1s));
    }

    /**
     * join multiple terms with AND
     * @param terms Collection<Term>>
     * @return term
     */
    private static Term joinWithAnd(Collection<Term> terms){
        if(terms.size() == 1){
            return terms.iterator().next();
        }
        else{
            return termManager.mkTerm(Kind.AND, terms.toArray(new Term[1]));
        }
    }
}

