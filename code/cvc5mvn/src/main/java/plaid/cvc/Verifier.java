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

}
