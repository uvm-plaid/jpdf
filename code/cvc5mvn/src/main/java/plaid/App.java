package plaid;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;
import org.antlr.v4.runtime.*;
import plaid.antlr.ConstraintsLoader;
import plaid.antlr.Loader;
import plaid.ast.PreludeCommand;
import plaid.ast.Program;
import plaid.constraints.ast.Constraints;
import plaid.cvc.Verifier;
import plaid.eval.OvertureChecker;
import plaid.eval.ProgramEvaluator;


public class App {
    private static final String order = "2";

    public static void main(String[] args) throws Exception {
        // separate prelude source code and constraints
        File file = new File(new Scanner(System.in).nextLine());
        String content = Files.readString(file.toPath());
        String[] parts = content.split("constraints:");
        String program = parts[0];
        String constraints =  "constraints:" + parts[1];

        // evaluate prelude program to Overture protocol
        PreludeCommand protocol = evaluates(program);
        // check if the protocol is valid
        if(OvertureChecker.checkOverture(protocol)){
            // convert constraints into ast
            Constraints proposition = ConstraintsLoader.toConstraint(constraints);
            System.out.println(Verifier.verifies(protocol, proposition));
        }
        else{
            throw new IllegalArgumentException("Not a valid overture protocol");
        }

    } // main()


    /**
     * evaluate a program source code to Overture
     * @param program in String
     * @return Overture protocol in PreludeCommand
     */
    private static PreludeCommand evaluates(String program){
        return evaluates(Loader.toProgram(program));
    }

    /**
     * evaluate a program source code to Overture
     * @param program in Program
     * @return Overture protocol in PreludeCommand
     */
    private static PreludeCommand evaluates(Program program){
        ProgramEvaluator evaluator = new ProgramEvaluator(program);
        return evaluator.eval();
    }

    /**
     *
     * @param program allow arbitrary Prelude source code files to be input
     * @return a parse tree
     * @throws Exception
     */
    public static String preludeParseTree(String program) throws Exception{
        ANTLRInputStream input = new ANTLRInputStream(program);
        PreludeLexer lexer = new PreludeLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PreludeParser parser = new PreludeParser(tokens);
        parser.setBuildParseTree(true);
        PreludeParser.ProgramContext pc = parser.program();

        System.out.println(pc.toStringTree(parser)); // show AST in console
        //List<String> ruleNameList = Arrays.asList(parser.getRuleNames());
        //String prettyTree = TreeUtils.toPrettyTree(pc, ruleNameList);

        return pc.toStringTree(parser);
    } // preludeParseTree()








}
