package club.qiegaoshijie.qiegao.models;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.util.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Maps extends Models {
    private String username;
    private  int mapid;
    private  boolean status;

    public Maps(){
        setTableName("QIEGAOWORLD_Maps");
    }
    public List getMap(String username){
        ResultSet l= getOne("select id,uuid from qiegaoworld_user where username='"+username+"';");
        try {
            if(l==null ||!l.next())return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        try {
            String uuid=l.getString("uuid");

            ArrayList sk= (ArrayList) getList("select * from QIEGAOWORLD_maps where username='"+username+"' and status=false;");

            ArrayList list=new ArrayList();
            String sid="";
            YamlConfiguration yc=new YamlConfiguration();
            String file_path=Config.getString("skull.data-path")+"/userdata/"+uuid+".yml";
            yc.load(file_path);
            String money=yc.getString("money");
            BigDecimal m=Tools.toBigDecimal(money,new BigDecimal("0"));
            int _price=Integer.valueOf(Config.getString("maps.price"));
            BigDecimal count=new BigDecimal("0");
            for (Object s:sk) {
                Maps ss=(Maps)s;
                ItemStack mp=new ItemStack(Material.FILLED_MAP);
                MapMeta mm= (MapMeta) mp.getItemMeta();
                mm.setLore((List<String>) Qiegao.getMessages().getList("maps.info"));

                BigDecimal price=new BigDecimal(""+(_price ));

                if(m.compareTo(count.add(price))>0){
                    mm.setMapId(ss.getMapid());
                    mp.setItemMeta(mm);
                    list.add(mp);
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
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    public List getList(String sql){
        ResultSet s= null;
        ArrayList sk=new ArrayList();
        try {
            s = _getList(sql);
            while (s.next()) {
                Maps ss=new Maps();
                ss.setId(s.getInt("id"));
                ss.setMapid(s.getInt("mapid"));
                ss.setUsername(s.getString("username"));
                ss.setStatus(s.getBoolean("status"));
                sk.add(ss);

            }
            return  sk;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMapid() {
        return mapid;
    }

    public void setMapid(int mapid) {
        this.mapid = mapid;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
