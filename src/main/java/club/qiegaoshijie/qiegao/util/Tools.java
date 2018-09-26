package club.qiegaoshijie.qiegao.util;

import org.bukkit.entity.EntityType;

import java.math.BigDecimal;
import java.math.MathContext;

public class Tools {
    public static Integer getTime(){
        int times = 0;
        try {
            times = (int) (System.currentTimeMillis()/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(times==0){
            System.out.println("String转10位时间戳失败");
        }
        return times;
    }
    public static BigDecimal toBigDecimal(String input, BigDecimal def)
    {
        if ((input == null) || (input.isEmpty())) {
            return def;
        }
        try
        {
            return new BigDecimal(input, MathContext.DECIMAL128);
        }
        catch (NumberFormatException e)
        {
            return def;
        }
        catch (ArithmeticException e) {}
        return def;
    }

    public static Boolean isType(EntityType et,String license){


        String type=license.substring(3,4);
        EntityType t=null;
        switch (type){
            case "H":t=EntityType.HORSE;break;
            case "S":t=EntityType.SKELETON_HORSE;break;
            case "D":t=EntityType.DONKEY;break;
            case "M":t=EntityType.MULE;break;
            case "P":t=EntityType.PIG;break;
            default:return false;
        }
        Log.toConsole(t.name());
        Log.toConsole(et.name());
        if ( t==et)return true;
        return false;
    }
}
