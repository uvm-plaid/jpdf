package plaid.cvc;

import io.github.cvc5.CVC5ApiException;
import org.junit.Test;
import plaid.antlr.Loader;
import plaid.ast.*;
import scala.concurrent.impl.FutureConvertersImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class GenEntailVerifierTest {

    //private final GenEntailVerifier genEntailVerifier = new GenEntailVerifier();
    
    /**
     * is string expression generated with different number of $ symbol every time genFreshString() is invoked?
     */
    @Test
    public void genFreshStringTest() throws CVC5ApiException{
        GenEntailVerifier genEntailVerifier = new GenEntailVerifier(); // initialize counter = 0
        
        assertEquals(new Str("$"), GenEntailVerifier.genFreshValue(new StringType()));
        assertEquals(new Str("$$"), GenEntailVerifier.genFreshValue(new StringType()));
        assertEquals(new Str("$$$"), GenEntailVerifier.genFreshValue(new StringType()));         
    }

    /**
     * is number expression generated with different number?
     */
    @Test
    public void genFreshCIDTest() throws CVC5ApiException{
        GenEntailVerifier genEntailVerifier = new GenEntailVerifier(); // initialize counter = 0
        
        assertEquals(new Num(-1),  GenEntailVerifier.genFreshValue(new PartyIndexType()));
        assertEquals(new Num(-2), GenEntailVerifier.genFreshValue(new PartyIndexType()));
        assertEquals(new Num(-3), GenEntailVerifier.genFreshValue(new PartyIndexType()));
    }

    /**
     * does genFreshRecord construct a field (record) in the given types?
     */
    @Test
    public void genFreshRecordTest() throws CVC5ApiException {
        String src = "{ s : string ; i : cid}";
        // initialize counter = 1
        GenEntailVerifier genEntailVerifier = new GenEntailVerifier(); 

        RecordType recordType = (RecordType) Loader.toType(src); // convert a string src into AST
        
        TreeMap<Identifier, PreludeExpression> map = new TreeMap<>();
        map.put(new Identifier("s"), new Str("$$"));
        map.put(new Identifier("i"), new Num(-1));
        Object expected = new FieldExpr(map); 
        assertEquals(expected, GenEntailVerifier.genFreshValue(recordType));

    }

    /**
     * recordtype can have another recordtype as a value
     */
    @Test
    public void nestedRecordType() throws CVC5ApiException {
        String src = "{t: {s:string; t2:{i:cid}}}";
        // initialize counter = 1
        GenEntailVerifier genEntailVerifier = new GenEntailVerifier();
        
        RecordType recordType = (RecordType) Loader.toType(src);
        
        TreeMap<Identifier, PreludeExpression> innermap = new TreeMap<>();
        innermap.put(new Identifier("s"), new Str("$"));
        innermap.put(new Identifier("t2"), new FieldExpr(new TreeMap<>(Map.of(new Identifier("i"), new Num(-2)))));
        
        Object expected = new FieldExpr(new TreeMap<>(Map.of(new Identifier("t"), new FieldExpr(innermap))));
        assertEquals(expected, GenEntailVerifier.genFreshValue(recordType));
        
    }

    /**
     * precondition entails postcondition for all inputs
     */
    @Test
    public void genEntailTest() throws CVC5ApiException{
        String e1 = "m[z++\"s\"]@i1 == m[x++\"s\"]@i1 + m[y++\"s\"]@i1 AND m[z++\"s\"]@i2 == m[x++\"s\"]@i2 + m[y++\"s\"]@i2";
        String e2 = "m[z++\"s\"]@i1 + m[z++\"s\"]@i2 == m[x++\"s\"]@i1 + m[y++\"s\"]@i1 + m[x++\"s\"]@i2 + m[y++\"s\"]@i2";

        GenEntailVerifier genEntailVerifier = new GenEntailVerifier();
        Map<Identifier, Type> typing = new HashMap<>(); 
        typing.put(new Identifier("i2"), new PartyIndexType());
        typing.put(new Identifier("y"), new StringType());
        typing.put(new Identifier("x"), new StringType());
        typing.put(new Identifier("i1"), new PartyIndexType());
        typing.put(new Identifier("z"), new StringType());

        // e1 entails e2 
        boolean result1 = genEntailVerifier.genEntails(List.of(typing), Loader.toConstraintExpression(e1), Loader.toConstraintExpression(e2));
        assertTrue(result1);
    
        // e2 does not entail e1
        boolean result2 = genEntailVerifier.genEntails(List.of(typing), Loader.toConstraintExpression(e2), Loader.toConstraintExpression(e1));
        assertFalse(result2);
    }
}
