package plaid.logic;

import io.github.cvc5.CVC5ApiException;
import org.junit.Ignore;
import org.junit.Test;
import plaid.antlr.Loader;
import plaid.ast.*;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ConstraintAnalyzerTest {

    private Constraints inferPrePostCmd(String src, String program) throws CVC5ApiException {
        Program ast = Loader.toProgram(program);
        ConstraintAnalyzer constraintAnalyzer = new ConstraintAnalyzer(ast, "2");
        return constraintAnalyzer.inferPrePostCmd(Loader.toCommand(src), new ConstraintEvaluator(ast));
    }

    private Constraints inferPrePostFN(String fName, String src) throws CVC5ApiException {
        Program program = Loader.toProgram(src);
        ConstraintAnalyzer constraintAnalyzer = new ConstraintAnalyzer(program, "2");
        return constraintAnalyzer.inferPrePostFN(program.resolveCommandFunction(Loader.toExpression(fName)));
    }

    /**
     * infer precondition/postcondition for message assignment
     */
    @Test
    public void inferAssignment() throws CVC5ApiException {
        String src = "m[x]@1 := (s[x]+r[x])@2";
        Constraints expected = new Constraints(
                new TrueConstraint(),
                Loader.toConstraintExpression("m[x]@1 == s[x]@2 + r[x]@2"));
        // element-wise equality check
        assertEquals(expected.getPre(),  inferPrePostCmd(src, "").getPre());
        assertEquals(expected.getPost(),  inferPrePostCmd(src, "").getPost());

    }


    /**
     * infer preconditon for assert
     */
    @Test
    public void inferAssert() throws CVC5ApiException {
        String src = "assert (m[x] = m[y])@1";

        Constraints expected = new Constraints(Loader.toConstraintExpression("m[x]@1 == m[y]@1"), new TrueConstraint());

        assertEquals(expected.getPre(), inferPrePostCmd(src, "").getPre());
        assertEquals(expected.getPost(), inferPrePostCmd(src, "").getPost());
    }

    /**
     * infer precondition for let
     */
    @Test
    public void inferLet() throws CVC5ApiException {
        String src = "let i = 1 in m[x]@i := m[x]@2";
        Constraints expected = new Constraints(
                new TrueConstraint(),
                Loader.toConstraintExpression("m[x]@1 == m[x]@2"));
        assertEquals(expected.getPre(), inferPrePostCmd(src, "").getPre());
        assertEquals(expected.getPost(), inferPrePostCmd(src, "").getPost());
    }


    /**
     * infer preconditon for commandList
     */
    @Test
    public void inferCommandList() throws CVC5ApiException {
        String src = "m[x]@1 := m[x]@2; assert(m[y]=m[x])@i";
        Constraints expected = new Constraints(
                Loader.toConstraintExpression("T AND m[y]@i==m[x]@i"),
                Loader.toConstraintExpression("m[x]@1 == m[x]@2 AND T"));
        assertEquals(expected.getPre(), inferPrePostCmd(src, "").getPre());
        assertEquals(expected.getPost(), inferPrePostCmd(src, "").getPost());

    }


    /**
     * infer precondition and postcondition for function call
     */
    @Test
    public void inferFunctionCall() throws CVC5ApiException {
        String program = """
                cmdfunctions:
                f(i:cid) {assert (m["x"] = m["x"])@i}

                main(){f(1)}
                """;

        // when the precondition for a function is not provided
        // what is  precondition of f(1)?
        Constraints expected = new Constraints(
                Loader.toConstraintExpression("m[\"x\"]@1 == m[\"x\"]@1"),
                new TrueConstraint());

        Constraints actual = inferPrePostCmd("f(1)", program);
        assertEquals(expected.getPre(), actual.getPre());
        assertEquals(expected.getPost(), actual.getPost());

    }

    /**
     * infer precondition and postcondition for function call
     * when the function called does have constraints
     */
    @Test
    @Ignore
    public void inferFunctionCallWithAnnotations() throws CVC5ApiException {
        String program = """
                cmdfunctions:
                precondition: ( m[y]@1 == 2 AND m["z"]@1 == 3)
                h(y : string) {
                    m["x"]@1 := (m[y] * m["z"])@1;
                    assert(m["x"] = m["z"] + m["z"])@1
                    }
                """;

        // perform hardpack rule on given and inferred constraints
        // and use the hardpacked constraints to get the precondition and postcondition for the application
        Constraints expected = new Constraints(
                Loader.toConstraintExpression("m[\"y\"]@1 == 2 AND m[\"z\"]@1 == 3"),
                null
        );

        Constraints actual = inferPrePostCmd("m[\"y\"]@1 := 2; m[\"z\"]@1 := 3; h(\"y\")", program);

        assertEquals(expected.getPre(), actual.getPre());
        assertEquals(expected.getPost(), actual.getPost());

    }

    /**
     * apply FN rule to derive precondition of a function, with no precondition and postcondition given
     */
    @Test
    public void inferFunction() throws CVC5ApiException {
        String program = """
                cmdfunctions:
                f(i:cid) {assert (m["x"] = m["x"])@i}

                main(){f(1); let x = "foo" in m[x]@2 := m[x]@2}
                """;

        Constraints expected = new Constraints(
                Loader.toConstraintExpression("m[\"x\"]@1 == m[\"x\"]@1 AND T"),
                Loader.toConstraintExpression("T AND m[\"foo\"]@2 == m[\"foo\"]@2")
        );

        Constraints actual = inferPrePostFN("main", program);
        assertEquals(expected.getPre(), actual.getPre());
        assertEquals(expected.getPost(), actual.getPost());

    }



    /**
     * apply FN rule to a function with precondiiton and postcondition
     */
    @Test
    @Ignore
    public void inferFunctionWithAnnotations() throws CVC5ApiException{
        String program = """
                cmdfunctions:
                g() {
                    m["x"]@1 := (m["y"] * m["z"])@1;
                    assert(m["x"] = m["z"] + m["z"])@1
                }
                
                precondition: ( m["y"]@1 == 2 AND m["z"]@1 == 3)
                h() { g() }
                """;

        Constraints expected = new Constraints(
                Loader.toConstraintExpression("m[\"y\"]@1 == 2 AND m[\"z\"]@1 == 3"),
                null
        );

        Constraints actual = inferPrePostFN("h", program);
        assertEquals(expected.getPre(), actual.getPre());
        assertEquals(expected.getPost(), actual.getPost());
    }

    /**
     * throws error when hardpack does not hold
     */
    @Test (expected =  RuntimeException.class)
    public void wrongConstraintAnnotation() throws CVC5ApiException {
        String program = """
                cmdfunctions:
                precondition: (T)
                g() {
                    m["x"]@1 := (m["y"] * m["z"])@1;
                    assert(m["x"] = m["z"] + m["z"])@1
                }
                postcondition: (T)

                """;

        inferPrePostFN("g", program);

    }

    /**
     * if I call a function by passing concatenation expression as an argument, 
     * does the algorithm return constraints with substitution of ?
     */
    @Test
    public void concatArg() throws CVC5ApiException{
        String program = """
                cmdfunctions:
                f(x:string) {m[x++"foo"]@1 := m[x++"foo"]@1}
                main(){ f(x++"s") }
                """;

        Constraints expected = new Constraints(
                new TrueConstraint(),
                Loader.toConstraintExpression("m[x++\"sfoo\"]@1 == m[x++\"sfoo\"]@1")
        );
        
        assertEquals(expected.getPre(), inferPrePostFN("main", program).getPre());
        assertEquals(expected.getPost(), inferPrePostFN("main", program).getPost());
    }
    

    @Test
    public void concatLiterals() {
        Program program = new Program(List.of(), List.of(), List.of());
        ConstraintEvaluator evaluator = new ConstraintEvaluator(program);
        Expr input = new ConcatExpr(new Str("a"), new Str("b"));
        Object actual = evaluator.toOverture(input);
        Object expected = new Str("ab");
        assertEquals(expected, actual);
    }

    /**
     * ("a" ++ "b") ++ ("c" ++ "d")
     */
    @Test
    public void concatGroupedLiterals() {
        Program program = new Program(List.of(), List.of(), List.of());
        ConstraintEvaluator evaluator = new ConstraintEvaluator(program);
        Expr group1 = new ConcatExpr(new Str("a"), new Str("b"));
        Expr group2 = new ConcatExpr(new Str("c"), new Str("d"));
        Expr input = new ConcatExpr(group1, group2);
        Object actual = evaluator.toOverture(input);
        Object expected = new Str("abcd");
        assertEquals(expected, actual);
    }

}
