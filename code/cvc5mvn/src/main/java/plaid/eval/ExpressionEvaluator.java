package plaid.eval;

import plaid.ast.OvertureExpression;
import plaid.ast.PreludeExpression;
import plaid.ast.*;

import java.util.*;
import java.util.stream.Collectors;

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
     * check whether a PreludeExpression e is value
     * @param e
     * @return true iff e is value
     */
    public static boolean isVal(PreludeExpression e){
        return switch(e){
            case Str w -> true;
            //case IndexValue i -> true;
            case FieldExpr f -> isFieldValue(f);
            case OvertureExpression oe -> true;
            case OvertureVariable ov -> true;
            default -> false;
        };
    }

    /**
     * auxilary method to check whether a field expression is value or not
     * @param e
     * @return
     */
    public static boolean isFieldValue(FieldExpr e){
            for (PreludeExpression v : e.getElements().values()){
                if(isVal(v) == false){
                    return false;
                }
            }
            return true;
    }

    /**
     * auxilary method to check whether parameters of a function call expression is value or not
     * @param e
     * @return
     */
    public boolean isParametersValue(List<PreludeExpression> e){
        for (PreludeExpression arg : e){
            if(isVal(arg) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param field
     * @param value
     * @return a corresponding key for a given value from a given Map
     * @param <K>
     * @param <V>
     */
    public static <K, V> K getKey(Map<K, V> field, V value){
        for (Map.Entry<K, V> entry : field.entrySet()){
            if(entry.getValue().equals(value))
                return entry.getKey();

        }
        return null;
    }


    /**
     * subsitute e2 for e1 if an identifier matches given ID
     * @param e1, e2, x
     * @return subsituted value
     */
//    public static PreludeExpression subst(PreludeExpression e1, PreludeExpression e2, Identifier x){
//        return switch(e1){
//            case Identifier id -> ((x.equals(id) ? e2 : e1));
//            case Str w -> w;
//            case RandomExpr re -> new RandomExpr(subst(re.getE(), e2, x));
//            case MessageExpr me -> new MessageExpr(subst(me.getE(), e2, x), me.getI());
//            case PublicExpr pe -> new PublicExpr(subst(pe.getE(), e2, x));
//            case PlusExpr pe -> new PlusExpr(subst(pe.getE1(), e2, x), subst(pe.getE2(), e2, x));
//            case MinusExpr me -> new MinusExpr(subst(me.getE(), e2, x));
//            case TimesExpr te -> new TimesExpr(subst(te.getE1(), e2, x), subst(te.getE2(), e2, x));
//
//            case FieldExpr fe -> {
//                fe.getElements().replace(getKey(fe.getElements(), x), e2);
//                yield new FieldExpr(fe.getElements());
//
//            }
//            case LetExpr le -> new LetExpr(le.getY(), subst(le.getE1(), e2, x), subst(le.getE2(), e2, x));
//                // let y = v in e
//                // e can be let y' = e1 in e2
//            // recursively find if an ID appears in e1 and e2. If it does, do a substitution.
//
//            default -> throw new IllegalArgumentException();
//        };
//    }


    /**
     * Evaluate Prelude Expressions
     * @param e
     * @return Overture Expressions
     * @throws Exception
     */
    public PreludeExpression toOverture(PreludeExpression e) {
        return switch(e){
            // base cases
            case Str w -> new Str(w.getStr());
            case Num i -> new Num(i.getNum());
            //case OvertureExpression oe -> oe;
            //case OvertureVariable ov -> ov;
            case Identifier id -> binding_list.getLast().get(id); // throws an error if the list doesn't contain the id

            case RandomExpr re -> new RandomExpr(toOverture(re.getE()));
            case SecretExpr se -> new SecretExpr(toOverture(se.getE()));
            case MessageExpr me -> new MessageExpr(toOverture(me.getE()));
            case PublicExpr pe -> new PublicExpr(toOverture(pe.getE()));
            case OutputExpr oe -> new OutputExpr();

            case TimesExpr te -> new TimesExpr(toOverture(te.getE1()), toOverture(te.getE2()));
            case PlusExpr pe -> new PlusExpr(toOverture(pe.getE1()), toOverture(pe.getE2()));
            // TODO: ("x" ++ "y") evaluates to Str(Str("x"), Str("y")).
            // Is this okay?
            case ConcatExpr ce -> {
                Str s1 = (Str) toOverture(ce.getE1());
                Str s2 = (Str) toOverture(ce.getE2());

                //String sb = String.valueOf(toOverture(ce.getE1())) +
                //        c);
                yield new Str(s1.getStr() + s2.getStr());
            }
            case MinusExpr me -> new MinusExpr(toOverture(me.getE()));

            case LetExpr le -> {
                // let y = e1 in e2
                // evaluate e1 first
                PreludeExpression e1 = toOverture(le.getE1());
                // let y = v in e2
                // substitute v for y in e2

                Map<Identifier, PreludeExpression> bindings = new HashMap<>(binding_list.getLast());
                bindings.put(le.getY(), e1);
                binding_list.addLast(bindings);

                PreludeExpression result = toOverture(le.getE2());
                binding_list.removeLast();
                //subst(le.getE2(), e1, le.getY());
                //isVal(le.getE1()) ?  : new LetExpr(le.getY(), toOverture(le.getE1()), le.getE2());
                yield result;
            }
            case FunctionCallExpr fe -> {
                // evaluate the parameters first
                List<PreludeExpression> actualParams = fe
                        .getParameters()
                        .stream()
                        .map(this::toOverture)
                        .toList();

                // bind former parameters to actual parameters in function context
                Map<Identifier, PreludeExpression> bindings = new HashMap<>();


                for(int i = 0; i < actualParams.size(); i++){
                    bindings.put(program.resolveExprFunction(fe.getFname()).getY().get(i), actualParams.get(i));
                }
                binding_list.add(bindings);

                PreludeExpression result = toOverture(program.resolveExprFunction(fe.getFname()).getE());

                binding_list.removeLast();
                yield result;
            }
            case FieldExpr fe -> {
                Map<Identifier, PreludeExpression> field = new HashMap<>();
                for(Map.Entry<Identifier, PreludeExpression> element : fe.getElements().entrySet()){
                    field.put(element.getKey(), toOverture(element.getValue()));
                }

                yield new FieldExpr(field);
            }
            case FieldSelectExpr fse -> {
                // evaluate field expression first
                FieldExpr field = (FieldExpr) toOverture(fse.getE());
                yield field.getElements().get(fse.getL());
            }

            case AtExpr ae -> new AtExpr(toOverture(ae.getE1()), toOverture(ae.getE2()));

            default -> throw new IllegalArgumentException("Bad Expression");
        };
    }


}
