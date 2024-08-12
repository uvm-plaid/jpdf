package plaid;

import java.math.BigInteger;
import java.util.List;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import io.github.cvc5.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        // antlr demo
        //String prog = "p[\"foo\"] * 12 + m[\"bar\"]";
        String prog = "m[\"foo\"]@2 := (s[\"x\"] + -r[\"y\"])@1";
        //String prog = "m[\"foo\"]@2";
        ANTLRInputStream input = new ANTLRInputStream(prog);
        OvertureLexer lexer = new OvertureLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Print tokens before filtering
        //tokens.fill();
        for (Object tok : tokens.getTokens()){
            System.out.println(tok);
        }
        OvertureParser parser = new OvertureParser(tokens);
        parser.setBuildParseTree(false);
        System.out.println(parser.protocol());
        //ParseTree tree = parser.program();
        //System.out.println(tree.toStringTree(parser));

        // cvc5 demo
        // create a solver
        Solver solver = new Solver();

// 2-party addition in F7
        solver.resetAssertions();

        solver.setLogic("ALL");

        Sort f7 = solver.mkFiniteFieldSort("7", 10);

        Term m1 = solver.mkConst(f7, "m1");
        Term f11 = solver.mkConst(f7, "f11");
        Term x = solver.mkConst(f7, "x");
        Term m2 = solver.mkConst(f7, "m2");
        Term f21 = solver.mkConst(f7, "f21");
        Term y = solver.mkConst(f7, "y");
        Term p1 = solver.mkConst(f7, "p1");
        Term p2 = solver.mkConst(f7, "p2");
        Term o = solver.mkConst(f7, "o");

// constraints from the protocol
        Term c1 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.FINITE_FIELD_ADD, x, (solver.mkTerm(Kind.FINITE_FIELD_MULT, f11, (solver.mkFiniteFieldElem("-1", f7, 10))))), m2);
        Term c2 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.FINITE_FIELD_ADD, y, (solver.mkTerm(Kind.FINITE_FIELD_MULT, f21, (solver.mkFiniteFieldElem("-1", f7, 10))))), m1);
        Term c3 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.FINITE_FIELD_ADD, m1, f11), p1);
        Term c4 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.FINITE_FIELD_ADD, m2, f21), p2);
        Term c5 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.FINITE_FIELD_ADD, p1, p2), o);

        solver.assertFormula(c1);
        solver.assertFormula(c2);
        solver.assertFormula(c3);
        solver.assertFormula(c4);
        solver.assertFormula(c5);


// sanity check (should be sat)
        Result r_sat = solver.checkSat();
        System.out.println(r_sat);


// correctness (should be unsat)
        Result r_unsat = solver.checkSatAssuming(solver.mkTerm(Kind.DISTINCT, solver.mkTerm(Kind.FINITE_FIELD_ADD, x, y), o));
        System.out.println(r_unsat);


// 3-party addition in F7
        solver.resetAssertions();

        Term m21 = solver.mkConst(f7, "m21");
        Term m31 = solver.mkConst(f7, "m31");
        Term s1 = solver.mkConst(f7, "s1");
        Term rl1 = solver.mkConst(f7, "rl1");
        Term r1 = solver.mkConst(f7, "r1");
        Term m12 = solver.mkConst(f7, "m12");
        Term m32 = solver.mkConst(f7, "m32");
        Term s2 = solver.mkConst(f7, "s2");
        Term rl2 = solver.mkConst(f7, "rl2");
        Term r2 = solver.mkConst(f7, "r2");
        Term m13 = solver.mkConst(f7, "m13");
        Term m23 = solver.mkConst(f7, "m23");
        Term s3 = solver.mkConst(f7, "s3");
        Term rl3 = solver.mkConst(f7, "rl3");
        Term r3 = solver.mkConst(f7, "r3");
        Term p1_3 = solver.mkConst(f7, "p1");
        Term p2_3 = solver.mkConst(f7, "p2");
        Term p3_3 = solver.mkConst(f7, "p3");
        Term o_3 = solver.mkConst(f7, "o");

/*

s.add(p1 == rl1 + m21 + m31)
s.add(p2 == rl2 + m12 + m32)
s.add(p3 == rl3 + m13 + m23)
s.add(o == p1 + p2 + p3)
 */

