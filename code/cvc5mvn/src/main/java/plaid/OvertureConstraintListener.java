package plaid;

import io.github.cvc5.*;
import org.antlr.v4.runtime.RuleContext;
import java.util.List;

/*
 * Identifies expressions in Overture and converts them into constraint terms in a cvc5 solver
 */
public class OvertureConstraintListener extends OvertureBaseVisitor<Term> {
//    private final TermManager termManager;
//    private final Sort prime;
//    private Iterable<Memory> memories;
//
//    // constructor
//    public OvertureConstraintListener(TermManager termManager, Sort sort, Iterable<Memory> memories){
//        this.termManager = termManager;
//        this.prime = sort;
//        this.memories = memories;
//    }
//
//    @Override
//    protected Term aggregateResult(Term aggregate, Term nextResult) {
//        return aggregate == null ? nextResult : aggregate;
//    }
//    @Override
//    public Term visitValueExpr(OvertureParser.ValueExprContext ctx) {
//        try {
//            return termManager.mkFiniteFieldElem(ctx.VALUE().getText(), prime, 10);
//        } catch (CVC5ApiException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public Term visitMinusExpr(OvertureParser.MinusExprContext ctx) {
//        Term expr = visit(ctx.expression());
//        try {
//            return termManager.mkTerm(Kind.FINITE_FIELD_MULT, expr, termManager.mkFiniteFieldElem("-1", prime, 10));
//        } catch (CVC5ApiException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public Term visitPlusExpr(OvertureParser.PlusExprContext ctx) {
//        Term expr1 = visit(ctx.expression(0));
//        Term expr2 = visit(ctx.expression(1));
//
//        return termManager.mkTerm(Kind.FINITE_FIELD_ADD, expr1, expr2);
//
//    }
//
//    @Override
//    public Term visitTimesExpr(OvertureParser.TimesExprContext ctx) {
//        Term expr1 = visit(ctx.expression(0));
//        Term expr2 = visit(ctx.expression(1));
//
//        return termManager.mkTerm(Kind.FINITE_FIELD_MULT, expr1, expr2);
//    }
//
//    @Override
//    public Term visitParenExpr(OvertureParser.ParenExprContext ctx) {
//        return visit(ctx.expression());
//    }
//
//    @Override
//    public Term visitMessageMemory(OvertureParser.MessageMemoryContext ctx) {
//        return lookup(ctx);
//    }
//
//    @Override
//    public Term visitPublicMemory(OvertureParser.PublicMemoryContext ctx) {
//        return lookup(ctx);
//    }
//
//    @Override
//    public Term visitSecretMemory(OvertureParser.SecretMemoryContext ctx) {
//        return lookup(ctx);
//    }
//
//    @Override
//    public Term visitRandomMemory(OvertureParser.RandomMemoryContext ctx) {
//        return lookup(ctx);
//    }
//
//    private Term lookup(RuleContext ctx){
//        for (Memory memory : memories){
//            if(memory.getContexts().contains(ctx)){
//                return memory.getTerm();
//            }
//        }
//        throw new IllegalArgumentException("Constant not found");
//    }
//
//    @Override
//    public Term visitCommand(OvertureParser.CommandContext ctx) {
//        Term srcExpr = visit(ctx.source());
//        Term destExpr = visit(ctx.dest());
//
//        return termManager.mkTerm(Kind.EQUAL, destExpr, srcExpr);
//    }
//
//    @Override
//    public Term visitOutputMemory(OvertureParser.OutputMemoryContext ctx) {
//        return lookup(ctx);
//    }
}
