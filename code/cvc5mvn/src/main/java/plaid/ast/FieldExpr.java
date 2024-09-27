package plaid.ast;

import java.util.Map;

public class FieldExpr implements PreludeExpression{
    private final Map<Identifier, PreludeExpression> elements;


    // constructor
    public FieldExpr(Map<Identifier, PreludeExpression> elements){
        this.elements = elements;
    }

    public Map<Identifier, PreludeExpression> getElements() {
        return elements;
    }
}
