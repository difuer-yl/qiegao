package club.qiegaoshijie.qiegao.config;

import java.util.HashMap;
import java.util.List;

import club.qiegaoshijie.qiegao.util.Log;

public class Config
{
    public static boolean UPDATE_CHECK;
    private static HashMap<String,List> CONFIG=new HashMap<>();
    private static FileConfig ConfigFile;

    public static void load(FileConfig config)
    {
        UPDATE_CHECK = config.getBoolean("update-check", true);
        ConfigFile= config;
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
}

