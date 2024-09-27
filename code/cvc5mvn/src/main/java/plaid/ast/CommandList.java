package plaid.ast;

import java.util.List;

public class CommandList implements PreludeCommand{
    private final List<PreludeCommand> commands;

    public CommandList(List<PreludeCommand> commands){
        this.commands = commands;
    }

    public List<PreludeCommand> getCommands() {
        return commands;
    }
}
