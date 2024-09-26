package plaid;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.Map;

// interpret the entire program
public class Interpreter  {
    public void interpret (InputStream in, OutputStream out) throws Exception{
        // source code
        String program = new String(in.readAllBytes());
        ANTLRInputStream input = new ANTLRInputStream(program);
        PreludeLexer lexer = new PreludeLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PreludeParser parser = new PreludeParser(tokens);
        parser.setBuildParseTree(true);
        PreludeParser.ProgramContext pc = parser.program();

        // output overture code
        FunctionCollector functionCollector = new FunctionCollector();
        new ParseTreeWalker().walk(functionCollector, pc);
        Map<String, PreludeParser.CommandFuncContext> commandFunctions = functionCollector.getCommandFunctions();

        // find the 'main' function
        PreludeParser.CommandFuncContext main = commandFunctions.get("main");
        LinkedList<Map<String, Object>> parameters = new LinkedList<>();

        CommandsListener commandsListener = new CommandsListener
                (out, commandFunctions, functionCollector.getExprFunctions(), parameters);

        new ParseTreeWalker().walk(commandsListener, main);

    }



}
