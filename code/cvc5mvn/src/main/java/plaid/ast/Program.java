package plaid.ast;

import java.util.List;
import java.util.Objects;

public class Program {

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

}
