package plaid;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;
import plaid.OvertureParser.AssignmentContext;
import plaid.OvertureParser.ProgramContext;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class OvertureParserTest {

    private static ProgramContext loadProgram(String src) {
        ANTLRInputStream input = new ANTLRInputStream(src);
        OvertureLexer lexer = new OvertureLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        OvertureParser parser = new OvertureParser(tokens);
        parser.setBuildParseTree(true);
        // TODO Would be nice to see error AND throw exception...
        parser.setErrorHandler(new BailErrorStrategy());
        return parser.program();
    }

    /**
     * Programs must have at least one assignment.
     */
    @Test(expected = ParseCancellationException.class)
    public void empty() {
        loadProgram("");
    }

    /**
     * Nonsensical assignment but similar to example in Section 2.3.
     */
    @Test
    public void assignOutput() {
        // TODO This line doesn't work when using IDENTIFIER instead of VALUE
        //ProgramContext program = loadProgram("out @ 1 := (p[1] + p[2] + p[3]) @ 4");
        ProgramContext program = loadProgram("out @ 1 := (p[1] + p[2] + p[3]) @ 4");
        List<AssignmentContext> assignments = program.assignment();
        assertEquals("Program should have one assignment", 1, assignments.size());

        AssignmentContext assignment = program.assignment().getFirst();
        assertEquals("4", assignment.atparty().party().VALUE().getText());
    }

}
