package plaid.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Program implements Node {

    private final List<CommandFunction> commandFunctions;
    private final List<ExprFunction> exprFunctions;

    public Program(List<CommandFunction> commandFunctions, List<ExprFunction> exprFunctions) {
        this.commandFunctions = commandFunctions;
        this.exprFunctions = exprFunctions;
    }

    public List<CommandFunction> getCommandFunctions() {
        return commandFunctions;
    }

    public List<ExprFunction> getExprFunctions() {
        return exprFunctions;
    }

    public ExprFunction resolveExprFunction(PreludeExpression functionName){
        for (ExprFunction exprFunction : exprFunctions) {
            if(exprFunction.getFname().equals(functionName)){
                return exprFunction;
            }
        }
        return null;
    }

    /**
     * return command function that has the same function name as input functionName
     * @param functionName
     * @return
     */
    public CommandFunction resolveCommandFunction(PreludeExpression functionName){
        for (CommandFunction commandFunction : commandFunctions) {
            if(commandFunction.getFname().equals(functionName)){
                return commandFunction;
            }
        }
        return null;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Program program = (Program) o;
        return Objects.equals(commandFunctions, program.commandFunctions) && Objects.equals(exprFunctions, program.exprFunctions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandFunctions, exprFunctions);
    }

    @Override
    public Iterable<Node> children() {
        List<Node> result = new ArrayList<>();
        result.addAll(commandFunctions);
        result.addAll(exprFunctions);
        return result;
    }

    @Override
    public String toString() {
        return "Program{" +
                "commandFunctions=" + commandFunctions +
                ", exprFunctions=" + exprFunctions +
                '}';
    }

    @Override
    public String prettyPrint(){
        throw new UnsupportedOperationException();
    }
}
