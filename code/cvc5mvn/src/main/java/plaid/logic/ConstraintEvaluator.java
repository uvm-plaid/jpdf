package plaid.logic;

import plaid.ast.*;

import java.util.*;

public class ConstraintEvaluator {
    private final Program program;
    public List<Map<Identifier, Expr>> binding_list;

    public ConstraintEvaluator(Program program){
        this.program = program; 
        binding_list = new LinkedList<>();
        binding_list.add(new HashMap<>());
    }

    /**
     * Evaluate Prelude Expression 
     * @param e Prelude Expression
     * @return Overture 
     */
    public Expr toOverture(Expr e) {
        return switch(e){
            // base cases
            case Str w -> new Str(w.str());
            case Num i -> new Num(i.num());
            case Identifier id ->
            {
                // if identifier is bound to value 
//                for(Map<Identifier, PreludeExpression> bindings : binding_list){
//                    for(Map.Entry<Identifier, PreludeExpression> entry : bindings.entrySet()){
//                        if(entry.getKey().equals(id)){
//                            // return the value 
//                            yield entry.getValue();
//                        }
//                    }
//                }
                // if the list does not contain the id itself
                // return id itself
                if (binding_list.getLast().get(id) == null) {
                    yield id;
                }
                // otherwise return bound value 
                else {
                    yield binding_list.getLast().get(id); // original
                }
            } 
           

            case RandomExpr re -> new RandomExpr(toOverture(re.e()));
            case SecretExpr se -> new SecretExpr(toOverture(se.e()));
            case MessageExpr me -> new MessageExpr(toOverture(me.e()));
            case PublicExpr pe -> new PublicExpr(toOverture(pe.e()));
            case OutputExpr oe -> new OutputExpr();

            case TimesExpr te -> new TimesExpr(toOverture(te.e1()), toOverture(te.e2()));
            case PlusExpr pe -> new PlusExpr(toOverture(pe.e1()), toOverture(pe.e2()));
            case ConcatExpr ce -> {
                
                // if both sub-terms are strings, concatenate directly 
                if(toOverture(ce.e1()) instanceof Str && toOverture(ce.e2()) instanceof Str) {
                    Str s1 = (Str) toOverture(ce.e1());
                    Str s2 = (Str) toOverture(ce.e2());
                    yield new Str(s1.str() + s2.str());
                }
                
                // else if (x++"s") ++ "foo" case 
                else if(toOverture(ce.e1()) instanceof ConcatExpr left && left.e2() instanceof Str && toOverture(ce.e2()) instanceof Str) {
                    yield new ConcatExpr(left.e1(), toOverture(new ConcatExpr(left.e2(), toOverture(ce.e2()))));
                }
                
                // else if "foo" ++ ("s" ++ x) case  
                else if(toOverture(ce.e1()) instanceof Str && toOverture(ce.e2()) instanceof ConcatExpr right && right.e1() instanceof Str){
                    yield new ConcatExpr(toOverture(new ConcatExpr(toOverture(ce.e1()), right.e1())), right.e2());
                }
                yield new ConcatExpr(toOverture(ce.e1()), toOverture(ce.e2()));
            }
            case MinusExpr me -> new MinusExpr(toOverture(me.e()));

            case LetExpr le -> {
                // let y = e1 in e2
                // evalConstraint e1 first
                Expr e1 = toOverture(le.e1());
                // let y = v in e2
                // substitute v for y in e2

                Map<Identifier, Expr> bindings = new HashMap<>(binding_list.getLast());
                bindings.put(le.y(), e1);
                binding_list.addLast(bindings);

                Expr result = toOverture(le.e2());
                binding_list.removeLast();

                yield result;
            }
            case FunctionCall fe -> {
                // evalConstraint the parameters first
                List<Expr> actualParams = fe
                        .parameters()
                        .stream()
                        .map(this::toOverture)
                        .toList();

                // bind former parameters to actual parameters in function context
                Map<Identifier, Expr> bindings = new HashMap<>();


                for(int i = 0; i < actualParams.size(); i++){
                    bindings.put(program.resolveExprFunction(fe.fname()).y().get(i), actualParams.get(i));
                }
                binding_list.add(bindings);

                Expr result = toOverture(program.resolveExprFunction(fe.fname()).e());

                binding_list.removeLast();
                yield result;
            }
            case FieldExpr fe -> {
                TreeMap<Identifier, Expr> field = new TreeMap<>();
                for(Map.Entry<Identifier, Expr> element : fe.elements().entrySet()){
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
                List<Expr> actual_parameters =
                        functionCall.parameters().stream().map(this::toOverture).toList();

                // find a function with the same function name
                CommandFunction function = program.resolveCommandFunction(functionCall.fname());

                // map former parameters to actual parameters
                Map<Identifier, Expr> bindings = new HashMap<>();
                for (int i = 0; i < actual_parameters.size(); i++) {
                    bindings.put(function.typedVariables().get(i).y(), actual_parameters.get(i));
                }

                binding_list.add(bindings);

                // evalConstraint the function body with the actual parameters
                PreludeCommand result = evalInstruction(function.c());
                binding_list.removeLast(); 

                yield result;
            }

            case LetCommand letCommand -> {
                // let y = e in c
                // evalConstraint e
                Expr v = toOverture(letCommand.e());
                // let y = v in c
                Map<Identifier, Expr> binding = new HashMap<>(binding_list.getLast());
                binding.put(letCommand.y(), v);
                binding_list.addLast(binding);

                PreludeCommand result = evalInstruction(letCommand.c());
                binding_list.removeLast();
                yield result;
            }

            case CommandList commandList -> new CommandList(evalInstruction(commandList.c1()), evalInstruction(commandList.c2()));
            

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

    public Constraint evalConstraint(Constraint e) {
        return switch (e) {
            case AndConstraint x -> new AndConstraint(evalConstraint(x.e1()), evalConstraint(x.e2()));
            case NotConstraint x -> new NotConstraint(evalConstraint(x.e()));
            case EqualConstraint x -> new EqualConstraint(toOverture(x.e1()), toOverture(x.e2()));
            case FunctionCall x -> {
                ConstraintFunction function = program.resolveConstraintFunction(x.fname());
                List<Identifier> formalParams = function.params();
                List<Expr> actualParams = x
                        .parameters()
                        .stream()
                        .map(this::toOverture)
                        .toList();

                Map<Identifier, Expr> bindings = new HashMap<>();
                for (int i = 0; i < actualParams.size(); i++) {
                    bindings.put(formalParams.get(i), actualParams.get(i));
                }

                binding_list.add(bindings);
                Constraint result = evalConstraint(function.body());
                binding_list.removeLast();

                yield result;
            }
            case TrueConstraint x -> x;
            default -> throw new IllegalArgumentException("Bad constraint");
        };
    }

}
