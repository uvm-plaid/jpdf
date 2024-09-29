package plaid.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import plaid.PreludeLexer;
import plaid.PreludeParser;
import plaid.PreludeParser.CommandContext;
import plaid.PreludeParser.ExprContext;
import plaid.PreludeParser.ProgramContext;
import plaid.ast.PreludeCommand;
import plaid.ast.PreludeExpression;
import plaid.ast.Program;

public class Loader {

    public static PreludeParser createParser(String src) {
        ANTLRInputStream input = new ANTLRInputStream(src);
        PreludeLexer lexer = new PreludeLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PreludeParser parser = new PreludeParser(tokens);
        parser.setBuildParseTree(true);
        return parser;
    }

    public static PreludeExpression toExpression(ExprContext ctx) {
        return new ExpressionVisitor().visit(ctx);
    }

    public static PreludeExpression toExpression(String src) {
        return toExpression(createParser(src).expr());
    }

    public static PreludeCommand toCommand(CommandContext ctx) {
        return new CommandVisitor().visit(ctx);
    }

    public static PreludeCommand toCommand(String src) {
        return toCommand(createParser(src).command());
    }

    public static Program toProgram(ProgramContext ctx) {
        FunctionListener listener = new FunctionListener();
        new ParseTreeWalker().walk(listener, ctx);
        return new Program(listener.getCommandFunctions(), listener.getExprFunctions());
    }

    public static Program toProgram(String src) {
        return toProgram(createParser(src).program());
    }

}
