package plaid.eval;

import plaid.ast.*;

public class StrEvaluator {

//    public OvertureExpression toOverture(PreludeExpression in) {
//        return switch (in) {
//            case ConcatExpr e -> new Str(evalStr(e.getE1()) + evalStr(e.getE2()));
//            case Num e -> e;
//            case PlusExpr e -> new PlusExpr(toOverture(e.getE1()), toOverture(e.getE2()));
//            case Str e -> e;
//            default -> throw new IllegalArgumentException("Bad string expression: " + in);
//        };
//    }

//    private String evalStr(PreludeExpression in) {
//        Str str = (Str) toOverture(in);
//        return str.getStr();
//    }
}
