package plaid.cvc;

import io.github.cvc5.Result;
import io.github.cvc5.Solver;
import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;
import plaid.antlr.Loader;
import plaid.ast.PreludeCommand;

import static plaid.cvc.CvcUtils.mkFiniteFieldSort;

public class Verifier {

    public static boolean verify(String src) {
        return verify(Loader.toCommand(src));
    }

    public static boolean verify(String src, int order) {
        return verify(Loader.toCommand(src), order);
    }

    public static boolean verify(PreludeCommand command) {
        return verify(command, 7);
    }

    public static boolean verify(PreludeCommand command, int order) {
        TermManager termManager = new TermManager();
        Sort sort = mkFiniteFieldSort(termManager, Integer.toString(order), 10);
        Solver solver = new Solver(termManager);
        TermFactory termFactory = new TermFactory(termManager, sort);
        termFactory.toTerms(command).forEach(solver::assertFormula);
        Result result = solver.checkSat();
        return result.isSat();
    }

    public static boolean entails(PreludeCommand c1, PreludeCommand c2) {
        return verify(c1) && !verify(c2);
    }

    public static boolean equivalent(PreludeCommand c1, PreludeCommand c2) {
        return entails(c1, c2) && entails(c2, c1);
    }
}
