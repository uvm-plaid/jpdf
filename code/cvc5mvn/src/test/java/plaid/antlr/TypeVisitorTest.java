package plaid.antlr;

import org.junit.Test;
import plaid.ast.*;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

public class TypeVisitorTest {
    
    private static Type ast(String src){
        return Loader.toType(src);
    }
    
    /**
     * Parses client id type
     */
    @Test
    public void partyIndexType(){
        // using loader I can convert cid as AST, PartyIndexType() 
        assertEquals(new PartyIndexType(), ast("cid"));
    }

    /**
     * Parses string type
     */
    @Test
    public void stringType(){
        assertEquals(new StringType(), ast("string"));
    }

    /**
     * Parses record type
     */
    @Test
    public void recordType(){
        TreeMap<Identifier, Type> map = new TreeMap<>();
        map.put(new Identifier("s"), new StringType());
        map.put(new Identifier("i"), new PartyIndexType());
        RecordType expected = new RecordType(map);
        
        assertEquals(expected, ast("{s: string; i: cid}"));        
    }

    /**
     * parses nested record type
     */
    @Test
    public void nestedRecordType(){
        TreeMap<Identifier, Type> map = new TreeMap<>();
        map.put(new Identifier("s"), new RecordType(new TreeMap<>(Map.of(new Identifier("i"), new PartyIndexType()))));
        RecordType expected = new RecordType(map);

        assertEquals(expected, ast("{s: {i: cid}}"));
    }

}
