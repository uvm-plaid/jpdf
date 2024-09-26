package plaid;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;


/**
 * evaluate Prelude expressions to Overture expressions
 */
public class ExpressionEvaluator extends PreludeBaseVisitor<Object>{
    // constructor
    private final Map<String, PreludeParser.ExprFuncContext> exprFunctions;
    private final LinkedList<Map<String, Object>>  parameters;

    public ExpressionEvaluator(Map<String, PreludeParser.ExprFuncContext> exprFunctions, LinkedList<Map<String, Object>> parameters){
        this.exprFunctions = exprFunctions;
        this.parameters = parameters;
    }

    @Override
    public Object visitStringVal(PreludeParser.StringValContext ctx) {
        return ctx.getText();
    }

    @Override
    public Object visitValueVal(PreludeParser.ValueValContext ctx) {
        return ctx.getText();
    }

    @Override
    public Object visitExprVal(PreludeParser.ExprValContext ctx) {
        return ctx.getText();
    }

    @Override
    public Object visitVarVal(PreludeParser.VarValContext ctx) {
        return ctx.getText();
    }

    @Override
    public Object visitFieldVal(PreludeParser.FieldValContext ctx) {
        return ctx.getText();
    }

    @Override
    public Object visitSecretMemory(PreludeParser.SecretMemoryContext ctx) {
        String w = (String)(visit(ctx.index().p_expression()));
        return ctx.SECRET().getText() + '[' +  w    + ']';
    }

    @Override
    public Object visitMessageMemory(PreludeParser.MessageMemoryContext ctx) {
        String w = (String)visit(ctx.index().p_expression());
        return ctx.MESSAGE().getText() + '[' +  w + ']';
    }

    @Override
    public Object visitPublicMemory(PreludeParser.PublicMemoryContext ctx) {
        String w = (String)visit(ctx.index().p_expression());
        return ctx.PUBLIC().getText() + '[' +  w + ']';
    }

    @Override
    public Object visitOutputMemory(PreludeParser.OutputMemoryContext ctx) {
        return ctx.OUTPUT().getText();
    }

    @Override
    public Object visitRandomMemory(PreludeParser.RandomMemoryContext ctx) {
        String w = (String)visit(ctx.index().p_expression());
        return ctx.RANDOM().getText() + '[' +  w + ']';
    }

    public Object visitOperExprvalue(PreludeParser.OperExprvalueContext ctx) {
        String e1 = (String)visit(ctx.p_expression(0));
        String e2 = (String)visit(ctx.p_expression(1));

        return switch (ctx.binop().getText()) {
            case "*" -> e1 + "*" + e2;
            case "+" -> e1 + "+" + e2;
            case "-" -> e1 + "-" + e2;
            case "++" -> e1 + e2;
            default -> "";
        };

    }

    @Override
    public Object visitLetExpr(PreludeParser.LetExprContext ctx) {
        // let y = e1 in e2
        // evaluate e1 first
        String v1 = (String)visit(ctx.p_expression(0));
        Map<String, Object> bindings = new HashMap<>(parameters.getLast());
        bindings.put(ctx.y().getText(), v1);
        parameters.addLast(bindings);
        // look for y in e2 and substitute v1 for y
        String v2 = (String)visit(ctx.p_expression(1));
        parameters.removeLast();
        return v2;
    }

    @Override
    public Object visitExprFunc(PreludeParser.ExprFuncContext ctx) {
        PreludeParser.ExprFuncContext function_context = exprFunctions.get(ctx.fname().getText());
        Map<String, Object> bindings = new HashMap<>();

        for(int i=0; i<function_context.y().size(); i++){
            bindings.put(function_context.y(i).getText(), ctx.y(i).getText());
        }

        parameters.addLast(bindings);
        String result = (String)visit(function_context.p_expression());
        parameters.removeLast();
        return result;
    }

    @Override
    public Object visitFieldExpr(PreludeParser.FieldExprContext ctx) {
        Map<String, Object> result = new HashMap<>();
        // evaluate expressions and bind them to field names
        for(int i=0; i<ctx.p_expression().size(); i++){
            result.put(ctx.l(i).getText(), visit(ctx.p_expression(i)));
        }

        // return {l1 = v1; ... ln = vn}
        return result;
    }

    @Override
    public Object visitFieldSelectExpr(PreludeParser.FieldSelectExprContext ctx) {
        Map<String, Object> field = (Map<String, Object>)(visit(ctx.p_expression()));
        return field.get(ctx.l().getText());
    }

    @Override
    public Object visitAtExpr(PreludeParser.AtExprContext ctx) {
        // e1 @ e2
        String e1 = (String)visit(ctx.p_expression(0));
        String e2 = (String)visit(ctx.p_expression(1));

        return (e1 + ctx.AT().getText() + e2);
    }

    @Override
    public Object visitEVarExpr(PreludeParser.EVarExprContext ctx) {
        return parameters.getLast().get(ctx.getText());
    }

    @Override
    public Object visitParenPExpr(PreludeParser.ParenPExprContext ctx) {
        // ( e )
        String e = (String)visit(ctx.p_expression());
        return (ctx.LPAREN() + e + ctx.RPAREN());
    }
}
