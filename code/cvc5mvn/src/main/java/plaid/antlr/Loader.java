package plaid.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import plaid.PreludeLexer;
import plaid.PreludeParser;
import plaid.PreludeParser.CommandContext;
import plaid.PreludeParser.ExprContext;
import plaid.PreludeParser.ProgramContext;
import plaid.ast.PreludeCommand;
import plaid.ast.PreludeExpression;
import plaid.ast.Program;

/**
 * Support for converting Prelude source code into an abstract syntax tree.
 */
public class Loader {

    /**
     * Creates a ANTLR4 parser for Prelude source code.
     *
     * @param src Source code
     * @return ANTLR4 parser
     */
    public static PreludeParser createParser(String src) {
        ANTLRInputStream input = new ANTLRInputStream(src);
        PreludeLexer lexer = new PreludeLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PreludeParser parser = new PreludeParser(tokens);
        parser.setBuildParseTree(true);
        return parser;
    }

    /**
     * Converts an ANTLR4 context into an abstract syntax tree for an expression.
     *
     * @param ctx ANTLR4 context
     * @return Abstract syntax tree for an expression
     */
    public static PreludeExpression toExpression(ExprContext ctx) {
        return new ExpressionVisitor().visit(ctx);
    }

    /**
     * Converts Prelude source code into an abstract syntax tree for an expression.
     *
     * @param src Source code
     * @return Abstract syntax tree for an expression
     */
    public static PreludeExpression toExpression(String src) {
        return toExpression(createParser(src).expr());
    }

    /**
     * Converts an ANTLR4 context into an abstract syntax tree for a command.
     *
     * @param ctx ANTLR4 context
     * @return Abstract syntax tree for a command
     */
    public static PreludeCommand toCommand(CommandContext ctx) {
        return new CommandVisitor().visit(ctx);
    }

    /**
     * Converts Prelude source code into an abstract syntax tree for a command.
     *
     * @param src Source code
     * @return Abstract syntax tree for a command
     */
    public static PreludeCommand toCommand(String src) {
        return toCommand(createParser(src).command());
    }

    /**
     * Converts an ANTLR4 context into an abstract syntax tree for a Prelude program.
     *
     * @param ctx ANTLR4 context
     * @return Abstract syntax tree for a Prelude program
     */
    public static Program toProgram(ProgramContext ctx) {
        FunctionListener listener = new FunctionListener();
        new ParseTreeWalker().walk(listener, ctx);
        return new Program(listener.getCommandFunctions(), listener.getExprFunctions());
    }

    /**
     * Converts Prelude source code into an abstract syntax tree for a Prelude program.
     *
     * @param src Source code
     * @return Abstract syntax tree for a Prelude program
     */
    public static Program toProgram(String src) {
        return toProgram(createParser(src).program());
    }

}
