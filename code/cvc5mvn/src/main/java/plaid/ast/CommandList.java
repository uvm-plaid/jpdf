package plaid.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandList implements PreludeCommand{
    private final List<PreludeCommand> commands;

    public CommandList(List<PreludeCommand> commands){
        this.commands = commands;
    }

    public List<PreludeCommand> getCommands() {
        return commands;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandList that = (CommandList) o;
        return Objects.equals(commands, that.commands);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(commands);
    }

    @Override
    public Iterable<Node> children() {
        return new ArrayList<>(commands);
    }

    @Override
    public String toString() {
        return "CommandList{" +
                "commands=" + commands +
                '}';
    }

    @Override
    public String prettyPrint(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < commands.size(); i++){
            sb.append(commands.get(i).prettyPrint());
            if(i != commands.size()-1){
                sb.append("; ");
            }
        }
        return sb.toString();
    }
}
