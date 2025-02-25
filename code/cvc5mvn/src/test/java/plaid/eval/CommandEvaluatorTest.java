package plaid.eval;

import io.github.cvc5.Command;
import org.junit.Test;
import plaid.ast.*;
import plaid.antlr.Loader;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class CommandEvaluatorTest {

    private PreludeCommand eval(String src, List<CommandFunction> commandFunctions){
        PreludeCommand ast = Loader.toCommand(src);
        CommandEvaluator commandEvaluator = new CommandEvaluator(new Program(commandFunctions, List.of(), List.of(), null, null));
        return commandEvaluator.evalInstruction(ast);
    }

    /**
     * evaluate assign command
     */
    @Test
    public void evalAssignCommand(){
        PreludeCommand command = eval("r[\"x\"]@1 := 3", List.of());
        assertEquals(new AssignCommand(
                new AtExpr(new RandomExpr(new Str("x")), new Num(1)),
                new Num(3)), command);
    }

    /**
     * evaluate function call command
     */
    @Test
    public void evalFunctionCallCommand(){
        List<Identifier> parameters = List.of(new Identifier("n"), new Identifier("i1"), new Identifier("i2"));
        PreludeCommand commands = Loader.toCommand("m[n]@i2 := (s[n] + r[n])@i1;\n" + "m[n]@i1 := r[n]@i1");
        CommandFunction commandFunction = new CommandFunction(new Identifier("encodegmw"), parameters, commands);
        List<CommandFunction> functionContext = List.of(commandFunction);
        PreludeCommand command = eval("encodegmw(\"x\", 2, 1)", functionContext);

        assertEquals(Loader.toCommand("m[\"x\"]@1 := (s[\"x\"] + r[\"x\"])@2;\n" + "m[\"x\"]@2 := r[\"x\"]@2"),
                command);
    }

    /**
     * evaluate let command
     */
    @Test
    public void evalLetCommand(){
        PreludeCommand command = eval("let table = \"foo\" in m[table]@1 := r[\"x\"]@2", List.of());
        assertEquals(Loader.toCommand("m[\"foo\"]@1 := r[\"x\"]@2"), command);
    }

    /**
     * evaluate a double let commands
     */
    @Test
    public void evalDoubleLetCommand(){
        PreludeCommand command = eval("let table = \"foo\" in let i = 2 in m[table]@1 := r[\"x\"]@i", List.of());
        assertEquals(Loader.toCommand("m[\"foo\"]@1 := r[\"x\"]@2"), command);
    }

    /**
     * evaluate a list of commands
     */

    /**
     * evaluate assert command
     */
    @Test
    public void evalAssertCommand(){
        // command function context
        List<Identifier> formal_parameters = List.of(new Identifier("x"), new Identifier("i1"), new Identifier("i2"));
        PreludeCommand functionBody =
                Loader.toCommand("m[x++\"exts\"]@i1 := m[x++\"s\"]@i2;\n" +
                        "m[x++\"extm\"]@i1 := m[x++\"m\"]@i2;\n" +
                        "assert(m[x++\"extm\"] = m[x++\"k\"] + (m[\"delta\"] * m[x++\"exts\"]))@i1;\n" +
                        "m[x]@i1 := (m[x++\"exts\"] + m[x++\"s\"])@i1" );
        CommandFunction functionContext = new CommandFunction(new Identifier("_open"), formal_parameters, functionBody);

        // evaluate function body with function call
        PreludeCommand command = eval("_open(\"foo\", 1, 2)", List.of(functionContext));
        assertEquals(Loader.toCommand("m[\"fooexts\"]@1 := m[\"foos\"]@2;\n" +
                "m[\"fooextm\"]@1 := m[\"foom\"]@2;\n" +
                "assert(m[\"fooextm\"] = m[\"fook\"] + (m[\"delta\"] * m[\"fooexts\"]))@1;\n" +
                "m[\"foo\"]@1 := (m[\"fooexts\"] + m[\"foos\"])@1"), command);
    }
    /**
     * evaluate command function call and expression function call
     */
//    @Test
//    public void evalCommandAndExprFunctions(){
//        // command function context
//        List<Identifier> formal_parameters = List.of(new Identifier("z"), new Identifier("x"), new Identifier("y"));
//        PreludeCommand commands =
//                Loader.toCommand("let table = andtablegmw(x,y,z) in\n" +
//                "   m[x]@1 := m[x]@2;\n" +
//                "   m[y]@1 := m[y]@2;\n" +
//                "   m[z]@2 := mux4(m[x], m[y], table.row1, table.row2, table.row3, table.row4)@1;\n" +
//                "   m[z]@1 := r[z]@1");
//        CommandFunction commandFunction = new CommandFunction(new Identifier("encodegmw"), formal_parameters, commands);
//        List<CommandFunction> functionContext = List.of(commandFunction);
//
//
//        PreludeCommand command = eval("encodegmw(\"x\", 2, 1)", functionContext);
//
//        assertEquals(Loader.toCommand("m[\"x\"]@1 := (s[\"x\"] + r[\"x\"])@2;\n" + "m[\"x\"]@2 := r[\"x\"]@2"),
//                command);
//    }

}
