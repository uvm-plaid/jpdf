package plaid;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.HashMap;

// output the corresponding Overture for Prelude commands in main()
public class CommandsListener extends PreludeBaseListener{
    private final PrintStream out;
    private final Map<String, PreludeParser.CommandFuncContext> commandFunctions;
    private final Map<String, PreludeParser.ExprFuncContext> exprFunctions;
    private final LinkedList<Map<String, Object>> parameters;

    public CommandsListener(OutputStream out, Map<String, PreludeParser.CommandFuncContext> commandFunctions, Map<String, PreludeParser.ExprFuncContext> exprFunctions, LinkedList<Map<String, Object>> parameters) {
        this.out = new PrintStream(out);
        this.commandFunctions = commandFunctions;
        this.exprFunctions = exprFunctions;
        this.parameters = parameters;

    }

    @Override
    public void enterFunctionCallCommand(PreludeParser.FunctionCallCommandContext ctx) {
        // match a function name with one of the previous functions
        PreludeParser.CommandFuncContext function_context = commandFunctions.get(ctx.fname().getText());

        Map<String, Object> bindings = new HashMap<>();

        // expression evaluator
        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(exprFunctions, parameters);

        // Bind actual params to formal params,
        for(int i=0; i<function_context.y().size(); i++){
            bindings.put(function_context.y(i).getText(), expressionEvaluator.visit(ctx.p_expression(i)));
        }

        // push new map onto "parameters"
        parameters.addLast(bindings);

        new ParseTreeWalker().walk(this, function_context);

    }

    @Override
    public void exitFunctionCallCommand(PreludeParser.FunctionCallCommandContext ctx) {
        // Pop last map off "parameters"
        parameters.removeLast();
    }

    @Override
    public void enterAssignCommand(PreludeParser.AssignCommandContext ctx) {
        // evaluate lhs, rhs
        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(exprFunctions, parameters);
        String lhs = (String)expressionEvaluator.visit(ctx.p_expression(0));
        String rhs = (String)expressionEvaluator.visit(ctx.p_expression(1));

        out.println(lhs + ctx.ASSIGN().getText() + rhs);
    }

    @Override
    public void enterAssertCommand(PreludeParser.AssertCommandContext ctx) {
        // assert(e1 = e2)@e3
        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(exprFunctions, parameters);
        String e1 = (String)expressionEvaluator.visit(ctx.p_expression(0));
        String e2 = (String)expressionEvaluator.visit(ctx.p_expression(1));
        String e3 = (String)expressionEvaluator.visit(ctx.p_expression(2));

        out.println("assert" + ctx.LPAREN().getText() + e1 + "=" + e2 + ctx.RPAREN().getText() + ctx.AT().getText() + e3);
    }


}


