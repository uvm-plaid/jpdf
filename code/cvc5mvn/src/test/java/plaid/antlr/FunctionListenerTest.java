package plaid.antlr;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;
import plaid.ast.*;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class FunctionListenerTest {

    private void assertFunctions(
            String src,
            List<ExprFunction> exprFunctions,
            List<CommandFunction> commandFunctions) {

        FunctionListener listener = new FunctionListener();
        ParseTree tree = Loader.createParser(src).program();
        new ParseTreeWalker().walk(listener, tree);

        assertEquals(exprFunctions, listener.getExprFunctions());
        assertEquals(commandFunctions, listener.getCommandFunctions());
    }

    /**
     * Parses expression functions of zero, one, and multiple parameters.
     */
    @Test
    public void exprFunctions() {
        ExprFunction f = new ExprFunction(
                new Identifier("f"),
                List.of(),
                new Num(0));

        ExprFunction g = new ExprFunction(
                new Identifier("g"),
                List.of(new Identifier("x")),
                new Num(1));

        ExprFunction h = new ExprFunction(
                new Identifier("h"),
                List.of(new Identifier("x"), new Identifier("y")),
                new Num(2));

        assertFunctions(
                "exprfunctions: f() { 0 } g(x) { 1 } h(x, y) { 2 }",
                List.of(f, g, h),
                List.of());
    }

    /**
     * Parses command functions of zero, one, and multiple parameters.
     */
    @Test
    public void commandFunctions() {
        CommandFunction f = new CommandFunction(
                new Identifier("f"),
                List.of(),
                new AssignCommand(new OutputExpr(), new Num(2)));

        CommandFunction g = new CommandFunction(
                new Identifier("g"),
                List.of(new TypedIdentifier(new Identifier("x"), new StringType())),
                new AssignCommand(new OutputExpr(), new Num(2)));

        CommandFunction h = new CommandFunction(
                new Identifier("h"),
                List.of(new TypedIdentifier(new Identifier("x"), new StringType()), new TypedIdentifier(new Identifier("i"), new PartyIndexType())),
                new AssignCommand(new OutputExpr(), new Num(2)));

        assertFunctions(
                "cmdfunctions: f() { out := 2 } g(x : string) { out := 2 } h(x : string, i : cid) { out := 2 }",
                List.of(),
                List.of(f, g, h));
    }

    /**
     * Expression functions and command functions can come in any order.
     */
    @Test
    public void orderAgnostic() {
        ExprFunction f = new ExprFunction(
                new Identifier("f"),
                List.of(),
                new Num(3));

        CommandFunction g = new CommandFunction(
                new Identifier("g"),
                List.of(),
                new AssignCommand(new OutputExpr(), new Num(2)));

        assertFunctions(
                "exprfunctions: f() { 3 } cmdfunctions: g() { out := 2 }",
                List.of(f),
                List.of(g));
    }

}
