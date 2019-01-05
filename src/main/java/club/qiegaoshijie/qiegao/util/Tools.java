package club.qiegaoshijie.qiegao.util;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.util.sqlite.SqliteManager;
import net.minecraft.server.v1_13_R2.ChatMessageType;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
        con.setRequestProperty("Content-Type", "application/json");

                con.setConnectTimeout(5*1000);

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

    public static void send(Player p, List<String> json)
    {
        if ((json == null) || (json.isEmpty())) {
            return;
        }
        String first = (String)json.get(0);
        try
        {
            IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', first));
            for (String line : json) {
                if ((line != null) && (!line.isEmpty()) && (!line.equals(first))) {
                    icbc.addSibling(IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', line)));
                }
            }
            PacketPlayOutChat chat = new PacketPlayOutChat(icbc, ChatMessageType.CHAT);

            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(chat);
        }
        catch (Exception e)
        {

//            System.out.println("[qiegao] There was an error sending the following message to: " +
//                    p != null ? p.getName() : "unknown player");
//            System.out.println("[qiegao] Message: " + json != null ? json.toString() : "null");
//            System.out.println("[qiegao] Reason: " + e.getMessage() != null ? e.getMessage() : "unknown error");
        }
    }
    public static void send(Player p, String json)
    {
            IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', json));
            PacketPlayOutChat chat = new PacketPlayOutChat(icbc, ChatMessageType.CHAT);
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(chat);
        try
        {


        }
        catch (Exception e)
        {

            System.out.println("[qiegao] There was an error sending the following message to: " +
                    p != null ? p.getName() : "unknown player");
//            System.out.println("[qiegao] Message: " + json != null ? json : "null");
//            System.out.println("[qiegao] Reason: " + e.getMessage() != null ? e.getMessage() : "unknown error");
        }
    }
    public static byte determineDataOfDirection(BlockFace bf){
        if(bf.equals(BlockFace.NORTH))
            return (byte)2;
        else if(bf.equals(BlockFace.SOUTH))
            return (byte)3;
        else if(bf.equals(BlockFace.WEST))
            return (byte)4;
        else if(bf.equals(BlockFace.EAST))
            return (byte)5;
        return (byte)0;
    }

    public static List<String> getSdjPlayer(){
//        int x=849;
//        int z=-33;
        int x=953;
        int z=1529;
        int j=20;
        List<String> sql=new ArrayList<>();
        for (int i=0;i<=20;){

            if (i<5){
                j=20;
            }
            else if (i<8){
                j=19;
            }else if(i<10){
                j=18;
            }else if(i<12){
                j=17;
            }else {
                j--;
            }

            if(i<=20&&j>=0){
                sql.add("(x>="+(x-i)+" and x <="+(x+i)+" and z>="+(z-j)+" and z<="+(z+j)+")");
            }
            if(j<6){
                i=20;
            }else if(j<9){
                i=19;
            }else if(j<11){
                i=18;
            }else if(j<13){
                i=17;
            }else{
                i++;
            }
            if(j<0){
                i++;
            }

        }
        String s=String.join(" || ",sql);
        List<String> list=new ArrayList<>();
        SqliteManager sqliteManager =new SqliteManager("G:\\mc\\spigotmc\\1.13.2\\plugins\\CoreProtect\\database.db");
        ResultSet sd_chest_data = sqliteManager.filter("select * from co_block where action=1 and   y>=60  and ("+s+") group by x,y,z ");
        HashMap<Integer,Integer> user_id=new HashMap<>();
//        Log.toConsole("select * from co_black where action=1 and   y>=60  and ("+s+") group by x,y,z ");
//        return list;

        try {
            while (sd_chest_data.next()){
                user_id.put(sd_chest_data.getInt("user"),user_id.get(sd_chest_data.getInt("user"))+1);
            }
            return list;
//            for (int i : user_id.keySet()){
//                if(user_id.get(i)>4){
//                    user_id.remove(i);
//                }
//            }
//            if (user_id.size()>0){
//                ResultSet userlist=sqliteManager.filter("select user from co_user where  id in ("+String.join(",", (CharSequence) user_id.values())+")");
//                while (userlist.next()){
//                    list.add(userlist.getString("user"));
//                }
//            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return list;
    }
}
