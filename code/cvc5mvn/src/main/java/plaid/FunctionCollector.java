package plaid;

import java.util.HashMap;
import java.util.Map;

// collect function name and context while walking through parse tree
public class FunctionCollector extends PreludeBaseListener{
    private final Map<String, PreludeParser.ExprFuncContext> exprFunctions = new HashMap<>();  ;
    private final Map<String, PreludeParser.CommandFuncContext> commandFunctions = new HashMap<>();

    public Map<String, PreludeParser.ExprFuncContext> getExprFunctions() {
        return exprFunctions;
    }

    public Map<String, PreludeParser.CommandFuncContext> getCommandFunctions() {
        return commandFunctions;
    }

//    @Override
//    public void enterExprFunc(PreludeParser.ExprFuncContext ctx) {
//        exprFunctions.put(ctx.fname().getText(), ctx);
//    }
//
//    @Override
//    public void enterCommandFunc(PreludeParser.CommandFuncContext ctx) {
//        commandFunctions.put(ctx.fname().getText(), ctx);
//    }

}
