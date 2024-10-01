package plaid.antlr;

import org.junit.Test;
import plaid.ast.AssertCommand;
import plaid.ast.AssignCommand;
import plaid.ast.FunctionCallCommand;
import plaid.ast.Identifier;
import plaid.ast.MessageExpr;
import plaid.ast.Num;
import plaid.ast.OutputExpr;
import plaid.ast.PreludeCommand;
import plaid.ast.Str;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommandVisitorTest {

    private PreludeCommand ast(String src) {
        return Loader.toCommand(src);
    }

    /**
     * Parses assignment commands.
     */
    @Test
    public void assignCommand() {
        assertEquals(new AssignCommand(new OutputExpr(new Num(1)), new Num(3)), ast("out@1 := 3"));
    }

    /**
     * Functions can be called with zero, one, or multiple actual parameters.
     */
    @Test
    public void functionCall() {
        assertEquals(new FunctionCallCommand(
                new Identifier("f"),
                List.of()), ast("f()"));
        assertEquals(new FunctionCallCommand(
                new Identifier("f"),
                List.of(new Num(0))), ast("f(0)"));
        assertEquals(new FunctionCallCommand(
                new Identifier("f"),
                List.of(new Num(0), new Identifier("x"))), ast("f(0, x)"));
    }

    /**
     * Parses assert commands.
     */
    @Test
    public void assertCommand() {
        assertEquals(new AssertCommand(
                new MessageExpr(new Str("x"), new Num(5)),
                new MessageExpr(new Str("y"), new Num(5))), ast("assert (m[\"x\"] = m[\"y\"])@5"));
    }

}