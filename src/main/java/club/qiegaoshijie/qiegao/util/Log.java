package club.qiegaoshijie.qiegao.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Log
{
    private static Logger logger = Qiegao.getInstance().getLogger();

    public static void info(String msg)
    {
        logger.info(msg);
    }

    public static void warning(String msg)
    {
        logger.warning(msg);
    }

    public static void toConsole(String msg, boolean b)
    {
        if (b) {
            Bukkit.getConsoleSender().sendMessage(
                    ChatColor.translateAlternateColorCodes('&', Messages.PLUGIN_PREFIX + msg));
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }public static void toConsole(String msg)
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public static void toSender(CommandSender sender, String msg, boolean b)
    {
        if (b) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.PLUGIN_PREFIX + msg));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    public static void toSender(CommandSender sender, List<String> msgs, boolean b)
    {
        if (b) {
            for (String msg : msgs) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.PLUGIN_PREFIX + msg));
            }
        } else {
            for (String msg : msgs) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
    }

    public static void toPlayer(Player player, String msg, boolean b)
    {
        if (b) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.PLUGIN_PREFIX + msg));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    public static void toPlayer(Player player, List<String> msgs, boolean b)
    {
        if (b) {
            for (String msg : msgs) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.PLUGIN_PREFIX + msg));
            }
        } else {
            for (String msg : msgs) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
    }

    public static String translate(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> translate(List<String> text)
    {
        List<String> list = new ArrayList();
        for (String line : text) {
            list.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return list;
    }

}

