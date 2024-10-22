package plaid.ast;

import java.util.List;

public record OutputExpr() implements MemoryExpr {

    @Override
    public Iterable<Node> children() {
        return List.of();
    }

    @Override
    public String prettyPrint(){
        return "out";
    }
}
