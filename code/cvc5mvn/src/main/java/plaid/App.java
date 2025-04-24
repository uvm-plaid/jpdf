package plaid;

import io.github.cvc5.CVC5ApiException;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import io.github.cvc5.TermManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import plaid.antlr.Loader;
import plaid.ast.CommandFunction;
import plaid.ast.ConstraintExpr;
import plaid.ast.PreludeCommand;
import plaid.ast.Program;
import plaid.cvc.TermFactory;
import plaid.cvc.Verifier;
import plaid.eval.Evaluator;
import plaid.eval.ExpressionEvaluator;
import plaid.eval.OvertureChecker;
import plaid.eval.ProgramEvaluator;
import plaid.logic.ConstraintAnalyzer;
import plaid.logic.Constraints;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;

import static plaid.cvc.CvcUtils.mkFiniteFieldSort;

@Command(name="prelude", version="prelude-dev", mixinStandardHelpOptions=true)
public class App implements Runnable {

    private TermFactory termFactory;

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
//        TermManager termManager = new TermManager();
//        Sort sort = mkFiniteFieldSort(termManager, fieldSize, 10);
//        termFactory = new TermFactory(termManager, sort);
//        Verifier verifier = new Verifier(termFactory);
        
        // read prelude source code and constraints
        String program = readSourceCode();
        Program programAST = Loader.toProgram(program);
        
        
        // static analysis 
        try {
            Constraints constraints = staticAnalysis(programAST);
            System.out.println("The hardpacked precondition for main: " +  ScalaFunctions.prettyPrint(constraints.getPre())); 
            System.out.println("The hardpacked postcondition for main: " + ScalaFunctions.prettyPrint(constraints.getPost()));
        } catch (CVC5ApiException e) {
            throw new RuntimeException(e);
        }

//        PreludeCommand protocol = new ProgramEvaluator(Loader.toProgram(program)).eval(); // evaluation 
//        Term overtureTerms = termFactory.toTerm(protocol);
//        
//        Term preconditionTerm = evaluateConstraint(programAST, programAST.precondition());
//        Term postconditionTerm = evaluateConstraint(programAST, programAST.postcondition());
//
//        if (!OvertureChecker.checkOverture(protocol)) {
//            System.out.println("Overture protocol is invalid");
//            System.exit(1);
//        }
//
//        if (!verifier.satisfies(protocol)) {
//            System.out.println("Protocol is not satisfiable");
//            System.exit(1);
//        }
//        System.out.println("Protocol is satisfiable");
//        
//        Collection<Term> premises = new ArrayList<>();
//        premises.add(overtureTerms);
//        if (preconditionTerm != null) {
//            premises.add(preconditionTerm);
//        }
//        
//        if (postconditionTerm!=null &&  !verifier.entails(premises, postconditionTerm)) {
//            System.out.println("Protocol and precondition do not entail postcondition");
//            System.exit(1);
//        }
//        System.out.println("Protocol and precondition entail postcondition");
    }

    private String readSourceCode() {
        File file = new File(path);
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private Term evaluateConstraint(Program program, ConstraintExpr expr) {
//        if (expr == null) {
//            return null;
//        }
//        ConstraintExpr eval = new Evaluator(program).evalConstraint(expr);
//        return termFactory.constraintToTerm(eval);
//    }

}
