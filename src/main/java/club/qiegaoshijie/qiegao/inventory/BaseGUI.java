package club.qiegaoshijie.qiegao.inventory;

import club.qiegaoshijie.qiegao.util.Log;
import org.bukkit.inventory.Inventory;

import java.util.Calendar;
import java.util.HashMap;

public class BaseGUI {
    private static HashMap<String ,BaseGUI> GUIHashMap =new HashMap<String, BaseGUI>();
    private static BaseGUI obj=null;
    private static int day= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    public static BaseGUI getGUI(String username)
    {
        if (day !=Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
            GUIHashMap=new HashMap<String, BaseGUI>();
            day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
        if(GUIHashMap!=null&&GUIHashMap.containsKey(username)){
        }else{
            obj.createGUI(username);
        }

        return GUIHashMap.get(username);
    }
    public static void setGUI(String username,BaseGUI inv){
        if (GUIHashMap.containsKey(username)){
            GUIHashMap.replace(username,inv);
        }else{
            GUIHashMap.put(username,inv);
        }
    }

    public   void createGUI(String username){
    }

    public void setObj(BaseGUI obj) {
        this.obj = obj;
    }
}
