package plaid.eval;

import plaid.ast.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

public class CommandEvaluator {
    private final Program program;
    private final List<Map<Identifier, PreludeExpression>> binding_list;
    private final ExpressionEvaluator expressionEvaluator;

    // constructor
    public CommandEvaluator(Program program){
        this.program = program;
        binding_list = new LinkedList<>();
        binding_list.add(new HashMap<>());
        expressionEvaluator = new ExpressionEvaluator(this.program);
    }


    public PreludeCommand evalInstruction(PreludeCommand instr) {
        return switch(instr) {

            case AssignCommand assignCommand ->
                    new AssignCommand(expressionEvaluator.toOverture(assignCommand.e1()), expressionEvaluator.toOverture(assignCommand.e2()));

            case FunctionCallCommand functionCall -> {
                // evaluate actual parameters first
                List<PreludeExpression> actual_parameters =
                        functionCall.parameters().stream().map(expressionEvaluator::toOverture).toList();

                // find a function with the same function name
                CommandFunction function = program.resolveCommandFunction(functionCall.fname());

                // map former parameters to actual parameters
                Map<Identifier, PreludeExpression> bindings = new HashMap<>();
                for (int i = 0; i < actual_parameters.size(); i++){
                    bindings.put(function.typedVariables().get(i).y(), actual_parameters.get(i));
                }

                expressionEvaluator.binding_list.add(bindings);

                // evaluate the function body with the actual parameters
                PreludeCommand result = evalInstruction(function.c());
                expressionEvaluator.binding_list.removeLast();

                yield result;
            }

            case LetCommand letCommand -> {
                // let y = e in c
                // evaluate e
                PreludeExpression v = expressionEvaluator.toOverture(letCommand.e());

                // let y = v in c
                Map<Identifier, PreludeExpression> binding = new HashMap<>(expressionEvaluator.binding_list.getLast());
                binding.put(letCommand.y(), v);
                expressionEvaluator.binding_list.addLast(binding);

                PreludeCommand result = evalInstruction(letCommand.c());
                expressionEvaluator.binding_list.removeLast();
                yield result;
            }

            case CommandList commandList -> {
                List<PreludeCommand> commands = commandList
                        .commands()
                        .stream()
                        .map(this::evalInstruction)
                        .toList();

                yield new CommandList(commands);
            }

            case AssertCommand assertCommand ->
                    new AssertCommand(expressionEvaluator.toOverture(assertCommand.e1()), expressionEvaluator.toOverture(assertCommand.e2()), expressionEvaluator.toOverture(assertCommand.e3()));

            default -> throw new IllegalArgumentException("Bad instruction");

        };
    }
}
