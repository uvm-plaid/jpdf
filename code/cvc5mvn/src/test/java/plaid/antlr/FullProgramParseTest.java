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

    @Test
    public void chrisExample(){
        Program program = Loader.toProgram("""
                    cmdfunctions: 
                    _open(x,i1,i2){
                      m[x++"exts"]@i1 := m[x++"s"]@i2;
                      m[x++"extm"]@i1 := m[x++"m"]@i2;
                      assert(m[x++"extm"] = m[x++"k"] + (m["delta"] * m[x++"exts"]))@i1 ;
                      m[x]@i1 := (m[x++"exts"] + m[x++"s"])@i2
                    }
                
                    _sum(z, x, y,i1,i2) {
                        m[z++"s"]@i2 := (m[x++"s"] + m[y++"s"])@i2;
                        m[z++"m"]@i2 := (m[x++"m"] + m[y++"m"])@i2;
                        m[z++"k"]@i1 := (m[x++"k"] + m[y++"k"])@i1
                    }
                
                    sum(z,x,y) { _sum(z,x,y,1,2); _sum(z,x,y,2,1) }
                
                    open(x) { _open(x,1,2); _open(x,2,1) }
                
                    _mult1(z, x, y) {
                        m[z++"s"]@1 :=
                          (m[z++"bs"] * m[z++"d"] + m[z++"as"] * m[z++"e"] + m[z++"cs"])@1;
                        m[z++"m"]@1 :=
                          (m[z++"bm"] * m[z++"d"] + m[z++"am"] * m[z++"e"] + m[z++"cm"])@1;
                        m[z++"k"]@2 :=
                          (m[z++"bk"] * m[z++"d"] + m[z++"ak"] * m[z++"e"] + m[z++"ck"])@2   \s
                    }
                
                    mult(z,x,y) {
                        sum(z++"a", x, z++"d");
                        open(z++"d");
                        sum(z++"b", y, z++"e");
                        open(z++"e");\s
                        _mult1(z,x,y); _mult2(z,x,y)
                    }
                """);

        System.out.println(program);
        assertNotNull(program);
    }
}
