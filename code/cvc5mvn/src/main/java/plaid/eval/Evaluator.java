package plaid.eval;

import plaid.ast.*;

import java.util.*;

public class Evaluator {
    private final Program program;
    public List<Map<Identifier, PreludeExpression>> binding_list;
    
    public Evaluator(Program program){
        this.program = program; 
        binding_list = new LinkedList<>();
        binding_list.add(new HashMap<>());
    }

    /**
     * Evaluate Prelude Expression 
     * @param e Prelude Expression
     * @return Overture 
     */
    public PreludeExpression toOverture(PreludeExpression e) {
        return switch(e){
            // base cases
            case Str w -> new Str(w.str());
            case Num i -> new Num(i.num());
            case Identifier id -> binding_list.getLast().get(id);
            
//            {
//                // if identifier is bound to value 
//                for(Map<Identifier, PreludeExpression> bindings : binding_list){
//                    for(Map.Entry<Identifier, PreludeExpression> entry : bindings.entrySet()){
//                        if(entry.getKey().equals(id)){
//                            // return the value 
//                            yield entry.getValue();
//                        }
//                    }
//                }
//                //yield binding_list.getLast().get(id); // original
//                // otherwise the id itself
//                yield id;
//            } // throws an error if the list doesn't contain the id
           

            case RandomExpr re -> new RandomExpr(toOverture(re.e()));
            case SecretExpr se -> new SecretExpr(toOverture(se.e()));
            case MessageExpr me -> new MessageExpr(toOverture(me.e()));
            case PublicExpr pe -> new PublicExpr(toOverture(pe.e()));
            case OutputExpr oe -> new OutputExpr();

            case TimesExpr te -> new TimesExpr(toOverture(te.e1()), toOverture(te.e2()));
            case PlusExpr pe -> new PlusExpr(toOverture(pe.e1()), toOverture(pe.e2()));
            case ConcatExpr ce -> {
                Str s1 = (Str) toOverture(ce.e1());
                Str s2 = (Str) toOverture(ce.e2());
                yield new Str(s1.str() + s2.str());
            }
            case MinusExpr me -> new MinusExpr(toOverture(me.e()));

            case LetExpr le -> {
                // let y = e1 in e2
                // evalConstraint e1 first
                PreludeExpression e1 = toOverture(le.e1());
                // let y = v in e2
                // substitute v for y in e2

                Map<Identifier, PreludeExpression> bindings = new HashMap<>(binding_list.getLast());
                bindings.put(le.y(), e1);
                binding_list.addLast(bindings);

                PreludeExpression result = toOverture(le.e2());
                binding_list.removeLast();

                yield result;
            }
            case FunctionCallExpr fe -> {
                // evalConstraint the parameters first
                List<PreludeExpression> actualParams = fe
                        .parameters()
                        .stream()
                        .map(this::toOverture)
                        .toList();

                // bind former parameters to actual parameters in function context
                Map<Identifier, PreludeExpression> bindings = new HashMap<>();


                for(int i = 0; i < actualParams.size(); i++){
                    bindings.put(program.resolveExprFunction(fe.fname()).y().get(i), actualParams.get(i));
                }
                binding_list.add(bindings);

                PreludeExpression result = toOverture(program.resolveExprFunction(fe.fname()).e());

                binding_list.removeLast();
                yield result;
            }
            case FieldExpr fe -> {
                TreeMap<Identifier, PreludeExpression> field = new TreeMap<>();
                for(Map.Entry<Identifier, PreludeExpression> element : fe.elements().entrySet()){
                    field.put(element.getKey(), toOverture(element.getValue()));
                }

                yield new FieldExpr(field);
            }
            case FieldSelectExpr fse -> {
                // evalConstraint field expression first
                FieldExpr field = (FieldExpr) toOverture(fse.e());
                yield field.elements().get(fse.l());
            }

            case AtExpr ae -> new AtExpr(toOverture(ae.e1()), toOverture(ae.e2()));
            
            case OTExpr oe -> new OTExpr(toOverture(oe.e1()), toOverture(oe.i1()), toOverture(oe.e2()), toOverture(oe.e3()));
            case OTFourExpr ofe -> new OTFourExpr(toOverture(ofe.s1()), toOverture(ofe.s2()), toOverture(ofe.i1()), toOverture(ofe.e1()), toOverture(ofe.e2()), toOverture(ofe.e3()), toOverture(ofe.e4()));
            default -> throw new IllegalArgumentException("Bad Expression");
        };
    }

    /**
     * Evaluate Prelude Command 
     * @param instr Prelude command
     * @return Overture 
     */
    public PreludeCommand evalInstruction(PreludeCommand instr) {
        return switch (instr) {

            case AssignCommand assignCommand ->
                    new AssignCommand(toOverture(assignCommand.e1()), toOverture(assignCommand.e2()));

            case FunctionCallCommand functionCall -> {
                // evalConstraint actual parameters first
                List<PreludeExpression> actual_parameters =
                        functionCall.parameters().stream().map(this::toOverture).toList();

                // find a function with the same function name
                CommandFunction function = program.resolveCommandFunction(functionCall.fname());

                // map former parameters to actual parameters
                Map<Identifier, PreludeExpression> bindings = new HashMap<>();
                for (int i = 0; i < actual_parameters.size(); i++) {
                    bindings.put(function.typedVariables().get(i).y(), actual_parameters.get(i));
                }

                binding_list.add(bindings);

                // evalConstraint the function body with the actual parameters
                PreludeCommand result = evalInstruction(function.c());
                binding_list.removeLast(); // have to comment out for a constraint to evaluate

                yield result;
            }

            case LetCommand letCommand -> {
                // let y = e in c
                // evalConstraint e
                PreludeExpression v = toOverture(letCommand.e());
                // let y = v in c
                Map<Identifier, PreludeExpression> binding = new HashMap<>(binding_list.getLast());
                binding.put(letCommand.y(), v);
                binding_list.addLast(binding);

                PreludeCommand result = evalInstruction(letCommand.c());
                binding_list.removeLast();
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
                    new AssertCommand(toOverture(assertCommand.e1()), toOverture(assertCommand.e2()), toOverture(assertCommand.e3()));

            default -> throw new IllegalArgumentException("Bad instruction");

        };
    }

    /**
     * Evalute Constraint Expression 
     * @param e ConstraintExpr
     * @return constraints
     */

    public ConstraintExpr evalConstraint(ConstraintExpr e) {
        return switch (e) {
            case AndConstraintExpr x -> new AndConstraintExpr(evalConstraint(x.e1()), evalConstraint(x.e2()));
            case NotConstraintExpr x -> new NotConstraintExpr(evalConstraint(x.e()));
            case EqualConstraintExpr x -> new EqualConstraintExpr(toOverture(x.e1()), toOverture(x.e2()));
            case FunctionCallExpr x -> {
                ConstraintFunction function = program.resolveConstraintFunction(x.fname());
                List<Identifier> formalParams = function.params();
                List<PreludeExpression> actualParams = x
                        .parameters()
                        .stream()
                        .map(this::toOverture)
                        .toList();

                Map<Identifier, PreludeExpression> bindings = new HashMap<>();
                for (int i = 0; i < actualParams.size(); i++) {
                    bindings.put(formalParams.get(i), actualParams.get(i));
                }

                binding_list.add(bindings);
                ConstraintExpr result = evalConstraint(function.body());
                binding_list.removeLast();

                yield result;
            }
            case TrueConstraintExpr x -> x;
            default -> throw new IllegalArgumentException("Bad constraint");
        };
    }

}
