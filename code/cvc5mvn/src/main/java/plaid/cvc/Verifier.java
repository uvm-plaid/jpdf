package plaid.cvc;

import io.github.cvc5.*;
import plaid.antlr.Loader;
import plaid.ast.AssertCommand;
import plaid.ast.Num;
import plaid.ast.PreludeCommand;
import plaid.ast.PreludeExpression;
import plaid.eval.OvertureChecker;

import java.util.Collection;

import static plaid.cvc.CvcUtils.mkFiniteFieldSort;

public class Verifier {
    // TODO: maybe we need to define another type/grammar for constraints?
    public static boolean satisfies(String src) {
        return satisfies(Loader.toCommand(src));
    }

    public static boolean satisfies(PreludeCommand command) {
        if (!OvertureChecker.checkOverture(command)) {
            throw new IllegalArgumentException("Not a valid overture protocol");
        }
        TermManager termManager = new TermManager();
        // TODO Do we need to parameterize the sort?
        Sort sort = mkFiniteFieldSort(termManager, "7", 10);
        Solver solver = new Solver(termManager);
        TermFactory termFactory = new TermFactory(termManager, sort);
        termFactory.toTerms(command).forEach(solver::assertFormula);
        Result result = solver.checkSat();
        return result.isSat();
    }

    // when E1 entails E2, there doesn't exist a model that entails E1 and not E2
    public static boolean entails(PreludeCommand c1, PreludeCommand c2) {
        TermManager termManager = new TermManager();
        Sort sort = mkFiniteFieldSort(termManager,"7", 10);
        TermFactory termFactory = new TermFactory(termManager, sort);

        Collection<Term> e1s = termFactory.toTerms(c1);
        Collection<Term> e2s = termFactory.toTerms(c2);
        Term e1 = termManager.mkTerm(Kind.AND, e1s.toArray(new Term[1]));
        Term e2 = termManager.mkTerm(Kind.AND, e1s.toArray(new Term[1]));
        Term notE2 = termManager.mkTerm(Kind.NOT, e2);
        Term entailment = termManager.mkTerm(Kind.AND, e1, notE2);
        Solver solver = new Solver(termManager);

        solver.assertFormula(entailment);
        Result result = solver.checkSat();
        return !result.isSat();

    }

//    public static boolean verify(PreludeCommand c, PreludeCommand proposition){
//        return enatails(c, proposition);
//    }

    public static boolean equivalent(PreludeCommand c1, PreludeCommand c2) {
        return entails(c1, c2) && entails(c2, c1);
    }
}
