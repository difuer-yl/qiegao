package club.qiegaoshijie.qiegao.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.util.Log;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Config
{
    public static boolean WS_ENABLE;
    public static String MYSQL_FLAG;
    public static String VERSION;
    public static boolean DEBUG;

    private static HashMap<String,List> CONFIG=new HashMap<>();
    private static FileConfig ConfigFile;
    private static Boolean isrun=false;
    private static Long iswsj=-1L;
    private static int sdjStatus=-1;

    public static HashMap<String,Integer> fraction=new HashMap<>();
    public static HashMap<String, Player> playerHashMap =new HashMap<>();
    public static List<String> helpHashMap =new ArrayList<>();

    public static HashMap<String, Location> quandi=new HashMap<>();

    private static boolean message=true;


    public static void load(FileConfig config)
    {
        WS_ENABLE = config.getBoolean("ws.enable", true);
        MYSQL_FLAG=config.getString("mysql.falg","?useSSL=false&autoReconnect=true&testOnBorrow=true&validationQuery=select 1");
        config.set("autoBackup",false);
        config.set("version", Qiegao.getInstance().getDescription().getVersion());
        ConfigFile= config;
        sdjStatus=config.getInt("sdjStatus",-1);
        setMessage(config.getBoolean("auto-message", true));

        DEBUG=config.getBoolean("debug",false);
//        loadGems(config);
    }

    public static String getString(String s,String def) {
//        return getArrays(s).toString();

        return ConfigFile.getString(s)!=null?ConfigFile.getString(s):def;
    }
    public static String getString(String s){
//        return getArrays(s).toString();

        return ConfigFile.getString(s);
    }
    public static List getList(String s){
        return CONFIG.get(s);
    }
    public static String[] getArrays(String s){
        List a = CONFIG.get(s);
        String[] array =new String[a.size()];
        int i=0;
        for (Object b : a) {
            Log.info(b.toString());
            array[i++]=b.toString();
        }
        return array;
    }

    public static Boolean getIsrun() {
        return isrun;
    }

    public static void setIsrun(Boolean isrun) {
        Config.isrun = isrun;
    }

    public static Long getIswsj() {
        return iswsj;
    }

    public static void setIswsj(Long iswsj) {
        Config.iswsj = iswsj;
    }

    public static int getSdjStatus() {
        return sdjStatus;
    }

    public static void setSdjStatus(int sdjStatus) {
        Config.sdjStatus = sdjStatus;
    }

    public static boolean isMessage() {
        return message;
    }

    public static void setMessage(boolean message) {
        Config.message = message;
    }
}

