package plaid.antlr;

import plaid.ast.PreludeCommand;

public class PreludeCommandVisitorTest {

    private PreludeCommand ast(String src) {
        return PreludeLoader.toCommand(src);
    }

}
