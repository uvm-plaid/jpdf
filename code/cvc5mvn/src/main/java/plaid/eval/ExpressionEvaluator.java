package plaid.eval;

import plaid.ast.PreludeExpression;
import plaid.ast.*;

import java.util.*;

/**
 * operational semantics for Prelude Expressions
  */

public class ExpressionEvaluator {

    private final Program program;
    List<Map<Identifier, PreludeExpression>> binding_list;

    public ExpressionEvaluator(Program program) {
        this.program = program;
        binding_list = new LinkedList<>();
        binding_list.add(new HashMap<>());
    }

    /**
     * Evaluate Prelude Expressions
     * @param e
     * @return Overture Expressions
     * @throws Exception
     */
    public PreludeExpression toOverture(PreludeExpression e) {
        return switch(e){
            // base cases
            case Str w -> new Str(w.str());
            case Num i -> new Num(i.num());
            case Identifier id -> binding_list.getLast().get(id); // throws an error if the list doesn't contain the id

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
                // evaluate e1 first
                PreludeExpression e1 = toOverture(le.e1());
                // let y = v in e2
                // substitute v for y in e2

                Map<Identifier, PreludeExpression> bindings = new HashMap<>(binding_list.getLast());
                bindings.put(le.y(), e1);
                binding_list.addLast(bindings);

                PreludeExpression result = toOverture(le.e2());
                binding_list.removeLast();
                //subst(le.getE2(), e1, le.getY());
                //isVal(le.getE1()) ?  : new LetExpr(le.getY(), toOverture(le.getE1()), le.getE2());
                yield result;
            }
            case FunctionCallExpr fe -> {
                // evaluate the parameters first
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
                Map<Identifier, PreludeExpression> field = new HashMap<>();
                for(Map.Entry<Identifier, PreludeExpression> element : fe.elements().entrySet()){
                    field.put(element.getKey(), toOverture(element.getValue()));
                }

                yield new FieldExpr(field);
            }
            case FieldSelectExpr fse -> {
                // evaluate field expression first
                FieldExpr field = (FieldExpr) toOverture(fse.e());
                yield field.elements().get(fse.l());
            }

            case AtExpr ae -> new AtExpr(toOverture(ae.e1()), toOverture(ae.e2()));
            case OTExpr oe -> new OTExpr(toOverture(oe.e1()), toOverture(oe.i1()), toOverture(oe.e2()), toOverture(oe.e3()));
            default -> throw new IllegalArgumentException("Bad Expression");
        };
    }


}
