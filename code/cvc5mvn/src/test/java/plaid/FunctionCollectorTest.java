package plaid;

import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class FunctionCollectorTest extends AbstractPreludeTest{
    @Test
    public void addsCommandFunction(){
        PreludeParser.ProgramContext program =
                loadProgram(""" 
                        main() { baz("foo","bar",1); out@1 := bar("foo","bar")@1 }""");

        FunctionCollector functionCollector = new FunctionCollector();
        new ParseTreeWalker().walk(functionCollector, program);

        assertEquals(Collections.singleton("main"), functionCollector.getCommandFunctions().keySet());
    }

    @Test
    public void addsMultipleFunctions(){
        PreludeParser.ProgramContext program =
                loadProgram(""" 
                        f1() { baz("foo","bar",1); out@1 := bar("foo","bar")@1 }
                        f2() { baz("foo","bar",1); out@1 := bar("foo","bar")@1 }""");

        FunctionCollector functionCollector = new FunctionCollector();
        new ParseTreeWalker().walk(functionCollector, program);

        Set<String> expected = new HashSet<>();
        expected.add("f1");
        expected.add("f2");
        assertEquals("Two functions added with correct names", expected, functionCollector.getCommandFunctions().keySet());
        List<PreludeParser.CommandFuncContext> nodes = new ArrayList<>(functionCollector.getCommandFunctions().values());
        assertNotEquals("Parse tree nodes are different", nodes.get(0), nodes.get(1));
    }
}
