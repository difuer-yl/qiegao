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
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Qiegao extends JavaPlugin implements Listener {

    private  static  Qiegao instance;
    private static FileConfig config;
    private static FileConfig messages;
    private CommandHandler commandhandler;
    private static SqliteManager sm;
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
    }



    @Override
    public void onDisable() {

        getLogger().info("onDisable is called!");
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
