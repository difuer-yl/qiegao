package club.qiegaoshijie.qiegao.models;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.inventory.SigninGUI;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Signin extends Models {
    private String username;
    private String reward;
    private int year;
    private int month;
    private int day;
    private int time;
    private int month_total;
    private int total;
    private int continuous;
    private int supplement;
    private static HashMap<String,Signin> _signinHashMap=new HashMap<String, Signin>();

    private static int _now=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    public Signin() {
        setTableName("QIEGAOWORLD_signin");
    }
    public Signin(String username){
        setTableName("QIEGAOWORLD_signin");
        setYear(Calendar.getInstance().get(Calendar.YEAR));
        setMonth(Calendar.getInstance().get(Calendar.MONTH)+1);
        setDay(_now);

        setUsername(username);

        ResultSet _one=getOne("select * from qiegaoworld_signin where username='"+username+"' order by time DESC");
        ResultSet s;
        try {
            if(_one==null|| _one.getInt("total")==0 ){
                s= _getList("select count(*) as num from qiegaoworld_signin where username='"+username+"' ");
                if (s==null){
                    setTotal(1);
                }else{
                    setTotal(s.getInt("num"));
                }
                s = _getList("select count(*) as num from qiegaoworld_signin where username='"+username+"' and year="+getYear() +" and month="+getMonth());
                if (s==null){
                    setMonth_total(0);
                }else{
                    setMonth_total(s.getInt("num"));
                }

                updateContinuous();

                setSupplement(getContinuous()/7);
            }else{
                setTotal(_one.getInt("total"));
                if(getYear()==_one.getInt("year")&&getMonth()==_one.getInt("month")){
                    setMonth_total(_one.getInt("month_total"));
//                    if (_now-1==_one.getInt("day")||_now==_one.getInt("day")){
                        setContinuous(_one.getInt("continuous"));
//                    }else{
//                        setContinuous(0);
//                    }

                }else{
                    s = _getList("select count(*) as num from qiegaoworld_signin where username='"+username+"' and year="+getYear() +" and month="+getMonth());
                    if (s==null){
                        setMonth_total(0);
                    }else{
                        setMonth_total(s.getInt("num"));
                    }
                    setContinuous(0);
                    setSupplement(0);
                }

                setSupplement(_one.getInt("supplement"));

            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Signin getSignin(String username){
        if (_now!=Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
            _signinHashMap=new HashMap<String, Signin>();
            _now=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
        if (_signinHashMap!=null && _signinHashMap.containsKey(username)){
        }else{
            _signinHashMap.put(username,new Signin(username));
        }
            return _signinHashMap.get(username);
    }
    public static void setSignin(String username, Signin inv){
        if (_signinHashMap.containsKey(username)){
            _signinHashMap.replace(username,inv);
        }else{
            _signinHashMap.put(username,inv);
        }
    }
    public static void signin(Player p,int day){
        getSignin(p.getName()).add(p,day);
    }
    public static void signin(Player p){
        getSignin(p.getName()).add(p,Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }
    //更新连续签到数
    private void updateContinuous(){
        int d=_now;
        int o=d-1;
        int c=0;
        int da=0,last=0;
        try {
            ResultSet rs=_getList("select day from qiegaoworld_signin where username='"+getUsername()+"' and year="+getYear()+" and month="+getMonth()+" order by day DESC");
            while (rs!=null&&rs.next()){
                da=rs.getInt("day");
                if((c==0&&da==o)){
                    d-=1;

                }
                if((da==(getDay()-1)&&getDay()!=_now)){
                    d-=1;
                    c+=1;
                }
                if ((da==d)){
                    c++;
                    last=d;
                    d-=1;

                }else{
                    break;
                }
                if(d<=0)break;
            }
            if((last==(getDay()+1)&&getDay()!=_now)){
                c+=1;
            }


            setContinuous(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private   void add(Player p,int day){
        setDay(day);
        String username=p.getName();
        setTime((int) (new Date().getTime()/1000));
        setTotal(getTotal()+1);
        setMonth_total(getMonth_total()+1);
        setContinuous(getContinuous()+1);
        if(day==getDay()&&getContinuous()!=0&&getContinuous()%7==0){
            setSupplement(getSupplement()+1);
        }
        if (day!=_now&&getSupplement()>0){
            setSupplement(getSupplement()-1);
        }
        updateContinuous();
        String id=getReward(username);
        if (id==null){
            p.sendMessage("本月活动道具已发放完毕");
        }
        setReward(id);
        if(insert()){
            p.sendMessage("签到成功！");
            if (getReward()!=null){
                ItemStack mp=new ItemStack(Material.FILLED_MAP);
                MapMeta mm= (MapMeta) mp.getItemMeta();
                ArrayList<String> ll = new ArrayList<>();
                ll.add(getMonth()+"月签到活动道具");
//                ll.add("");
                mm.setDisplayName(getMonth()+"月签到活动道具");
                mm.setLore(ll);
                mm.setMapId(Integer.parseInt(getReward()));
                mp.setItemMeta(mm);
                if(p.getInventory().firstEmpty()!=-1){
                    p.getInventory().addItem(mp);
                }else{
                    p.getWorld().dropItem(p.getLocation(),mp);
                }
            }

            Signin.setSignin(username,this);
            ((SigninGUI)SigninGUI.getGUI(username)).updateTotal(getDay());
        }else{
            p.sendMessage("签到失败，请联系管理员");
        }
        return;

    }
    public  String getReward(String  username){
        ResultSet s = null;
        ArrayList user = new ArrayList();
        ArrayList globe = new ArrayList();
        ArrayList other = new ArrayList();
        ArrayList user_new = new ArrayList();
        ArrayList globe_new = new ArrayList();
        int month_num=getMonth_total(),week_num,num=getTotal();

        int day=getDay();
        int month=getMonth();
        int year=getYear();
        int week=Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2;
        if (week==-1)week=6;
        String date=""+year+"-"+month+"-"+day;
        try {
            s = _getList("select count(*) as num from qiegaoworld_signin where username='"+username+"' and year="+year +" and month="+month+" and day >"+(day-week));
            if (s==null){
                week_num=0;
            }else{
                week_num=s.getInt("num");
            }


            s = _getList("select reward from qiegaoworld_signin where  reward !='null'");
            while (s!=null && s.next()){
                globe_new.add(s.getString("reward"));
            }
            s = _getList("select reward from qiegaoworld_signin where reward !='null' and username='"+username+"'");
            while (s!=null && s.next()){
                user_new.add(s.getString("reward"));
            }


            String sql="select * from qiegaoworld_REWaRD where (start_time=='' or (start_time>='"+date+"' and end_time <='"+date+"'))";
            s=_getList(sql);
            while(s!=null && s.next()){
                String reward=s.getString("reward_id");
                int rele_mode=s.getInt("release_mode");
                int mode=s.getInt("mode");
                String rele_time=s.getString("release_time");
                if(rele_mode==0){
                    Log.toConsole("每天");
                    if (reward.contains("-")){
                        String[] a=reward.split("-");
                        reward="";
                        for (int i=Integer.parseInt(a[0]);i<Integer.parseInt(a[1]);i++){
                            reward+=i+",";
                        }
                        reward=reward.substring(0,reward.length()-1);
                    }
                    String[] id=reward.split(",");

                    switch (mode){
                        case 0:
                            other.addAll(Arrays.asList(id));break;
                        case 1:
                            user.addAll(Arrays.asList(id));break;
                        case 2:
                            globe.addAll(Arrays.asList(id));break;

                    }
                }else if(rele_mode==1){
                    if((","+rele_time+",").contains(","+(week+1)+",")){
                        if (reward.contains("-")){
                            String[] a=reward.split("-");
                            reward="";
                            for (int i=Integer.parseInt(a[0]);i<Integer.parseInt(a[1]);i++){
                                reward+=i+",";
                            }
                            reward=reward.substring(0,reward.length()-1);
                        }
                        String[] id=reward.split(",");

                        switch (mode){
                            case 0:
                                other.addAll(Arrays.asList(id));break;
                            case 1:
                                user.addAll(Arrays.asList(id));break;
                            case 2:
                                globe.addAll(Arrays.asList(id));break;

                        }
                    }
                }else if(rele_mode==2){
                    if((","+rele_time+",").contains(","+(day)+",")){
                        if (reward.contains("-")){
                            String[] a=reward.split("-");
                            reward="";
                            for (int i=Integer.parseInt(a[0]);i<Integer.parseInt(a[1]);i++){
                                reward+=i+",";
                            }
                            reward=reward.substring(0,reward.length()-1);
                        }
                        String[] id=reward.split(",");

                        switch (mode){
                            case 0:
                                other.addAll(Arrays.asList(id));break;
                            case 1:
                                user.addAll(Arrays.asList(id));break;
                            case 2:
                                globe.addAll(Arrays.asList(id));break;

                        }
                    }
                }else if(rele_mode==4){
                    if((","+rele_time+",").contains(","+(week_num)+",")){
                        if (reward.contains("-")){
                            String[] a=reward.split("-");
                            reward="";
                            for (int i=Integer.parseInt(a[0]);i<Integer.parseInt(a[1]);i++){
                                reward+=i+",";
                            }
                            reward=reward.substring(0,reward.length()-1);
                        }
                        String[] id=reward.split(",");

                        switch (mode){
                            case 0:
                                other.addAll(Arrays.asList(id));break;
                            case 1:
                                user.addAll(Arrays.asList(id));break;
                            case 2:
                                globe.addAll(Arrays.asList(id));break;

                        }
                    }
                }else if(rele_mode==5){
                    if((","+rele_time+",").contains(","+(month_num)+",")){
                        if (reward.contains("-")){
                            String[] a=reward.split("-");
                            reward="";
                            for (int i=Integer.parseInt(a[0]);i<Integer.parseInt(a[1]);i++){
                                reward+=i+",";
                            }
                            reward=reward.substring(0,reward.length()-1);
                        }
                        String[] id=reward.split(",");

                        switch (mode){
                            case 0:
                                other.addAll(Arrays.asList(id));break;
                            case 1:
                                user.addAll(Arrays.asList(id));break;
                            case 2:
                                globe.addAll(Arrays.asList(id));break;

                        }
                    }
                }else if(rele_mode==7){
                    if((","+rele_time+",").contains(","+(num)+",")){
                        if (reward.contains("-")){
                            String[] a=reward.split("-");
                            reward="";
                            for (int i=Integer.parseInt(a[0]);i<Integer.parseInt(a[1]);i++){
                                reward+=i+",";
                            }
                            reward=reward.substring(0,reward.length()-1);
                        }
                        String[] id=reward.split(",");

                        switch (mode){
                            case 0:
                                other.addAll(Arrays.asList(id));break;
                            case 1:
                                user.addAll(Arrays.asList(id));break;
                            case 2:
                                globe.addAll(Arrays.asList(id));break;

                        }
                    }
                }

            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i=0;i<user.size();i++){
            if(!user_new.contains(user.get(i))){
                other.add(user.get(i));
            }
        }
        for (int i=0;i<globe.size();i++){
            if(!globe_new.contains(globe.get(i))){
                other.add(globe.get(i));
            }
        }
        if (other.size()==0)return null;
        int d= (int) (Math.random()*other.size());
        return  other.get(d).toString();

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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getMonth_total() {
        return month_total;
    }

    public void setMonth_total(int month_total) {
        this.month_total = month_total;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getContinuous() {
        return continuous;
    }

    public void setContinuous(int continuous) {
        this.continuous = continuous;
    }

    public int getSupplement() {
        return supplement;
    }

    public void setSupplement(int supplement) {
        this.supplement = supplement;
    }
}
