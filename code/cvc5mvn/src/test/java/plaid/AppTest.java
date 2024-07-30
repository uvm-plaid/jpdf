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
        String prog = "p[\"foo\"] * 12 + m[\"bar\"]";
        ANTLRInputStream input = new ANTLRInputStream(prog);
        OvertureLexer lexer = new OvertureLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Print tokens before filtering
        //tokens.fill();
        for (Object tok : tokens.getTokens()){
            System.out.println(tok);
        }
        OvertureParser parser = new OvertureParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.expression();
        System.out.println(tree.toStringTree(parser));    }
}
