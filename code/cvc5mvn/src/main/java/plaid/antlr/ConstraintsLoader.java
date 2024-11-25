package plaid.antlr;


import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import plaid.ConstraintsLexer;
import plaid.ConstraintsParser;
import plaid.ConstraintsParser.ConstraintsTermContext;
import plaid.ConstraintsParser.ConstraintsExprContext;
import plaid.ConstraintsParser.ConstraintsContext;
import plaid.constraints.ast.Constraints;
import plaid.constraints.ast.ConstraintsExpr;
import plaid.constraints.ast.ConstraintsTerm;

/**
 * support for converting Constraints source code into an abstract syntax tree
 */
public class ConstraintsLoader {

    /**
     * Creates a ANTLR parser for Constraints source code
     * @param src source code
     * @return ANTLR parser
     */
    public static ConstraintsParser createParser(String src){
        ANTLRInputStream input = new ANTLRInputStream(src);
        ConstraintsLexer lexer = new ConstraintsLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ConstraintsParser parser = new ConstraintsParser(tokens);
        parser.setBuildParseTree(true);
        return parser;
    }

    /**
     * Converts constraints term source code into an abstract syntax tree
     * @param src source code
     * @return ANTLR context
     */
    public static ConstraintsTerm toTerm(String src){
        return toTerm(createParser(src).constraintsTerm());
    }

    /**
     * Converts an ANTLR context into an abstract syntax tree for terms
     * @param ctx ANTLR context
     * @return Abstract syntax tree for terms
     */
    public static ConstraintsTerm toTerm(ConstraintsTermContext ctx){
        return new ConstraintsTermVisitor().visit(ctx);
    }

    /**
     * converts Constraints Expr source code into abstract syntax tree
     * @param src String source code 
     * @return abstract syntax tree
     */
    public static ConstraintsExpr toExpr(String src){
        return toExpr(createParser(src).constraintsExpr());
    }

    /**
     * converts an ANTLR context for Constraints Expr into abstract syntax tree
     * @param ctx ANTLR context
     * @return Abstract syntax tree for expressions
     */
    public static ConstraintsExpr toExpr(ConstraintsExprContext ctx){
        return new ConstraintsExprVisitor().visit(ctx);
    }

    /**
     * converts constraints string source code into abstract syntax tree
     * @param src constraints
     * @return abstract syntax tree
     */
    public static Constraints toConstraint(String src){
        return toConstraint(createParser(src).constraints());
    }

    /**
     * converts ANTLR context into abstract syntax tree
     * @param ctx constraints context
     * @return abstract syntax tree
     */
    public static Constraints toConstraint(ConstraintsContext ctx){
        return new Constraints(ctx.constraintsExpr().stream().map(ConstraintsLoader::toExpr).toList());
    }
}
