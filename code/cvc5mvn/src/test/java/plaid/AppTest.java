package plaid;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        String prog = "\"asdf\"";
        ANTLRInputStream input = new ANTLRInputStream(prog);
        OvertureLexer lexer = new OvertureLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        OvertureParser parser = new OvertureParser(tokens);
        ParseTree tree = parser.expression();
        assertTrue(tree.getText().equals("\"asdf\""));
    }
}
