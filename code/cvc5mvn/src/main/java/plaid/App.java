package plaid;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.gui.TreeViewer;
import io.github.cvc5.*;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class App 
{
    public static void main( String[] args ) throws Exception {
        String path = args[0];
        // prelude demo
        File file = new File("/home/yyeh/jpdf/code/cvc5mvn/src/main/java/plaid/confidentiality_example.txt");
        InputStream inputStream = new FileInputStream(file);
        PrintStream outputStream = new PrintStream("parse tree for " + file.getName());
        outputStream.println(preludeParseTree(inputStream));
        inputStream.close();
        outputStream.close();


    } // main()

    // allow arbitrary Prelude source code files to be input
    // return a parse tree
    public static String preludeParseTree(InputStream in) throws Exception{
        // prelude demo
        String program = new String(in.readAllBytes());
        ANTLRInputStream input = new ANTLRInputStream(program);
        PreludeLexer lexer = new PreludeLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PreludeParser parser = new PreludeParser(tokens);
        parser.setBuildParseTree(true);
        PreludeParser.ProgramContext pc = parser.program();

        System.out.println(pc.toStringTree(parser)); // show AST in console
        //List<String> ruleNameList = Arrays.asList(parser.getRuleNames());
        //String prettyTree = TreeUtils.toPrettyTree(pc, ruleNameList);

//        //show AST in GUI
//        JFrame frame = new JFrame("Antlr AST");
//        JPanel panel = new JPanel();
//        TreeViewer viewer = new TreeViewer(Arrays.asList(
//                parser.getRuleNames()),tree);
//        viewer.setScale(1.5); // Scale a little
//        panel.add(viewer);
//        frame.add(panel);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
        return pc.toStringTree(parser);
    } // preludeParseTree()

    // return memories in an overture program
    public static Map<String, Term> createLookup(Iterable<Memory> memories){
        HashMap<String, Term> results = new HashMap<>();

        // iterate over memories
        for(Memory memory : memories){
            results.put(memory.getName(), memory.getTerm());
        }

        return results;
    }

    // antlr&cvc5 demo
    public static void overtureToCVC5() throws Exception{
//        TermManager termManager = new TermManager();
//        Solver solver = new Solver(termManager);
//        solver.resetAssertions();
//        solver.setLogic("ALL");
//        Sort f7 = termManager.mkFiniteFieldSort("7", 10);
//
//
//        // 3-party addition
//        String protocol = """
//                m["s1"]@2 := ((s["1"] + -r["local"]) + -r["x"])@1;
//                m["s1"]@3 := r["x"]@1;
//                m["s2"]@1 := ((s["2"] + -r["local"]) + -r["x"])@2;
//                m["s2"]@3 := r["x"]@2;
//                m["s3"]@1 := ((s["3"] + -r["local"]) + -r["x"])@3;
//                m["s3"]@2 := r["x"]@3;
//                p["1"] := ((r["local"] + m["s2"]) + m["s3"])@1;
//                p["2"] := ((m["s1"] + r["local"]) + m["s3"])@2;
//                p["3"] := ((m["s1"] + m["s2"]) + r["local"])@3;
//                out@1 := ((p["1"] + p["2"]) + p["3"])@1;
//                out@2 := ((p["1"] + p["2"]) + p["3"])@2;
//                out@3 := ((p["1"] + p["2"])+ p["3"])@3
//
//                """;
//
//        Iterable<Memory> memoryList = SolverExtension.addOvertureProtocolConst(solver, f7, protocol);
//        // sanity check (should be sat)
//        System.out.println(solver.checkSat());
//
//        // correctness (should be unsat)
//        Map<String, Term> lookup = createLookup(memoryList);
//
//        Result r_unsat = solver.checkSatAssuming(termManager.mkTerm(Kind.DISTINCT,
//                termManager.mkTerm(Kind.FINITE_FIELD_ADD,
//                        termManager.mkTerm(Kind.FINITE_FIELD_ADD, lookup.get("s_1_1"), lookup.get("s_2_2")),
//                        lookup.get("s_3_3")),
//                lookup.get("out_3")));
//
//        System.out.println(r_unsat);
    }


        /*
        //2-party addition
        String protocol = """
                m["s"]@2 := (s["x"] + -r["1"])@1;
                m["s"]@1 := (s["y"] + -r["2"])@2;
                p["1"] := (m["s"] + r["1"])@1;
                p["2"] := (m["s"] + r["2"])@2;
                out@1 := (p["1"] + p["2"])@1;
                out@2 := (p["1"] + p["2"])@2
                """;

        Iterable<Memory> memoryList = SolverExtension.addOvertureProtocolConst(solver, f7, protocol);
        // sanity check (should be sat)
        System.out.println(solver.checkSat());

        // correctness (should be unsat)
        Map<String, Term> lookup = createLookup(memoryList);

        Result r_unsat = solver.checkSatAssuming(termManager.mkTerm(Kind.DISTINCT,
                termManager.mkTerm(Kind.FINITE_FIELD_ADD, lookup.get("s_x_1"), lookup.get("s_y_2"))
                , lookup.get("out_1")));
        System.out.println(r_unsat);
        */
        



        /*
        //String prog = "p[\"foo\"] * 12 + m[\"bar\"]";
        String prog = "m[\"foo\"]@2 := (s[\"x\"] + -r[\"y\"])@1";
        ANTLRInputStream input = new ANTLRInputStream(prog);
        OvertureLexer lexer = new OvertureLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Print tokens before filtering
        //tokens.fill();
        //for (Object tok : tokens.getTokens()){
        //    System.out.println(tok);
        //}
        OvertureParser parser = new OvertureParser(tokens);
        parser.setBuildParseTree(true);
        OvertureParser.ProtocolContext pc = parser.protocol();
       // OvertureParser.ExpressionContext pc = parser.expression();
        //ParseTree tree = parser.protocol();
        System.out.println(pc.toStringTree(parser));

        // Overture to Constraints
        TermManager termManager = new TermManager();
        Solver solver = new Solver(termManager);
        Sort f7 = solver.mkFiniteFieldSort("7", 10);
        OvertureConstListener listener = new OvertureConstListener(termManager, f7);
        new ParseTreeWalker().walk(listener, pc);
        Iterable<Memory> memoryList = listener.memories();

        OvertureConstraintListener overtureConstraintListener = new OvertureConstraintListener(termManager, f7, memoryList);
        Term result = overtureConstraintListener.visit(pc);
        System.out.println(result);


        // old cvc5 demo
        // create a solver
        //TermManager termManager = new TermManager();
        //Solver solver = new Solver(termManager);

        // 2-party addition in F7
        solver.resetAssertions();
        //Sort f7 = solver.mkFiniteFieldSort("7", 10);

        Term m1 = termManager.mkConst(f7, "m1");
        Term f11 = termManager.mkConst(f7, "f11");
        Term x = termManager.mkConst(f7, "x");
        Term m2 = termManager.mkConst(f7, "m2");
        Term f21 = termManager.mkConst(f7, "f21");
        Term y = termManager.mkConst(f7, "y");
        Term p1 = termManager.mkConst(f7, "p1");
        Term p2 = termManager.mkConst(f7, "p2");
        Term o = termManager.mkConst(f7, "o");

// constraints from the protocol
        Term c1 = termManager.mkTerm(Kind.EQUAL, termManager.mkTerm(Kind.FINITE_FIELD_ADD, x, (termManager.mkTerm(Kind.FINITE_FIELD_MULT, f11, (solver.mkFiniteFieldElem("-1", f7, 10))))), m2);
        Term c2 = termManager.mkTerm(Kind.EQUAL, termManager.mkTerm(Kind.FINITE_FIELD_ADD, y, (termManager.mkTerm(Kind.FINITE_FIELD_MULT, f21, (solver.mkFiniteFieldElem("-1", f7, 10))))), m1);
        Term c3 = termManager.mkTerm(Kind.EQUAL, termManager.mkTerm(Kind.FINITE_FIELD_ADD, m1, f11), p1);
        Term c4 = termManager.mkTerm(Kind.EQUAL, termManager.mkTerm(Kind.FINITE_FIELD_ADD, m2, f21), p2);
        Term c5 = termManager.mkTerm(Kind.EQUAL, termManager.mkTerm(Kind.FINITE_FIELD_ADD, p1, p2), o);

        solver.assertFormula(c1);
        solver.assertFormula(c2);
        solver.assertFormula(c3);
        solver.assertFormula(c4);
        solver.assertFormula(c5);


// sanity check (should be sat)
        Result r_sat = solver.checkSat();
        System.out.println(r_sat);


// correctness (should be unsat)
        r_unsat = solver.checkSatAssuming(termManager.mkTerm(Kind.DISTINCT, termManager.mkTerm(Kind.FINITE_FIELD_ADD, x, y), o));
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

// constraints from the protocol
        Term c31 = solver.mkTerm(Kind.EQUAL, // m12 = (s1 - rl1) - r1
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
        */



}
