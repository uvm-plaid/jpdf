package plaid;

import io.github.cvc5.Term;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import plaid.antlr.ConstraintsLoader;
import plaid.antlr.Loader;
import plaid.ast.PreludeCommand;
import plaid.ast.Program;
import plaid.constraints.ast.Constraints;
import plaid.cvc.TermFactory;
import plaid.cvc.Verifier;
import plaid.eval.OvertureChecker;
import plaid.eval.ProgramEvaluator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.stream.Stream;

@Command(name="prelude", version="prelude-dev", mixinStandardHelpOptions=true)
public class App implements Runnable {

    @Option(names={"--field-size", "-s"}, defaultValue="2", description="Order of the finite field")
    String fieldSize;

    @Parameters(paramLabel="<path>", description="Path to a prelude source file")
    String path;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        // separate prelude source code and constraints
        String content = readSourceCode();
        String[] precondParts = content.split("precondition:");
        String[] postcondParts = content.split("postcondition:");
        String[] allParts = content.split("precondition:|postcondition:");
        String program = allParts[0];
        TermFactory tf = Verifier.getTermFactory();

        PreludeCommand protocol = evaluates(program);
        Collection<Term> overtureTerms = tf.toTerms(protocol);

        String preconditionSource = precondParts.length > 1 ? allParts[1] : "";
        Constraints preconditionAst = ConstraintsLoader.toConstraint("constraints:" + preconditionSource);
        Collection<Term> preconditionTerms = tf.constraintsToTerms(preconditionAst);

        String postconditionSource = postcondParts.length > 1 ? postcondParts[1] : "";
        Constraints postconditionAst = ConstraintsLoader.toConstraint("constraints:" + postconditionSource);
        Collection<Term> postconditionTerms = tf.constraintsToTerms(postconditionAst);

        if (!OvertureChecker.checkOverture(protocol)) {
            System.out.println("Overture protocol is invalid");
            System.exit(1);
        }

        if (!Verifier.satisfies(protocol)) {
            System.out.println("Protocol is not satisfiable");
            System.exit(1);
        }
        System.out.println("Protocol is satisfiable");

        Collection<Term> premises = Stream.concat(overtureTerms.stream(), preconditionTerms.stream()).toList();
        if (!Verifier.entails(premises, postconditionTerms)) {
            System.out.println("Protocol and precondition do not entail postcondition");
            System.exit(1);
        }
        System.out.println("Protocol and precondition entail postcondition");
    }

    private String readSourceCode() {
        File file = new File(path);
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
