package plaid.ast;

import java.util.List;
import java.util.Objects;

// TODO: add party index
public class AssertCommand implements  PreludeCommand{
    private final PreludeExpression e1;
    private final PreludeExpression e2;
    //private final PreludeExpression e3;

    public AssertCommand(PreludeExpression e1, PreludeExpression e2){
        this.e1 = e1;
        this.e2 = e2;
        //this.e3 = e3;
    }

    public PreludeExpression getE1() {
        return e1;
    }

    public PreludeExpression getE2() {
        return e2;
    }

    //public PreludeExpression getE3() {return e3; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssertCommand that = (AssertCommand) o;
        return Objects.equals(e1, that.e1) && Objects.equals(e2, that.e2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }

    @Override
    public Iterable<Node> children() {
        return List.of(e1, e2);
    }

    @Override
    public String toString() {
        return "AssertCommand{" +
                "e1=" + e1 +
                ", e2=" + e2 +
                '}';
    }

    @Override
    public String prettyPrint(){
//        return "assert" + "("  + e1.prettyPrint() + " = " + e2.prettyPrint() + ")" + "@" + e3.prettyPrint() + "\n";
        return "assert" + "("  + e1.prettyPrint() + " = " + e2.prettyPrint() + ")" + "\n";
    }

}
