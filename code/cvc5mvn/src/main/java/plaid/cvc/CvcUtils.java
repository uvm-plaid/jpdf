package plaid.cvc;

import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;
import plaid.ast.MemoryExpr;
import plaid.ast.MessageExpr;
import plaid.ast.Num;
import plaid.ast.OutputExpr;
import plaid.ast.PreludeExpression;
import plaid.ast.PublicExpr;
import plaid.ast.RandomExpr;
import plaid.ast.SecretExpr;
import plaid.ast.Str;

public class CvcUtils {

    public static Sort mkFiniteFieldSort(TermManager termManager, String s, int i) {
        try {
            return termManager.mkFiniteFieldSort(s, i);
        } catch (CVC5ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static Term mkFiniteFieldElem(TermManager termManager, String s, Sort sort, int i) {
        try {
            return termManager.mkFiniteFieldElem(s, sort, i);
        } catch (CVC5ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCvcName(MemoryExpr expr, Integer partyIndex) {
        return switch (expr) {
            case MessageExpr mem -> "m_" + toString(mem.getE()) + "_" + partyIndex;
            case OutputExpr ignored -> "o_" + partyIndex;
            case PublicExpr mem -> "p_" + toString(mem.getE());
            case RandomExpr mem -> "r_" + toString(mem.getE()) + "_" + partyIndex;
            case SecretExpr mem -> "s_" + toString(mem.getE()) + "_" + partyIndex;
            default -> throw new IllegalArgumentException("Must be memory, found " + expr.getClass().getName());
        };
    }

    public static int toInt(PreludeExpression expr) {
        return switch (expr) {
            case Num num -> num.getNum();
            default -> throw new IllegalArgumentException("Must be number, found " + expr.getClass().getName());
        };
    }

    public static String toString(PreludeExpression expr) {
        return switch (expr) {
            case Str str -> str.getStr();
            default -> throw new IllegalArgumentException("Must be string, found " + expr.getClass().getName());
        };
    }

}
