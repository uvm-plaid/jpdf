package plaid.ast;

import java.util.Map;
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
}
