package plaid.cvc;

import io.github.cvc5.*;
import plaid.antlr.Loader;
import plaid.ast.PreludeCommand;

import java.util.Collection;

import static plaid.cvc.CvcUtils.mkFiniteFieldSort;

public class Verifier {
    public static Collection<Term> toCVC5(PreludeCommand c, String primeNum){
        TermManager termManager = new TermManager();
        Sort sort = mkFiniteFieldSort(termManager, primeNum, 10);
        TermFactory termFactory = new TermFactory(termManager, sort);
        return termFactory.toTerms(c);
    }
    public static boolean verify(String src) {
        return verify(Loader.toCommand(src));
    }

    public static boolean verify(PreludeCommand command) {
        TermManager termManager = new TermManager();
        // TODO Do we need to parameterize the sort?
        Sort sort = mkFiniteFieldSort(termManager, "7", 10);
        Solver solver = new Solver(termManager);
        TermFactory termFactory = new TermFactory(termManager, sort);
        termFactory.toTerms(command).forEach(solver::assertFormula);
        Result result = solver.checkSat();
        return result.isSat();
    }

//    // when E1 entails E2, there doesn't exist a model that entails E1 and not E2
//    public static boolean entails(PreludeCommand c1, PreludeCommand c2) {
//        return !satisfy(toCVC5(c1, "7") && toCVC5(c2, "7"));
//    }

//    public static boolean verify(String c, String prop){
//
//    }


//    public static boolean equivalent(PreludeCommand c1, PreludeCommand c2) {
//        return entails(c1, c2) && entails(c2, c1);
//    }
}
