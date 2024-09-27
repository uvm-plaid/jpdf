package plaid.ast;

import java.util.Map;
import java.util.Objects;

public class FieldExpr implements PreludeExpression{
    private final Map<Identifier, PreludeExpression> elements;


    // constructor
    public FieldExpr(Map<Identifier, PreludeExpression> elements){
        this.elements = elements;
    }

    public Map<Identifier, PreludeExpression> getElements() {
        return elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldExpr fieldExpr = (FieldExpr) o;
        return Objects.equals(elements, fieldExpr.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(elements);
    }
}
