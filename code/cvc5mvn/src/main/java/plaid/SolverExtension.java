package plaid;
import io.github.cvc5.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;


public class SolverExtension {
//    public static Iterable<Memory> addOvertureProtocolConst(Solver solver, Sort prime, String protocol) throws CVC5ApiException{
//        //create a parse tree with protocol
//        ANTLRInputStream input = new ANTLRInputStream(protocol);
//        OvertureLexer lexer = new OvertureLexer(input);
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        OvertureParser parser = new OvertureParser(tokens);
//        parser.setBuildParseTree(true);
//        OvertureParser.ProtocolContext tree = parser.protocol();
//
//        //create memories from there
//        TermManager termManager = solver.getTermManager();
//        OvertureConstListener listener = new OvertureConstListener(termManager, prime);
//        new ParseTreeWalker().walk(listener, tree);
//        Iterable<Memory> memoryList = listener.memories();
//
//        //System.out.println(memoryList);
//        OvertureConstraintListener overtureConstraintListener = new OvertureConstraintListener(termManager, prime, memoryList);
//
//        // iterate over commands in the parse tree
//        for (OvertureParser.CommandContext commandContext : tree.command()){
//           // System.out.println(overtureConstraintListener.visit(commandContext));
//            solver.assertFormula(overtureConstraintListener.visit(commandContext));
//
//        }
//            // for each command, create a constraint (constraint visitor)
//
//        return memoryList;
//
//
//    }
}
