package plaid.eval;

import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;
import org.junit.Test;
import plaid.antlr.Loader;
import plaid.ast.*;
import plaid.cvc.TermFactory;
import plaid.cvc.Verifier;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProgramEvaluatorTest {

    /**
     * test confidentiality example
     */
    @Test
    public void testConfidentiality() throws CVC5ApiException {
        String program = """
                exprfunctions: 
                not(x){
                    x + 1
                }
                
                mux4(s1, s2, b1, b2, b3, b4){
                    ((s1 * s2) * b4) +
                    ((not(s1) * s2) * b3) +
                    ((s1 * not(s2)) * b2) +
                    ((not(s1) * not(s2)) * b1)
                }
                
                andtablegmw(x, y, z) {
                    let r11 = r[z] + (m[x] + 1) * (m[y] + 1) in
                    let r10 = r[z] + (m[x] + 1) * (m[y] + 0) in
                    let r01 = r[z] + (m[x] + 0) * (m[y] + 1) in
                    let r00 = r[z] + (m[x] + 0) * (m[y] + 0) in
                    { row1 = r11; row2 = r10; row3 = r01; row4 = r00 }
                }              
                
                cmdfunctions:
                encodegmw(n, i1, i2) {
                    m[n]@i2 := (s[n] + r[n])@i1;
                    m[n]@i1 := r[n]@i1
                }
                
                andgmw(z, x, y) {
                   let table = andtablegmw(x,y,z) in
                   m[x]@1 := m[x]@2;
                   m[y]@1 := m[y]@2;
                   m[z]@2 := mux4(m[x], m[y], table.row1, table.row2, table.row3, table.row4)@1;
                   m[z]@1 := r[z]@1
                }
                
                xorgmw(z, x, y) {
                        m[z]@1 := (m[x] + m[y])@1; m[z]@2 := (m[x] + m[y])@2
                }
                
                decodegmw(z) {
                        p["1"] := m[z]@1; p["2"] := m[z]@2;
                        out@1 := (p["1"] + p["2"])@1;
                        out@2 := (p["1"] + p["2"])@2
                }
                
                main(){
                    encodegmw("x",2,1);
                    encodegmw("y",2,1);               
                    encodegmw("z",1,2);                 
                    andgmw("g1","x","z");
                    xorgmw("g2","g1","y");
                    decodegmw("g2")
                }

                """;
        Program src = Loader.toProgram(program);
        ProgramEvaluator programEvaluator = new ProgramEvaluator(src);
        PreludeCommand actual_output = programEvaluator.eval();

        String expected_output_1 = """
                m["x"]@1 := (s["x"] + r["x"])@2;
                m["x"]@2 := r["x"]@2
                """;

        String expected_output_2 = """
                m["y"]@1 := (s["y"] + r["y"])@2;
                m["y"]@2 := r["y"]@2
                """;

        String expected_output_3 = """
                m["z"]@2 := (s["z"] + r["z"])@1;
                m["z"]@1 := r["z"]@1
                """;

        String expected_output_4 = """
                m["x"]@1 := m["x"]@2;
                m["z"]@1 := m["z"]@2;
                m["g1"]@2 := (((((m["x"] * m["z"]) * (r["g1"] + ((m["x"] + 0) * (m["z"] + 0)))) +
                        (((m["x"]+1) * m["z"]) * (r["g1"] + ((m["x"] + 0) * (m["z"] + 1))))) +
                        ((m["x"] * (m["z"]+1)) * (r["g1"] + ((m["x"] + 1) * (m["z"] + 0))))) +
                        (((m["x"]+1) * (m["z"]+1)) * (r["g1"] + ((m["x"] + 1) * (m["z"] + 1)))))@1;
                m["g1"]@1 := r["g1"]@1
                """;

        String expected_output_5 = """
                m["g2"]@1 := (m["g1"] + m["y"])@1; 
                m["g2"]@2 := (m["g1"] + m["y"])@2
                """;

        String expected_output_6 = """
                p["1"] := m["g2"]@1; p["2"] := m["g2"]@2;
                out@1 := (p["1"] + p["2"])@1;
                out@2 := (p["1"] + p["2"])@2
                """;

        CommandList expected_output =
                new CommandList(List.of(Loader.toCommand(expected_output_1),
                        new CommandList(List.of(Loader.toCommand(expected_output_2),
                        new CommandList(List.of(Loader.toCommand(expected_output_3),
                        new CommandList(List.of(Loader.toCommand(expected_output_4),
                        new CommandList(List.of(Loader.toCommand(expected_output_5), Loader.toCommand(expected_output_6)))))))))));

        assertEquals(expected_output, actual_output);

        // test cvc5 (semantic equality)
        Verifier.verify(actual_output);
        Verifier.verify(expected_output);

    }
}



