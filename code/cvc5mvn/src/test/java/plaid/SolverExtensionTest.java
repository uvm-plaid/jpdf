package plaid;

import org.junit.Test;
import io.github.cvc5.*;

public class SolverExtensionTest {
    @Test
    public void test() throws CVC5ApiException {
        TermManager termManager = new TermManager();
        Solver solver = new Solver(termManager);

        Sort f7 = termManager.mkFiniteFieldSort("7", 10);
        String protocol = "m[\"foo\"]@2 := (s[\"x\"] + -r[\"y\"])@1";

        //SolverExtension.addOvertureProtocolConst(solver, f7, protocol);
        //System.out.println(solver.checkSat());

        //System.out.println(termManager.mkFiniteFieldElem("7", f7, 10));
    }
}
