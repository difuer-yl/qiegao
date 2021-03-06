package club.qiegaoshijie.qiegao.models;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.parser.JSONParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task extends Models {
    private String title;
    private String content;
    private int status;
    private String username;

    public Task(){
        setTableName("QieGaoWorld_task");
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List getList(String sql){
        ResultSet s= null;
        ArrayList al=new ArrayList();
        try {
            s = _getList(sql);
            while (s.next()) {
                Task obj=new Task();
                obj.setId(s.getInt("id"));
                obj.setTitle(s.getString("title"));
                obj.setContent(s.getString("content"));
                obj.setUsername(s.getString("username"));
                obj.setStatus(s.getInt("status"));

                al.add(obj);

            }
            return  al;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public List getTask(){
        return getTask(1);
    }

    public List getTask(int page){
        int size=45;
        int start=45*(page-1);
        List list1 =this.where("status=0").limit(start+","+size).select();
        ArrayList list=new ArrayList();
        Task o=null;
        for (Object object :list1) {
            o= (Task) object;
            BookMeta meta = (BookMeta)Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
            String con=o.getContent();
            while(con != null){
                if (con.length()>250){
                    meta.addPage(con.substring(0,250));
                    con=con.substring(250);
                }else{
                    meta.addPage(con);
                    con =null;
                }
            }
            ItemStack it = new ItemStack(Material.WRITTEN_BOOK);

            meta.setTitle(o.getTitle());

            meta.setAuthor(o.getUsername());
            meta.setLore((List<String>) Qiegao.getMessages().getList("task.info"));

            it.setItemMeta(meta);
            list.add(it);
        }

        return  list;

    }
}
