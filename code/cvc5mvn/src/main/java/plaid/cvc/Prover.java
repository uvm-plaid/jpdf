package plaid.cvc;

import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;
import plaid.ast.MemoryExpr;
import plaid.ast.MessageExpr;
import plaid.ast.Node;
import plaid.ast.Num;
import plaid.ast.OutputExpr;
import plaid.ast.PreludeExpression;
import plaid.ast.PublicExpr;
import plaid.ast.RandomExpr;
import plaid.ast.SecretExpr;
import plaid.ast.Str;

import java.util.Collection;
import java.util.HashSet;

public class Prover {

    private final Sort sort;
    private final TermManager termManager;
    private final Collection<Memory> memories = new HashSet<>();

    public Prover(TermManager termManager, Sort sort) {
        this.termManager = termManager;
        this.sort = sort;
    }

    public Collection<Memory> getMemories() {
        return memories;
    }

    public void register(Node node) {
        switch (node) {
            case MemoryExpr x -> register(x);
            case Node x -> x.children().forEach(this::register);
        }
    }

    public void register(MemoryExpr expr) {
        String name = getCvcName(expr);
        if (memories.stream().noneMatch(x -> x.name().equals(name))) {
            memories.add(new Memory(name, termManager.mkConst(sort, name), expr));
        }
    }

    public static String getCvcName(MemoryExpr expr) {
        return switch (expr) {
            case MessageExpr mem -> "m_" + toString(mem.getE()) + "_" + toInt(mem.getI());
            case OutputExpr mem -> "o_" + toInt(mem.getI());
            case PublicExpr mem -> "p_" + toString(mem.getE());
            case RandomExpr mem -> "r_" + toString(mem.getE()) + "_" + toInt(mem.getI());
            case SecretExpr mem -> "s_" + toString(mem.getE()) + "_" + toInt(mem.getI());
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
