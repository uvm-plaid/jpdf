package plaid.antlr;

import org.junit.Test;
import plaid.ast.*;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommandVisitorTest {

    private Cmd ast(String src) {
        return Loader.toCommand(src);
    }

    /**
     * Parses assignment commands.
     */
    @Test
    public void assignCommand() {
        assertEquals(new AssignCmd(new AtExpr(new OutputExpr(), new Num(1)), new Num(3)), ast("out@1 := 3"));
    }

    /**
     * Functions can be called with zero, one, or multiple actual parameters.
     */
    @Test
    public void functionCall() {
        assertEquals(new CallCmd(
                new Identifier("f"),
                List.of()), ast("f()"));
        assertEquals(new CallCmd(
                new Identifier("f"),
                List.of(new Num(0))), ast("f(0)"));
        assertEquals(new CallCmd(
                new Identifier("f"),
                List.of(new Num(0), new Identifier("x"))), ast("f(0, x)"));
    }

    /**
     * Parses assert commands.
     */
    @Test
    public void assertCommand() {
        assertEquals(new AssertCmd(
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
        Cmd command = ast("let x = 4 in out@1 := x");
        assertEquals(new LetCmd(
                new Identifier("x"),
                new Num(4),
                new AssignCmd(new AtExpr(new OutputExpr(), new Num(1)), new Identifier("x"))), command);
    }

    /**
     * Let commands can have multiple other commands inside them.
     */
    @Test
    public void letCommandScope() {
        Cmd command = ast("let x = 4 in out@1 := x; out@2 := x");
        assertEquals(new LetCmd(
                new Identifier("x"),
                new Num(4),
                new ListCmd(
                    new AssignCmd(new AtExpr(new OutputExpr(), new Num(1)), new Identifier("x")),
                    new AssignCmd(new AtExpr(new OutputExpr(), new Num(2)), new Identifier("x")))), command);
    }

    /**
     * Parses list of commands.
     */
    @Test
    public void commandList() {
        Cmd command = ast("out@1 := x; out@2 := x");
        assertEquals(new ListCmd(
                new AssignCmd(new AtExpr(new OutputExpr(), new Num(1)), new Identifier("x")),
                new AssignCmd(new AtExpr(new OutputExpr(), new Num(2)), new Identifier("x"))), command);
    }

    /**
     * Full line comments prevent commands from being parsed.
     */
    @Test
    public void fullLineComments() {
        Cmd command = ast("out@1 := x;\n//out@3 := z;\nout@2 := y");
        assertEquals(new ListCmd(
                new AssignCmd(new AtExpr(new OutputExpr(), new Num(1)), new Identifier("x")),
                new AssignCmd(new AtExpr(new OutputExpr(), new Num(2)), new Identifier("y"))), command);
    }

    /**
     * Full line comments prevent commands from being parsed.
     */
    @Test
    public void partialLineComments() {
        Cmd command = ast("out@1 := x//@1");
        assertEquals(new AssignCmd(new AtExpr(new OutputExpr(), new Num(1)), new Identifier("x")), command);
    }

    /**
     * Parses OT expression
     */
    @Test
    public void OTCommand(){
        assertEquals(new AssignCmd(new AtExpr(new MessageExpr(new Str("x")), new Num(1)) ,new AtExpr(new OTExpr(new SecretExpr(new Str("foo")), new Num(1), new MessageExpr(new Str("bar")), new MessageExpr(new Str("zoo"))), new Num(2))),
                ast("m[\"x\"]@1 := OT(s[\"foo\"]@1, m[\"bar\"], m[\"zoo\"])@2"));
    }


    /**
     * Parses a list of commands in right associate.
     */
    @Test
    public void rightAssociative() {
        Cmd command = ast("out@1 := x; out@2 := x; out@3 := x");
        Object expected = new ListCmd(
                new ListCmd(
                        new AssignCmd(new AtExpr(new OutputExpr(), new Num(1)), new Identifier("x")),
                        new AssignCmd(new AtExpr(new OutputExpr(), new Num(2)), new Identifier("x"))
                ),
                new AssignCmd(new AtExpr(new OutputExpr(), new Num(3)), new Identifier("x"))
        );
        assertEquals(expected, command);
    }
}
