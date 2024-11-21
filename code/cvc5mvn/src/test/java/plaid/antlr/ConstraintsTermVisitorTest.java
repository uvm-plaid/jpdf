package plaid.antlr;

import org.junit.Test;
import plaid.ast.*;
import plaid.constraints.ast.*;
import plaid.constraints.ast.Node;

import static org.junit.Assert.assertEquals;

public class ConstraintsTermVisitorTest {

    private ConstraintsTerm ast(String src) {return ConstraintsLoader.toTerm(src);}

    /**
     * parses output term
     */
    @Test
    public void outputTerm(){
        assertEquals(new OutputConstraintTerm(2), ast("out@2"));
    }

    /**
     * parses secret term
     */
    @Test
    public void secretTerm(){
        assertEquals(new SecretConstraintsTerm("foo", 3), ast("s[\"foo\"]@3"));
    }

    /**
     * parses message term
     */
    @Test
    public void messageTerm(){
        assertEquals(new MessageConstraintsTerm("bar", 4), ast("m[\"bar\"]@4"));
    }

    /**
     * parses public term
     */
    @Test
    public void publicTerm(){
        assertEquals(new PublicConstraintsTerm("x"), ast("p[\"x\"]"));
    }

    /**
     * parses random term
     */
    @Test
    public void randomTerm(){
        assertEquals(new RandomConstraintsTerm("y", 1), ast("r[\"y\"]@1"));
    }

    /**
     * negation happens before party index
     */
    @Test
    public void minusFirst() {
        assertEquals(new MinusConstraintsTerm(new MessageConstraintsTerm("z", 1)), ast("-m[\"z\"]@1"));
    }

    /**
     * parentheses override operator precedence
     */
    @Test
    public void parenOverride(){
        assertEquals(new TimesConstraintsTerm
                        (new PlusConstraintsTerm
                                (new MessageConstraintsTerm("x", 2), 
                                        new RandomConstraintsTerm("y", 1)), new SecretConstraintsTerm("z", 3)) , 
                ast("(m[\"x\"]@2 + r[\"y\"]@1)*s[\"z\"]@3"));
    }

    /**
     * An expression is an expression no matter how many parens it is wrapped in.
     */
    @Test
    public void unlimitedParens() {
        Node none = ast("m[\"x\"]@1");
        Node many = ast("((((((m[\"x\"]@1))))))");
        assertEquals(many, none);
    }
}
