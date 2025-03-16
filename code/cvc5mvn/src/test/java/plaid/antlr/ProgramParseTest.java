package plaid.antlr;

import org.junit.Test;
import plaid.ast.Program;

import static org.junit.Assert.assertNotNull;

public class ProgramParseTest {

    /**
     * parses an example program with postcondition.
     */
    @Test
    public void programWithPostcondition() {
        Program program = Loader.toProgram("""
                        exprfunctions:
               
                        not(x){ x + 1 }
     
                        cmdfunctions:
                
                        oneTimePad(x:string, y:string, z:string) {
                            m[z]@1 := (s[x] + not(r[y]))@2
                        }
                
                        main(){ oneTimePad("foo", "bar", "pan") }
                        
                 
                        postcondition: (m["pan"]@1 ==  s["foo"]@2 + (r["bar"]@2+1))
                """);

        //System.out.print(program);
        assertNotNull(program);
    }

    /**
     * parses an example program with both precondition and postcondition.
     */
    @Test
    public void programWithPrePostcondition() {
        Program program = Loader.toProgram("""
                        exprfunctions:
               
                        not(x){ x + 1 }
     
                        cmdfunctions:
                
                        oneTimePad(x:string , y:string, z:string) {
                            m[z]@1 := (s[x] + not(r[y]))@2
                        }
                
                        main(){ oneTimePad("foo", "bar", "pan") }
                
                        precondition: (m["pan"]@1 ==  s["foo"]@2 + (r["bar"]@2+1))
                        postcondition: (m["pan"]@1 ==  s["foo"]@2 + (r["bar"]@2+1))
                """);

        //System.out.print(program);
        assertNotNull(program);
    }
    
}
