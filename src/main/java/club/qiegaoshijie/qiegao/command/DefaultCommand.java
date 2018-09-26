package club.qiegaoshijie.qiegao.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DefaultCommand
{
    private CommandSender sender;
    private Command command;
    private String label;
    private String[] args;

    public DefaultCommand(CommandSender sender, Command command, String label, String[] args)
    {
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args;
    }

    public CommandSender getSender()
    {
        return this.sender;
    }

    public Command getCommand()
    {
        return this.command;
    }

    public String getLabel()
    {
        return this.label;
    }

    public String[] getArgs()
    {
        return this.args;
    }
}