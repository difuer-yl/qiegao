package club.qiegaoshijie.qiegao.command;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.command.annotations.Cmd;
import club.qiegaoshijie.qiegao.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandHandler  implements TabExecutor
{
    private String name;
    private PluginCommand pcmd;
    private CommandHelp help;

    public CommandHandler(String name)
    {
        this.name = name;
        this.help = new CommandHelp();
        this.pcmd = Qiegao.getInstance().getCommand(name);
        this.pcmd.setExecutor(this);
        this.pcmd.setTabCompleter(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if ((args.length == 0) || (args[0].equalsIgnoreCase("help")))
        {
            this.help.send(sender);
            return true;
        }
        return execute(new DefaultCommand(sender, command, label, args));
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){

//        sender.sendMessage(args);
        List<String> list = new ArrayList();
        if(args==null) return null;


        if ( (args.length == 1)) {
            list=getList(new DefaultCommand(sender, command, alias, args));
        }

        if(args[0].equalsIgnoreCase("tag")) {
            if(args.length==2){

                list= (List<String>) Qiegao.getMessages().getList("tag.area");
            }else if(args.length==3){
                List<String> area=(List<String>) Qiegao.getMessages().getList("tag.area");
                Log.toConsole(args[1]);
                if(!args[1].equalsIgnoreCase("糕宠")&&area.indexOf(args[1])!=-1){
                    list=(List<String>) Qiegao.getMessages().getList("tag.type");
                }
            }

        }
        if(args[0].equalsIgnoreCase("landmark")) {
            if(args.length==2){

                list= (List<String>) Qiegao.getMessages().getList("landmark.area");
            }else if(args.length==3){
                list.add("地标名称");
            }

        }


        return list;
    }

    public boolean execute(DefaultCommand defcmd)
    {
        if (check(defcmd))
        {
            Method method = getMethod(defcmd.getArgs()[0]);
            if (method != null)
            {
                try
                {
                    method.invoke(Commands.class.newInstance(), new Object[] { defcmd });
                }
                catch (Exception e)
                {
                    Bukkit.getLogger().warning("玩家 " + defcmd.getSender().getName() + " 执行命令 /" + this.name + " " + defcmd.getArgs()[0] + " 时出错： " + e.getMessage());
                }
                return true;
            }
        }
        return false;
    }

    public boolean check(DefaultCommand defcmd)
    {
        Method method = getMethod(defcmd.getArgs()[0]);
        if (method != null)
        {
            Cmd cmd = (Cmd)method.getAnnotation(Cmd.class);
            if (cmd != null)
            {
                CommandSender sender = defcmd.getSender();
                if (defcmd.getArgs().length < cmd.minArgs())
                {
                    sender.sendMessage("§c参数不足!");
                    return false;
                }
                if (((sender instanceof Player)) && (cmd.onlyConsole()))
                {
                    sender.sendMessage("§c玩家无法使用该指令!");
                    return false;
                }
                if ((!(sender instanceof Player)) && (cmd.onlyPlayer()))
                {
                    sender.sendMessage("控制台无法使用该指令！");
                    ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
                    return false;
                }
                if(!cmd.status()){
                    sender.sendMessage(String.format("§c该指令已停用！"));
                    return false;
                }
                String perm = cmd.permission();
                if (!("qiegao.default".equalsIgnoreCase(perm)) && (!sender.hasPermission(perm)))
                {

                    sender.sendMessage(String.format("§c您需要 %s 权限才能使用该指令！", new Object[] { perm }));
                    return false;
                }
                return true;
            }
        }
        return false;
    }
    public List<String> getList(DefaultCommand defcmd)
    {
        Method[] methods = Commands.class.getMethods();
        CommandSender sender = defcmd.getSender();
        List<String> list = new ArrayList();
        for (Method method : methods)
        {
            Cmd cmd = (Cmd)method.getAnnotation(Cmd.class);
            if((cmd == null)) break;
            String perm = cmd.permission();
            if (cmd.status()&&(defcmd.getArgs().length==1|| cmd.value().indexOf(defcmd.getArgs()[1])==0 )&&(("qiegao.default".equalsIgnoreCase(perm)) || (sender.hasPermission(perm))))
            {
                list.add(cmd.value());
            }
        }

        return list;
    }

    public Method getMethod(String subcmd)
    {
        Method[] methods = Commands.class.getMethods();
        for (Method method : methods)
        {
            Cmd cmd = (Cmd)method.getAnnotation(Cmd.class);
            if ((cmd != null) && (cmd.value().equalsIgnoreCase(subcmd))) {
                return method;
            }
        }
        return null;
    }

    public String getName()
    {
        return this.name;
    }


}
