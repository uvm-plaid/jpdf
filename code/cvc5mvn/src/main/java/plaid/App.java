package plaid;

import io.github.cvc5.CVC5ApiException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import plaid.antlr.Loader;
import plaid.ast.Program;
import plaid.logic.ConstraintAnalyzer;
import plaid.logic.Constraints;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
    
    // static analysis on main
    private Constraints staticAnalysis(Program program) throws CVC5ApiException {
        ConstraintAnalyzer constraintAnalyzer = new ConstraintAnalyzer(program, fieldSize);
        return constraintAnalyzer.inferPrePostFN(program.resolveCommandFunction(Loader.toExpression("main")));
    }
    
    @Override
    public void run() {
        // read prelude source code and constraints
        String program = readSourceCode();
        Program programAST = Loader.toProgram(program);
        
        // static analysis 
        try {
            Constraints constraints = staticAnalysis(programAST);
            System.out.println("The precondition for main: " +  ScalaFunctions.prettyPrint(constraints.getPre()));
            System.out.println("The postcondition for main: " + ScalaFunctions.prettyPrint(constraints.getPost()));

        } catch (CVC5ApiException e) {
            throw new RuntimeException(e);
        }

    }

    private String readSourceCode() {
        File file = new File(path);
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
