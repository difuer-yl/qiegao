package club.qiegaoshijie.qiegao.models;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.config.FileConfig;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import com.google.gson.JsonArray;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.text.Style;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Skull extends Models{
    private String user_id;
    private boolean status;
    private int number;
    private String content;
    private String name;


    public Skull(){
        setTableName("QieGaoWorld_skullcustomize");
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = String.valueOf( user_id);
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getSkull(String username){
        User user=new User();
        user= (User) user.where("username='"+username+"'").find();
        if (user==null)return null;
        int id=user.getId();
        String uuid=user.getUuid();

        YamlConfiguration yc=new YamlConfiguration();
        String file_path=Config.getString("skull.data-path")+"/userdata/"+uuid+".yml";
        try {
            yc.load(file_path);
        } catch (IOException e) {
            Log.toConsole(user.getUsername()+"ess文件读取错误！");
            e.printStackTrace();
            return null;
        } catch (InvalidConfigurationException e) {
            Log.toConsole(user.getUsername()+"ess文件读取错误！");
            e.printStackTrace();
            return null;
        }
        String money=yc.getString("money");
        BigDecimal m=Tools.toBigDecimal(money,new BigDecimal("0"));
        int def=Integer.valueOf(Config.getString("skull.default"));
        int _price=Integer.valueOf(Config.getString("skull.price"));
        BigDecimal count=new BigDecimal("0");

        List list=this.where("user_id='"+id+"' and status=false").select();
        Skull ss=null;
        String sid="";
        for (Object object :list) {
            ss=(Skull)object;
            JSONParser parser = new JSONParser();
            String[] con=ss.getContent().split(":");
            String textures = con[1];
            SkullMeta meta = (SkullMeta)Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
            GameProfile profile = new GameProfile(UUID.fromString(con[0]), null);

            profile.getProperties().put("textures", new Property("textures", textures));
            Field profileField = null;
            try
            {
                profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
            }
            catch (NoSuchFieldException|IllegalArgumentException|IllegalAccessException e1)
            {
                e1.printStackTrace();
            }
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD,ss.getNumber());

            meta.setDisplayName(ss.getName());
            meta.setLore((List<String>) Qiegao.getMessages().getList("skull"));
            meta.setLocalizedName(ss.getName());
            skull.setItemMeta(meta);
            BigDecimal price=new BigDecimal(""+(def + _price * ss.getNumber()));

            if(m.compareTo(count.add(price))>0){
                list.add(skull);
                sid+=ss.getId()+",";
                count=count.add(price);
            }

        }

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"eco take "+username+" "+count.toString());
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"eco give NovaTang "+count.toString());
        if(sid.length()>1){
            update("update "+getTableName() +" SET status=true where id in ("+sid.substring(0,sid.length()-1)+");");

        }
        return  list;

    }
    public ItemStack getSkull(int id){

        Skull ss= (Skull) this.find(id);
        if (ss==null){
            return null;
        }
        String[] con=ss.getContent().split(":");
        String textures = con[1];
        SkullMeta meta = (SkullMeta)Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        GameProfile profile = new GameProfile(UUID.fromString(con[0]), null);

        profile.getProperties().put("textures", new Property("textures", textures));
        Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        }
        catch (NoSuchFieldException|IllegalArgumentException|IllegalAccessException e1)
        {
            e1.printStackTrace();
        }
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD,ss.getNumber());

        meta.setDisplayName(ss.getName());
        meta.setLore((List<String>) Qiegao.getMessages().getList("skull"));
        meta.setLocalizedName(ss.getName());
        skull.setItemMeta(meta);

        return  skull;

    }
    public List getList(String sql){
        ResultSet s= null;
        ArrayList sk=new ArrayList();
        try {
            s = _getList(sql);
            while (s.next()) {
                Skull ss=new Skull();
                ss.setId(s.getInt("id"));
                ss.setNumber(s.getInt("number"));
                ss.setContent(s.getString("content"));
                ss.setName(s.getString("name"));
                sk.add(ss);

            }
            return  sk;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


}
