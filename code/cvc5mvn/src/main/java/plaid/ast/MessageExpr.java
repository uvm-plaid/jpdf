package plaid.ast;

import java.util.List;

public record MessageExpr(PreludeExpression getE) implements MemoryExpr {

    @Override
    public Iterable<Node> children() {
        return List.of(getE);
    }

}
