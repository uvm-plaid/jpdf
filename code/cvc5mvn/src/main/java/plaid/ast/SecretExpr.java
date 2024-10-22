package plaid.ast;

import java.util.List;

public record SecretExpr(PreludeExpression getE) implements MemoryExpr {

    @Override
    public Iterable<Node> children() {
        return List.of(getE);
    }

    @Override
    public String prettyPrint(){
        return "s[\"" + getE().prettyPrint() + "\"]";
    }
}
