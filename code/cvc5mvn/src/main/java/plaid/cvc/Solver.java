package plaid.cvc;

import plaid.ast.MemoryExpr;
import plaid.ast.MessageExpr;
import plaid.ast.Num;
import plaid.ast.OutputExpr;
import plaid.ast.PreludeExpression;
import plaid.ast.PublicExpr;
import plaid.ast.RandomExpr;
import plaid.ast.SecretExpr;
import plaid.ast.Str;

public class Solver {

    static Iterable<Memory> collectMemories(PreludeExpression expr) {
        throw new UnsupportedOperationException();
    }

    static String getCvcName(MemoryExpr expr) {
        return switch (expr) {
            case MessageExpr mem -> "m_" + toString(mem.getE()) + "_" + toInt(mem.getI());
            case OutputExpr mem -> "o_" + toInt(mem.getI());
            case PublicExpr mem -> "p_" + toString(mem.getE());
            case RandomExpr mem -> "r_" + toString(mem.getE()) + "_" + toInt(mem.getI());
            case SecretExpr mem -> "s_" + toString(mem.getE()) + "_" + toInt(mem.getI());
            default -> throw new IllegalArgumentException("Must be memory, found " + expr.getClass().getName());
        };
    }

    static int toInt(PreludeExpression expr) {
        return switch (expr) {
            case Num num -> num.getNum();
            default -> throw new IllegalArgumentException("Must be number, found " + expr.getClass().getName());
        };
    }

    static String toString(PreludeExpression expr) {
        return switch (expr) {
            case Str str -> str.getStr();
            default -> throw new IllegalArgumentException("Must be string, found " + expr.getClass().getName());
        };
    }

}
