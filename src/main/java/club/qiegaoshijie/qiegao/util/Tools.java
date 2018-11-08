package club.qiegaoshijie.qiegao.util;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        if (license.substring(0,2).equalsIgnoreCase("糕宠")){
            return  true;
        }
        EntityType t=null;
        switch (type){
            case "H":t=EntityType.HORSE;break;
            case "S":t=EntityType.SKELETON_HORSE;break;
            case "D":t=EntityType.DONKEY;break;
            case "M":t=EntityType.MULE;break;
            case "P":t=EntityType.PIG;break;
            default:return false;
        }
        if ( t==et)return true;
        return false;
    }

    public static Boolean isOnly(ItemStack im){
        if (im.hasItemMeta()){
            ItemMeta itemMeta=im.getItemMeta();
            if (itemMeta.hasLore()){
                if (itemMeta.getLore().contains("唯一")){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }
}
