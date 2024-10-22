package plaid.ast;

public interface Node {

    Iterable<Node> children();

    // pretty print
    String prettyPrint();
}
