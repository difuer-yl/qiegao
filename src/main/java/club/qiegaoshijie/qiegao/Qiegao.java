package club.qiegaoshijie.qiegao;

import club.qiegaoshijie.qiegao.command.CommandHandler;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.config.FileConfig;
import club.qiegaoshijie.qiegao.config.Messages;
import club.qiegaoshijie.qiegao.inventory.SigninGUI;
import club.qiegaoshijie.qiegao.listener.BlockListener;
import club.qiegaoshijie.qiegao.listener.EntityListener;
import club.qiegaoshijie.qiegao.listener.InventoryListener;
import club.qiegaoshijie.qiegao.listener.PlayerListener;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.sqlite.SqliteManager;
import net.minecraft.server.v1_13_R2.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Qiegao extends JavaPlugin implements Listener {

    private  static  Qiegao instance;
    private static FileConfig config;
    private static FileConfig messages;
    private CommandHandler commandhandler;
    private static SqliteManager sm;

    private Object serverInstance;
    private Field tpsField;
    private final DecimalFormat format = new DecimalFormat("##.##");
    public String Unknown = null;
    public String Good = null;
    public String Warning = null;
    public String Bad = null;
    public String lastTPSstatus = null;
    @Override
    public void onEnable() {
        instance = this;
        Log.toConsole("§a====================", false);
        Log.toConsole("§a插件名: §e切糕世界工具包", false);
        Log.toConsole("§a插件版本: §e" + getDescription().getVersion(), false);
        Log.toConsole("§a====================", false);
        this.config = new FileConfig(this, "config.yml");
        this.messages = new FileConfig(this, "messages.yml");
        Config.load(this.config);
        Messages.load(this.messages);
        sm =new SqliteManager();

        this.commandhandler = new CommandHandler("Qiegao");
//        new BukkitRunnable() {
//
//            @Override
//            public void run() {
//
//                MySQLManager.get().enableMySQL();
//            }
//        }.runTaskAsynchronously(this);
//        利用BukkitRunnable创建新线程，防止使用SQL而堵塞主线程
        registerListener();
        //记录tps
        try
        {
            this.serverInstance = MinecraftServer.class.getMethod("getServer", new Class[0]).invoke(null, new Object[0]);
            this.tpsField = this.serverInstance.getClass().getField("recentTps");
        }
        catch (NoSuchFieldException|SecurityException|IllegalAccessException|IllegalArgumentException| InvocationTargetException |NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        new BukkitRunnable()
        {
            public void run()
            {
                double currentTPS = Double.valueOf(getTPS(0)).doubleValue();
                String currentTPSstatus =Unknown;
                if (currentTPS >= 18.25D) {
                    currentTPSstatus = Good;
                } else if (currentTPS >= 17.49D) {
                    currentTPSstatus = Warning;
                } else {
                    currentTPSstatus = Bad;
                }
                if (lastTPSstatus != currentTPSstatus) {
                    printTPSMessage(lastTPSstatus, currentTPSstatus);
                }
                lastTPSstatus = currentTPSstatus;
            }
        }.runTaskTimerAsynchronously(this, getConfig().getInt("settings.checktime"), getConfig().getInt("settings.checktime"));
    }



    @Override
    public void onDisable() {

        getLogger().info("onDisable is called!");
        Qiegao.getMessages().save();
        getPluginConfig().save();
//        MySQLManager.get().close(); //断开连接
    }

    public static Qiegao getInstance()
    {
        return instance;
    }

    public void reload()
    {
        Bukkit.getServer().clearRecipes();
        this.config.reload();
        this.messages.reload();
        Config.load(this.config);
        Messages.load(this.messages);
    }
    public void registerListener()
    {

        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        new SigninGUI();
    }

    public String getTPS(int time)
    {
        try
        {

            double[] tps = (double[])this.tpsField.get(this.serverInstance);
            return this.format.format(tps[time]);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
    public void printTPSMessage(String lastTPS, String currentTPS)
    {
        List<String> list = new ArrayList<>();
        list.add("§f");
        list.add("§f");
        list.add(getConfig().getString("message.TPSChanged") + lastTPS + getConfig().getString("message.Arrow") + currentTPS);
        list.add(getConfig().getString("message.Advice") + printTips(currentTPS));
        list.add("§f");
        list.add("§f");
        for (String string : list) {
            Bukkit.broadcastMessage(string);
        }
    }

    public String printTips(String level)
    {
        if (level == this.Unknown) {
            return getConfig().getString("advice.Unknown");
        }
        if (level == this.Good) {
            return getConfig().getString("advice.Good");
        }
        if (level == this.Warning) {
            return getConfig().getString("advice.Warning");
        }
        if (level == this.Bad) {
            return getConfig().getString("advice.Bad");
        }
        return "Error";
    }
    public CommandHandler getCommandHandler()
    {
        return this.commandhandler;
    }



    public static FileConfig getMessages() {
        return messages;
    }

    public static SqliteManager getSm() {
        return sm;
    }

    public static  FileConfig getPluginConfig() {
        return config;
    }
}
