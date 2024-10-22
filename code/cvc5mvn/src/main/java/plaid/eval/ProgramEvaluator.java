package plaid.eval;

import plaid.ast.CommandFunction;
import plaid.ast.Identifier;
import plaid.ast.PreludeCommand;
import plaid.ast.Program;

import java.util.List;

public class ProgramEvaluator {
    private final Program program;
    private final CommandEvaluator commandEvaluator;

    public ProgramEvaluator(Program program){
        this.program = program;
        this.commandEvaluator = new CommandEvaluator(program);
    }

    // evaluate main method (command function)
    public PreludeCommand eval() {
        PreludeCommand commands = null;
        for (CommandFunction commandFunction : program.getCommandFunctions()) {
            if (commandFunction.getFname().equals(new Identifier("main"))) {
                commands = commandFunction.getC();

            }
        }

        assert commands != null;
        return commandEvaluator.evalInstruction(commands);

    }



}

/*
String progrm = "";
ProgramEvaluator programEvaluator = new ProgramEvaluator(program);
programEvaluator.eval; // return output
 */