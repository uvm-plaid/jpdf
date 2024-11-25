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
    private static final String order = "2";

    public static boolean satisfies(String src) {
        return satisfies(Loader.toCommand(src));
    }

    public static boolean satisfies(PreludeCommand command) {
        if (!OvertureChecker.checkOverture(command)) {
            throw new IllegalArgumentException("Not a valid overture protocol");
        }
        TermManager termManager = new TermManager();
        Sort sort = mkFiniteFieldSort(termManager, order, 10);
        Solver solver = new Solver(termManager);
        TermFactory termFactory = new TermFactory(termManager, sort);
        //termFactory.toTerms(command).forEach(solver::assertFormula);
        Collection<Term> e = termFactory.toTerms(command);
        Term term = termManager.mkTerm(Kind.AND, e.toArray(new Term[1]));
//        solver.assertFormula(term);
//        Result result = solver.checkSat();
//        return result.isSat();
        return satisfies(term);
    }

    public static boolean satisfies(Term e) {
        TermManager termManager = new TermManager();
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
        TermManager termManager = new TermManager();
        Term notE2 = termManager.mkTerm(Kind.NOT, e2);
        Term e1_entails_notE2 = termManager.mkTerm(Kind.AND, e1, notE2);
        return !satisfies(e1_entails_notE2);

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
        if (!OvertureChecker.checkOverture(command)) {
            throw new IllegalArgumentException("Not a valid overture protocol");
        }

        TermManager termManager = new TermManager();
        Sort sort = mkFiniteFieldSort(termManager, order, 10);
        TermFactory termFactory = new TermFactory(termManager, sort);
        Collection<Term> e1s = termFactory.toTerms(command);
        Collection<Term> e2s = termFactory.constraintsToTerms(proposition);

        Term e1, e2 = null;

        if(e1s.size() == 1){
            e1 = e1s.iterator().next();
        }
        else{
            e1 = termManager.mkTerm(Kind.AND, e1s.toArray(new Term[1]));
        }

        if(e2s.size() == 1){
            e2 = e2s.iterator().next();
        }
        else{
            e2 = termManager.mkTerm(Kind.AND, e2s.toArray(new Term[1]));
        }

        return entails(e1, e2);
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
        if (!OvertureChecker.checkOverture(c1) && !OvertureChecker.checkOverture(c2)) {
            throw new IllegalArgumentException("Not a valid overture protocol");
        }

        TermManager termManager = new TermManager();
        Sort sort = mkFiniteFieldSort(termManager, order, 10);
        TermFactory termFactory = new TermFactory(termManager, sort);
        Collection<Term> e1s = termFactory.toTerms(c1);
        Collection<Term> e2s = termFactory.toTerms(c2);

        Term e1, e2 = null;

        if(e1s.size() == 1){
            e1 = e1s.iterator().next();
        }
        else{
            e1 = termManager.mkTerm(Kind.AND, e1s.toArray(new Term[1]));
        }

        if(e2s.size() == 1){
            e2 = e2s.iterator().next();
        }
        else{
            e2 = termManager.mkTerm(Kind.AND, e2s.toArray(new Term[1]));
        }

        return entails(e1, e2) && entails(e2, e1);
    }
}

