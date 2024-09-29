package plaid.ast;

import java.util.Objects;

public class AssignCommand implements PreludeCommand{
    private final PreludeExpression e1;
    private final PreludeExpression e2;

    public AssignCommand(PreludeExpression e1, PreludeExpression e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    public PreludeExpression getE1() {
        return e1;
    }

    public PreludeExpression getE2() {
        return e2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignCommand that = (AssignCommand) o;
        return Objects.equals(e1, that.e1) && Objects.equals(e2, that.e2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }
}