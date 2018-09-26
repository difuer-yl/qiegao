package club.qiegaoshijie.qiegao.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import club.qiegaoshijie.qiegao.command.annotations.Cmd;
import club.qiegaoshijie.qiegao.command.annotations.Command;
import org.bukkit.command.CommandSender;

public class CommandHelp
{
    List<Command> helpList = new ArrayList();

    public CommandHelp()
    {

        Method[] methods = Commands.class.getMethods();

        for (Method method : methods)
        {
            Cmd cmd = (Cmd)method.getAnnotation(Cmd.class);
            Command help = (Command)method.getAnnotation(Command.class);
            if ((cmd != null) && (help != null)) {
                this.helpList.add(help);
            }
        }
    }

    public void send(CommandSender sender)
    {
        for (Command help : this.helpList) {
            sender.sendMessage("/qiegao " + help.possibleArguments() + " - " + help.value());
        }
    }
}
