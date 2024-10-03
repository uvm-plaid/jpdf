package plaid.ast;

import java.util.Map;
import java.util.Objects;

@Deprecated
public class Field extends Value{
    private final Map<Identifier, PreludeExpression> elements;

    // constructor
    public Field(Map<Identifier, PreludeExpression> elements){
        this.elements = elements;
    }

    public Map<Identifier, PreludeExpression> getElements() {
        return elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return Objects.equals(elements, field.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(elements);
    }
    @Override
    public Iterable<Node> children() {
        throw new UnsupportedOperationException();
    }
}
