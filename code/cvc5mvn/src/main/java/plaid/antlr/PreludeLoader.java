package plaid.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import plaid.PreludeLexer;
import plaid.PreludeParser;
import plaid.PreludeParser.CommandContext;
import plaid.PreludeParser.ExprContext;
import plaid.PreludeParser.ProgramContext;
import plaid.ast.PreludeCommand;
import plaid.ast.PreludeExpression;
import plaid.ast.Program;

public class PreludeLoader {

    public static PreludeParser createParser(String src) {
        ANTLRInputStream input = new ANTLRInputStream(src);
        PreludeLexer lexer = new PreludeLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PreludeParser parser = new PreludeParser(tokens);
        parser.setBuildParseTree(true);
        return parser;
    }

    public static PreludeExpression toExpression(ExprContext ctx) {
        return new PreludeExpressionVisitor().visit(ctx);
    }

    public static PreludeExpression toExpression(String src) {
        return toExpression(createParser(src).expr());
    }

    public static PreludeCommand toCommand(CommandContext ctx) {
        return new PreludeCommandVisitor().visit(ctx);
    }

    public static PreludeCommand toCommand(String src) {
        return toCommand(createParser(src).command());
    }

    public static Program toProgram(ProgramContext ctx) {
        // TODO Implement me
        throw new UnsupportedOperationException();
    }

    public static Program toProgram(String src) {
        return toProgram(createParser(src).program());
    }

}
