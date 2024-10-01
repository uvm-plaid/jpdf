package plaid.antlr;

import org.junit.Test;
import plaid.ast.Program;

import static org.junit.Assert.assertNotNull;

public class FullProgramParseTest {

    /**
     * Successfully parses a confidentiality example.
     */
    @Test
    public void confidentialityExample() {
        Program program = Loader.toProgram("""
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

                andtablegmw(x, y, z, i) {
                    let r11 = (r[z] + (m[x] + 1) * (m[y] + 1))@i in
                    let r10 = (r[z] + (m[x] + 1) * (m[y] + 0))@i in
                    let r01 = (r[z] + (m[x] + 0) * (m[y] + 1))@i in
                    let r00 = (r[z] + (m[x] + 0) * (m[y] + 0))@i in
                    { row1 = r11; row2 = r10; row3 = r01; row4 = r00 }
                }

                cmdfunctions:

                encodegmw(n, i1, i2) {
                    m[n]@i2 := (s[n] + r[n])@i1;
                    m[n]@i1 := r[n]@i1
                }
                
                andgmw(z, x, y) {
                   let table = andtablegmw(x,y,z,1) in
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
                
                """);

        System.out.print(program);
        assertNotNull(program);
    }
}
