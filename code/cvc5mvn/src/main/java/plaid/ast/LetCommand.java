package plaid.ast;

import java.util.List;

public record LetCommand(
        Identifier identifier,
        PreludeExpression expr,
        PreludeCommand command)
    implements PreludeCommand {

    @Override
    public Iterable<Node> children() {
        return List.of(identifier, expr, command);
    }

}
