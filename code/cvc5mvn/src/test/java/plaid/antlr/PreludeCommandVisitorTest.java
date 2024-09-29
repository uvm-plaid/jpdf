package plaid.antlr;

import org.junit.Test;
import plaid.ast.AssignCommand;
import plaid.ast.Num;
import plaid.ast.OutputExpr;
import plaid.ast.PreludeCommand;

import static org.junit.Assert.assertEquals;

public class PreludeCommandVisitorTest {

    private PreludeCommand ast(String src) {
        return PreludeLoader.toCommand(src);
    }

    /**
     * Parses assignment commands.
     */
    @Test
    public void assignCommand() {
        assertEquals(new AssignCommand(new OutputExpr(new Num(1)), new Num(3)), ast("out@1 := 3"));
    }

}
