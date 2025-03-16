package plaid.cvc;

import org.junit.Test;
import plaid.antlr.Loader;
import plaid.ast.*;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

public class GenEntailVerifierTest {

    //private final GenEntailVerifier genEntailVerifier = new GenEntailVerifier();
    
    /**
     * is string expression generated with different number of $ symbol every time genFreshString() is invoked?
     */
    @Test
    public void genFreshStringTest(){
        GenEntailVerifier genEntailVerifier = new GenEntailVerifier(); // initialize counter = 0
        genEntailVerifier.genEntails(null, null);
        
        assertEquals(new Str("$"), GenEntailVerifier.genFreshValue(new StringType()));
        assertEquals(new Str("$$"), GenEntailVerifier.genFreshValue(new StringType()));
        assertEquals(new Str("$$$"), GenEntailVerifier.genFreshValue(new StringType()));         
    }

    /**
     * is number expression generated with different number?
     */
    @Test
    public void genFreshCIDTest(){
        GenEntailVerifier genEntailVerifier = new GenEntailVerifier(); // initialize counter = 0
        genEntailVerifier.genEntails(null, null);
        
        assertEquals(new Num(-1),  GenEntailVerifier.genFreshValue(new PartyIndexType()));
        assertEquals(new Num(-2), GenEntailVerifier.genFreshValue(new PartyIndexType()));
        assertEquals(new Num(-3), GenEntailVerifier.genFreshValue(new PartyIndexType()));
    }

    /**
     * does genFreshRecord construct a field (record) in the given types?
     */
    @Test
    public void genFreshRecordTest(){
        String src = "{ s : string ; i : cid}";
        // initialize counter = 1
        GenEntailVerifier genEntailVerifier = new GenEntailVerifier(); 
        genEntailVerifier.genEntails(null, null);
        
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
    public void nestedRecordType(){
        String src = "{t: {s:string; t2:{i:cid}}}";
        // initialize counter = 1
        GenEntailVerifier genEntailVerifier = new GenEntailVerifier();
        genEntailVerifier.genEntails(null, null);
        
        RecordType recordType = (RecordType) Loader.toType(src);
        
        TreeMap<Identifier, PreludeExpression> innermap = new TreeMap<>();
        innermap.put(new Identifier("s"), new Str("$"));
        innermap.put(new Identifier("t2"), new FieldExpr(new TreeMap<>(Map.of(new Identifier("i"), new Num(-2)))));
        
        Object expected = new FieldExpr(new TreeMap<>(Map.of(new Identifier("t"), new FieldExpr(innermap))));
        assertEquals(expected, GenEntailVerifier.genFreshValue(recordType));
        
    }


}
