package plaid.antlr;

import org.junit.Test;
import plaid.ast.AssertCommand;
import plaid.ast.AssignCommand;
import plaid.ast.AtExpr;
import plaid.ast.CommandList;
import plaid.ast.FunctionCallCommand;
import plaid.ast.Identifier;
import plaid.ast.LetCommand;
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
        assertEquals(new AssignCommand(new AtExpr(new OutputExpr(), new Num(1)), new Num(3)), ast("out@1 := 3"));
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
                new MessageExpr(new Str("x")),
                new MessageExpr(new Str("y")),
                new Num(5)),
                ast("assert (m[\"x\"] = m[\"y\"])@5"));
    }

    /**
     * Parses let commands.
     */
    @Test
    public void letCommand() {
        PreludeCommand command = ast("let x = 4 in out@1 := x");
        assertEquals(new LetCommand(
                new Identifier("x"),
                new Num(4),
                new AssignCommand(new AtExpr(new OutputExpr(), new Num(1)), new Identifier("x"))), command);
    }

    /**
     * Let commands can have multiple other commands inside them.
     */
    @Test
    public void letCommandScope() {
        PreludeCommand command = ast("let x = 4 in out@1 := x; out@2 := x");
        assertEquals(new LetCommand(
                new Identifier("x"),
                new Num(4),
                new CommandList(List.of(
                    new AssignCommand(new AtExpr(new OutputExpr(), new Num(1)), new Identifier("x")),
                    new AssignCommand(new AtExpr(new OutputExpr(), new Num(2)), new Identifier("x"))))), command);
    }

    /**
     * Parses list of commands.
     */
    @Test
    public void commandList() {
        PreludeCommand command = ast("out@1 := x; out@2 := x");
        assertEquals(new CommandList(List.of(
                new AssignCommand(new AtExpr(new OutputExpr(), new Num(1)), new Identifier("x")),
                new AssignCommand(new AtExpr(new OutputExpr(), new Num(2)), new Identifier("x")))), command);
    }

    /**
     * Full line comments prevent commands from being parsed.
     */
    @Test
    public void fullLineComments() {
        PreludeCommand command = ast("out@1 := x;\n//out@3 := z;\nout@2 := y");
        assertEquals(new CommandList(List.of(
                new AssignCommand(new AtExpr(new OutputExpr(), new Num(1)), new Identifier("x")),
                new AssignCommand(new AtExpr(new OutputExpr(), new Num(2)), new Identifier("y")))), command);
    }

    /**
     * Full line comments prevent commands from being parsed.
     */
    @Test
    public void partialLineComments() {
        PreludeCommand command = ast("out@1 := x//@1");
        assertEquals(new AssignCommand(new AtExpr(new OutputExpr(), new Num(1)), new Identifier("x")), command);
    }

}
