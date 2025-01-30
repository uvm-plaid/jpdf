package plaid;

import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import plaid.antlr.Loader;
import plaid.ast.PreludeCommand;
import plaid.ast.Program;
import plaid.cvc.TermFactory;
import plaid.cvc.Verifier;
import plaid.eval.OvertureChecker;
import plaid.eval.ProgramEvaluator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.stream.Stream;

import static plaid.cvc.CvcUtils.mkFiniteFieldSort;

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
        TermManager termManager = new TermManager();
        Sort sort = mkFiniteFieldSort(termManager, fieldSize, 10);
        TermFactory termFactory = new TermFactory(termManager, sort);
        Verifier verifier = new Verifier(termFactory);

        // separate prelude source code and constraints
        String program = readSourceCode();
        Program programAST = Loader.toProgram(program);
        PreludeCommand protocol = new ProgramEvaluator(Loader.toProgram(program)).eval();
        Collection<Term> overtureTerms = termFactory.toTerms(protocol);
        
        Term preconditionTerm = programAST.precondition() == null? null : termFactory.constraintToTerm(programAST.precondition());
        Term postconditionTerm = programAST.postcondition() == null? null:  termFactory.constraintToTerm(programAST.postcondition());

        if (!OvertureChecker.checkOverture(protocol)) {
            System.out.println("Overture protocol is invalid");
            System.exit(1);
        }

        if (!verifier.satisfies(protocol)) {
            System.out.println("Protocol is not satisfiable");
            System.exit(1);
        }
        System.out.println("Protocol is satisfiable");

        Collection<Term> premises = Stream.concat(overtureTerms.stream(), preconditionTerm==null? Stream.of() : Stream.of(preconditionTerm)).toList();
        
        if (postconditionTerm!=null &&  !verifier.entails(premises, postconditionTerm)) {
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

}
