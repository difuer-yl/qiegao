package club.qiegaoshijie.qiegao;

import cc.moecraft.icq.PicqBotX;
import cc.moecraft.icq.exceptions.HttpServerStartFailedException;
import club.qiegaoshijie.qiegao.command.CommandHandler;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.config.FileConfig;
import club.qiegaoshijie.qiegao.config.Messages;
import club.qiegaoshijie.qiegao.inventory.SigninGUI;
import club.qiegaoshijie.qiegao.listener.BlockListener;
import club.qiegaoshijie.qiegao.listener.EntityListener;
import club.qiegaoshijie.qiegao.listener.InventoryListener;
import club.qiegaoshijie.qiegao.listener.PlayerListener;
import club.qiegaoshijie.qiegao.runnable.MessageTask;
import club.qiegaoshijie.qiegao.runnable.QQBot;
import club.qiegaoshijie.qiegao.runnable.Server;
import club.qiegaoshijie.qiegao.util.HttpServer;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import club.qiegaoshijie.qiegao.util.sqlite.SqliteManager;
import net.minecraft.server.v1_13_R2.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Qiegao extends JavaPlugin implements Listener {

    private  static  Qiegao instance;
    private static FileConfig config;
    private static FileConfig messages;
//    private static FileConfig messages;
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
    private BukkitTask QQ;
    private  HttpServer qqServer;
    private  int day=0;
    public static boolean placeholderHook;
    public static boolean dynmapPlugin;
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
        sm =new SqliteManager(Config.getString("sqlite.file"));

        placeholderHook = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        dynmapPlugin = Bukkit.getPluginManager().isPluginEnabled("dynmap");
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

        runTask();
//        QQBot();

    }



    @Override
    public void onDisable() {

        getLogger().info("onDisable is called!");
        Qiegao.getMessages().save();
        getPluginConfig().save();
        this.qqServer.close();
        if (!this.getQQ().isCancelled())
            this.QQ.cancel();
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

    public void  runTask(){

        //公告进程
        new MessageTask(this,0).runTaskLater(this,20*5);

        //切糕报时
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
                int d= (int) (Bukkit.getWorld("world").getFullTime()/24000);
                if (d!=day){
                    Bukkit.getServer().broadcastMessage("[切糕报时]"+Tools.getGaoLi(d));
                    day=d;
                }
            }
        }.runTaskTimerAsynchronously(this, getConfig().getInt("settings.checktime",100), getConfig().getInt("settings.checktime",100));

        //qq消息监听
        this.QQ=new BukkitRunnable() {

            @Override
            public void run() {



                    Log.toConsole("创建socket");
//                    Qiegao.getInstance().setQqServer(new Server(31092));
                HttpServer httpServer=new HttpServer(31091);
                Qiegao.getInstance().setQqServer(httpServer);
                httpServer.start();

            }
        }.runTaskAsynchronously(this);
    }

    public void QQBot(){
        // 创建机器人对象 ( 信息发送URL, 发送端口, 接收端口, 是否DEBUG )
        //PicqBotX bot = new PicqBotX(31092, false);
//        PicqBotX bot = new PicqBotX("127.0.0.1", 31090, 31092, true);
//
//        //bot.getAccountManager().addAccount(
//        //new BotAccount("One", bot.getEventManager(), "127.0.0.1", 31091),
//        //new BotAccount("Two", bot.getEventManager(), "127.0.0.1", 31090)
//        //);
//
////        bot.setMaintenanceMode(false);
//        bot.setUniversalHyExpSupport(true);
//
//        // 设置异步
////        bot.setUseAsync(true);
//
//        try
//        {
//            bot.getEventManager().registerListener(new QQBot());
////                    .registerListener(new TestListener()) // 注册监听器
////                    .registerListener(new RequestListener())
////                    .registerListener(new AntiRecallListener())
////                    .registerListener(new AnnoyingListener())
////                    .registerListener(new ExceptionListener()); // 可以注册多个监听器
////            if (!bot.isDebug()) bot.getEventManager().registerListener(new SimpleTextLoggingListener()); // 这个只是在不开Debug的时候用来Log消息的
//
//            // 启用指令管理器, 启用的时候会自动注册指令
//            // 这些字符串是指令前缀, 比如!help的前缀就是!
//            bot.enableCommandManager("bot -", "!", "/", "~", "！", "我以令咒命之，", "我以令咒命之, ");
//            System.out.println(bot.getCommandManager().getRegisteredCommands());
//
//            bot.startBot(); //
//            Log.toConsole("bot 启动");
//        }
//        catch ( IllegalAccessException | InstantiationException e)
//        {
//            e.printStackTrace(); // 启动失败, 结束程序
//        } catch (HttpServerStartFailedException e) {
//            e.printStackTrace();
//        }
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

    public BukkitTask getQQ() {
        return QQ;
    }

    public void setQQ(BukkitTask QQ) {
        this.QQ = QQ;
    }

    public HttpServer getQqServer() {
        return qqServer;
    }

    public void setQqServer(HttpServer qqServer) {
        this.qqServer = qqServer;
    }
}
