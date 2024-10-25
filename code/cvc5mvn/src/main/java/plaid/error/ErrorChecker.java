package plaid.error;

import plaid.ast.Node;

import java.util.List;

public class ErrorChecker {

    private final List<Check> checks;

    public ErrorChecker(List<Check> checks) {
        this.checks = checks;
    }

    public void check(Node ast) {
        for (Check c : checks) {
            c.check(ast);
        }
    }

}
