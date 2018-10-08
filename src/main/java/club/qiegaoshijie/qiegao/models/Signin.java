package club.qiegaoshijie.qiegao.models;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Signin extends Models {
    private String username;
    private String reward;
    private int year;
    private int month;
    private int day;


    public Signin() {
        setTableName("QIEGAOWORLD_signin");
    }


    public  void add(String username, Player p){

        setYear(2018);
        setMonth(10);
        setDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        setUsername(username);

//        String day_conf=Config.getString("signin.day");
//        String[] days=day_conf.split(",");
//        List l=getList("select * from qiegaoworld_signin where username= '"+username+"' and year="+getYear()+" and month="+getMonth());
        p.sendMessage("本月活动道具已发放完毕");
//        for (String d: days) {
//            if(d .equals( 1+l.size()+"")){
//                String id=getReward(getMonth(),getYear());
//                if (id==null){
//                    p.sendMessage("本月活动道具已发放完毕");
//                }
//                setReward(id);
//                break;
//            }
//        }

        if(insert()){
            p.sendMessage("签到成功！");
//            if (getReward()!=null){
//                ItemStack mp=new ItemStack(Material.FILLED_MAP);
//                MapMeta mm= (MapMeta) mp.getItemMeta();
//                ArrayList<String> ll = new ArrayList<>();
//                ll.add(getMonth()+"月签到活动道具");
//                mm.setDisplayName(getMonth()+"月签到活动道具");
//                mm.setLore(ll);
//                mm.setMapId(Integer.parseInt(getReward()));
//                mp.setItemMeta(mm);
//                if(p.getInventory().firstEmpty()!=-1){
//                    p.getInventory().addItem(mp);
//                }else{
//                    p.getWorld().dropItem(p.getLocation(),mp);
//                }
//            }
        }else{
            p.sendMessage("签到失败，请联系管理员");
        }
        return;

    }
    public  String getReward(int month,int year){
        ResultSet s = null;
        ArrayList sk = new ArrayList();

        try {
            s = _getList("select reward from qiegaoworld_signin where  month="+month+" and year="+year);
            while (s!=null && s.next()) {

                sk.add(s.getString("reward"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        String reward_conf=Config.getString("signin.reward");
        int len;
        ArrayList a=new ArrayList();
        if(reward_conf.split("-").length==2){
            int start= Integer.parseInt(reward_conf.split("-")[0]);
            int end= Integer.parseInt(reward_conf.split("-")[1]);
            for (int i=start;i<=end;i++){
                a.add(i);
            }
            len=a.size();
        }else{

            String[] reward=reward_conf.split(",");
            if (sk.size()>= reward.length){

                return null;
            }
            len=reward.length;
        }
        int d;

        while (true){

            d= (int) (Math.random()*len);
            if(!sk.contains(a.get(d).toString()))
                return  a.get(d).toString();

        }

    }
    public List getList(String sql) {
        ResultSet s = null;
        ArrayList sk = new ArrayList();
        try {
            s = _getList(sql);
            if(s==null) return  null;
            while (s.next()) {
                Signin ss = new Signin();
                ss.setId(s.getInt("id"));
                ss.setDay(s.getInt("day"));
                ss.setMonth(s.getInt("month"));
                ss.setYear(s.getInt("year"));
                ss.setUsername(s.getString("username"));
                ss.setReward(s.getString("reward"));
                sk.add(ss);

            }
            return sk;
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

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
