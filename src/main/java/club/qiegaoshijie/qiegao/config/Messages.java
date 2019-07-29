package club.qiegaoshijie.qiegao.config;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.util.Log;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class Messages
{
    public static String PLUGIN_PREFIX;
    public static String RELOAD_SUCCEED;
    public static List<String> HELP;
    public static String ONLY_PLAYER;
    public static String ONLY_CONSOLE;
    public static String NO_PERM;
    public static String CMD_ERR;
    public static String CMD_USE;
    public static String GUI_MENU;
    public static String GUI_SEEGEM;
    public static String GUI_LISTGEM;
    public static String GUI_ACCEPT;
    public static String ITEMLORE;
    public static String SLOTLORE;
    public static String NULLSLOTLORE;
    public static String ANIMAL;
    public static String GUI_INLAY;
    private static HashMap<String,List> messages=new HashMap<>();
    private  static FileConfig cf;
    public static List<Location> sdj_chesh_list=new ArrayList<>();
    public static HashMap<String,Location> locationHashMap=new HashMap<>();


    public static void load(FileConfig config)
    {
        PLUGIN_PREFIX = String.format(config.getString("plugin-prefix", "&6[&b%s&6]&r "), new Object[] {
                Qiegao.getInstance().getName() });
        RELOAD_SUCCEED = config.getString("reload-succeed", "&a������������!");
        HELP = config.getStringList("help");
        ONLY_PLAYER = config.getString("only-player", "&c��������������������(&4��������������&c)!");
        ONLY_CONSOLE = config.getString("only-console", "&c������������������(&4����������������&c)!");
        NO_PERM = config.getString("no-perm", "&c�������� %s ��������������������!");
        CMD_ERR = config.getString("cmd-err", "&6��������: &4��������������!");
        CMD_USE = config.getString("cmd-use", "&6��������: &e/%s %s %s");
        GUI_MENU = config.getString("gui.inlay", "&e&l切糕工具箱");
        GUI_SEEGEM = config.getString("gui.seegem", "&e&l������������");
        GUI_LISTGEM = config.getString("gui.listgem", "&e&l��������");
        GUI_ACCEPT = config.getString("gui.accept", "&e&l�������� ");
        ITEMLORE = config.getString("itemlore", "&a&l&m��&b&l&m��&c&l&m��&r&e&l��������&c&l&m��&b&l&m��&a&l&m��");

        SLOTLORE = config.getString("slotlore", "&6[&a������&3<%s&3>&6]");
        NULLSLOTLORE = config.getString("nullslotlore", "&6[&a����&3<%s&3>&6]");
        ANIMAL = config.getString("animal", "动物申报");

//        ConfigurationSection sec = config.getConfigurationSection("");
//        Set<String> keys = sec.getKeys(false);
//        for (String s : keys)
//        {
//            if(sec.get(s) instanceof String){
//                List a=new ArrayList();
//                a.add(sec.getString(s));
//                messages.put(s,a);
//            }else{
//                messages.put(s, (List) sec.get(s));
//            }
//
//        }
    }

    public static String getString(String s){
        return getArrays(s).toString();

    }
    public static List getList(String s){
        return messages.get(s);
    }
    public static String[] getArrays(String s){
        List a = messages.get(s);
        String[] array =new String[a.size()];
        int i=0;
        for (Object b : a) {
            Log.info(b.toString());
            array[i++]=b.toString();
        }
        return array;
    }


}
