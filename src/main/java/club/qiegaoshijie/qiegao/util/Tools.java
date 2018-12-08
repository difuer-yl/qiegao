package club.qiegaoshijie.qiegao.util;

import club.qiegaoshijie.qiegao.Qiegao;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.*;

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

    public static int getPing(Player player) {
        int ping = 0;
        String color = "";
        Class<?> craftPlayerClass = null;
        try {
            craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().substring(23) + ".entity.CraftPlayer");
            Object handle = craftPlayerClass.getMethod("getHandle", new Class[0]).invoke(craftPlayerClass.cast(player), new Object[0]);
            ping = handle.getClass().getDeclaredField("ping").getInt(handle);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


        return  ping;
    }

    public static double getTps()
    {
        double tps = Double.valueOf(Qiegao.getInstance().getTPS(0)).doubleValue();
        if (tps > 20.0D) {
            tps = 20.0D;
        }

        return tps;
    }

    public static class ServerThread extends Thread {
        private Socket socket;
        private  String content;

        // Ready to conversation
        public ServerThread(String s) throws IOException {
            this.content = s;
            start();
        }

        // Execute conversation
        public void run() {
            URL obj = null;
            try {
                obj = new URL(content);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                //默认值我GET
                con.setRequestMethod("GET");

//                Socket socket=new Socket("8818", );

                //添加请求头
//        con.setRequestProperty("User-Agent", USER_AGENT);

                int responseCode = con.getResponseCode();
            } catch (MalformedURLException e) {
//                e.printStackTrace();
            } catch (ProtocolException e) {
//                e.printStackTrace();
            } catch (IOException e) {
//                e.printStackTrace();
            }

        }

    }
    public static String getGaoLi(int day){
        int year=day/360;
        int month=(day%360)/30+1;

        day=(day%30);
        if (day==0)day=30;
        return "当前日期：切糕历糕纪元"+year+"年"+month+"月"+day+"日";
    }
}