// constraints from the protocol
        Term c31 = solver.mkTerm(Kind.EQUAL, // (s1 - rl1) - r1
                solver.mkTerm(Kind.FINITE_FIELD_ADD,
                        solver.mkTerm(Kind.FINITE_FIELD_ADD, s1, solver.mkTerm(Kind.FINITE_FIELD_MULT, rl1, solver.mkFiniteFieldElem("-1", f7, 10))),
                        solver.mkTerm(Kind.FINITE_FIELD_MULT, r1, solver.mkFiniteFieldElem("-1", f7, 10))),
                m12); //
        Term c32 = solver.mkTerm(Kind.EQUAL, m13, r1);

        Term c33 = solver.mkTerm(Kind.EQUAL,
                solver.mkTerm(Kind.FINITE_FIELD_ADD,
                        solver.mkTerm(Kind.FINITE_FIELD_ADD, s2, solver.mkTerm(Kind.FINITE_FIELD_MULT, rl2, solver.mkFiniteFieldElem("-1", f7, 10))),
                        solver.mkTerm(Kind.FINITE_FIELD_MULT, r2, solver.mkFiniteFieldElem("-1", f7, 10))),
                m21); //

        Term c34 = solver.mkTerm(Kind.EQUAL, m23, r2);

        Term c35 = solver.mkTerm(Kind.EQUAL,
                solver.mkTerm(Kind.FINITE_FIELD_ADD,
                        solver.mkTerm(Kind.FINITE_FIELD_ADD, s3, solver.mkTerm(Kind.FINITE_FIELD_MULT, rl3, solver.mkFiniteFieldElem("-1", f7, 10))),
                        solver.mkTerm(Kind.FINITE_FIELD_MULT, r3, solver.mkFiniteFieldElem("-1", f7, 10))),
                m31); //
        Term c36 = solver.mkTerm(Kind.EQUAL, m32, r3);
        Term c37 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.FINITE_FIELD_ADD, solver.mkTerm(Kind.FINITE_FIELD_ADD, rl1, m21), m31), p1_3);
        Term c38 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.FINITE_FIELD_ADD, solver.mkTerm(Kind.FINITE_FIELD_ADD, rl2, m12), m32), p2_3);
        Term c39 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.FINITE_FIELD_ADD, solver.mkTerm(Kind.FINITE_FIELD_ADD, rl3, m13), m23), p3_3);
        Term c30 = solver.mkTerm(Kind.EQUAL, solver.mkTerm(Kind.FINITE_FIELD_ADD, solver.mkTerm(Kind.FINITE_FIELD_ADD, p1_3, p2_3), p3_3), o_3);

// sanity check (should be sat)
        solver.assertFormula(c31);
        solver.assertFormula(c32);
        solver.assertFormula(c33);
        solver.assertFormula(c34);
        solver.assertFormula(c35);
        solver.assertFormula(c36);
        solver.assertFormula(c37);
        solver.assertFormula(c38);
        solver.assertFormula(c39);
        solver.assertFormula(c30);

        r_sat = solver.checkSat();
        System.out.println(r_sat);

// correctness (should be unsat)
        r_unsat = solver.checkSatAssuming(solver.mkTerm(Kind.DISTINCT, solver.mkTerm(Kind.FINITE_FIELD_ADD, solver.mkTerm(Kind.FINITE_FIELD_ADD, s1, s2), s3), o_3));
        System.out.println(r_unsat);

// BDOZ/SPDZ scheme
        solver.resetAssertions();

        Term mac1 = solver.mkConst(f7, "mac1");
        Term mac2 = solver.mkConst(f7, "mac2");
        m1 = solver.mkConst(f7, "m1");
        m2 = solver.mkConst(f7, "m2");
        Term k1 = solver.mkConst(f7, "k1");
        Term k2 = solver.mkConst(f7, "k2");
        Term d = solver.mkConst(f7, "d");
        Term a = solver.mkConst(f7, "a");

      /*
      s.add(mac1 == k1 + (d * m1))
s.add(mac2 == k2 + (d * m2))
       */

        Term c41 = solver.mkTerm(Kind.EQUAL, mac1, solver.mkTerm(Kind.FINITE_FIELD_ADD, k1, solver.mkTerm(Kind.FINITE_FIELD_MULT, d, m1)));
        Term c42 = solver.mkTerm(Kind.EQUAL, mac2, solver.mkTerm(Kind.FINITE_FIELD_ADD, k2, solver.mkTerm(Kind.FINITE_FIELD_MULT, d, m2)));

        solver.assertFormula(c41);
        solver.assertFormula(c42);

// HE for sum of shares (should be unsat)
        r_unsat = solver.checkSatAssuming(solver.mkTerm(Kind.DISTINCT,
                solver.mkTerm(Kind.FINITE_FIELD_ADD, mac1, mac2),
                solver.mkTerm(Kind.FINITE_FIELD_ADD,
                        solver.mkTerm(Kind.FINITE_FIELD_ADD, k1, k2),
                        solver.mkTerm(Kind.FINITE_FIELD_MULT, d, solver.mkTerm(Kind.FINITE_FIELD_ADD, m1, m2)))));

        System.out.println(r_unsat);

// HE for multiplication of share and public value a (should be unsat)
        r_unsat = solver.checkSatAssuming(solver.mkTerm(Kind.DISTINCT,
                solver.mkTerm(Kind.FINITE_FIELD_MULT, mac1, a),
                solver.mkTerm(Kind.FINITE_FIELD_ADD,
                        solver.mkTerm(Kind.FINITE_FIELD_MULT, k1, a),
                        solver.mkTerm(Kind.FINITE_FIELD_MULT, d, solver.mkTerm(Kind.FINITE_FIELD_MULT, m1, a)))));

        System.out.println(r_unsat);

    }
}
