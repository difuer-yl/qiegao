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

        setTableName("QieGaoWorld_signin");

    }
    public Signin(String username){
        setTableName("QieGaoWorld_signin");
        updateDate(username);
    }

    private void updateDate(String username){
        setYear(Calendar.getInstance().get(Calendar.YEAR));
        setMonth(Calendar.getInstance().get(Calendar.MONTH)+1);
        setDay(_now);

        setUsername(username);
        Signin signin= (Signin) this.where("username='"+username+"'").order("time","DESC").find();
        if (signin==null||signin.getTotal()==0){
            int total=this.where("username='"+username+"'").count();
            if (total==0){
                setTotal(1);
            }else{
                setTotal(total);
            }
            int month_total=this.where("username='"+username+"' and year="+getYear() +" and month="+getMonth()).count();

            if (month_total==0){
                setMonth_total(0);
            }else{
                setMonth_total(month_total);
            }
            updateContinuous();

            setSupplement(getContinuous()/7);
        }else{
            setTotal(signin.getTotal());
            if (getYear() == signin.getYear()&&getMonth()==signin.getMonth()){
                setMonth_total(signin.getMonth_total());
                setContinuous(signin.getContinuous());
            }else{
                int month_total=this.where("username='"+username+"' and year="+getYear() +" and month="+getMonth()).count();

                if (month_total==0){
                    setMonth_total(0);
                }else{
                    setMonth_total(month_total);
                }
                setContinuous(0);
                setSupplement(0);
            }
            setSupplement(signin.getSupplement());
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

        List list= this.where("username='"+getUsername()+"' and year="+getYear()+" and month="+getMonth()).order("day","DESC").select();

        for (Object object :list ) {
            da=((Signin) object).getDay();
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
    }

    private   void add(Player p,int day){
        String username=p.getName();
        updateDate(username);
        setDay(day);
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
        setReward(id);
        if(insert()){
            p.sendMessage("签到成功！");
            if (getReward()!=null){
                ItemStack mp=new ItemStack(Material.FILLED_MAP);
                MapMeta mm= (MapMeta) mp.getItemMeta();
                ArrayList<String> ll = new ArrayList<>();
                ll.add(getMonth()+"月签到活动道具");
                ll.add("唯一");
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

    }
    public  String getReward(String  username){
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
        String date="";
        String _day=""+day;
        String _month=month+"";
        if (day<10){
            _day="0"+_day;
        }
        if (month<10){
            _month="0"+_month;
        }
        date=""+year+"-"+_month+"-"+_day;
        week_num=this.where("  username='"+username+"' and year="+year +" and month="+month+" and day >"+(day-week)).count();

        //查询全局奖励发放情况
        List list=this.where("reward !='null'").select();
        for (Object object :list) {
            globe_new.add(((Signin) object).getReward());
        }
        //查询用户奖励发放情况
        list=this.where("reward !='null' and username='"+username+"'").select();
        for (Object object :list) {
            user_new.add(((Signin) object).getReward());
        }
        //查询限时奖励发放情况
        Reward reward=new Reward();
        list=reward.where("(start_time=='' or (start_time<='"+date+"' and end_time >='"+date+"'))").select();
        String reward_id="",rele_time="",start_time="",end_time="",_sql="";
        int rele_mode=0,mode=0;
        for (Object object :list) {
            reward = (Reward) object;
            reward_id = reward.getReward_id();
            rele_mode = reward.getRelease_mode();
            mode = reward.getMode();
            rele_time = reward.getRelease_time();
            start_time = reward.getStart_time();
            end_time = reward.getEnd_time();
            _sql = "";
            String[] id = null;
            if (rele_mode == 0) {
                id = formatRewardId(reward_id);
            } else {
                String tmp = "";
                switch (rele_mode) {
                    case 1:
                        tmp = week + 1 + "";
                        break;
                    case 2:
                        tmp = day + "";
                        break;
                    case 3:
                    case 4:
                        tmp = week_num + "";
                        break;
                    case 5:
                        tmp = month_num + "";
                        break;
                    case 6:
                    case 7:
                        tmp = num + "";
                        break;
                    case 8:
                    case 9:
                        int _num = this.where("username='" + username + "' and " +
                                "((month>=10 and (day>=10  and date(year||\"-\"||month||\"-\"||day)>=date('" + start_time + "')) " +
                                "or ( date(year||\"-\"||month||\"-0\"||day)>=date('" + start_time + "'))" +
                                "or (day>=10  and date(year||\"-0\"||month||\"-\"||day)>=date('" + start_time + "')) or ( date(year||\"-0\"||month||\"-0\"||day)>=date('" + start_time + "')) ) )").count();
                        tmp = _num + 1 + "";
                        break;
                }
                if (("," + rele_time + ",").contains("," + tmp + ",")) {
                    id = formatRewardId(reward_id);
                }
            }
            switch (mode) {
                case 0:
                    other.addAll(Arrays.asList(id));
                    break;
                case 1:
                    user.addAll(Arrays.asList(id));
                    break;
                case 2:
                    globe.addAll(Arrays.asList(id));
                    break;

            }

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
    private String createReward(){

        return "";
    }

    public String[] formatRewardId(String reward_id){
        if (reward_id.contains("-")){
            String[] a=reward_id.split("-");
            reward_id="";
            for (int i=Integer.parseInt(a[0]);i<=Integer.parseInt(a[1]);i++){
                reward_id+=i+",";
            }
            reward_id=reward_id.substring(0,reward_id.length()-1);
        }
        String[] id=reward_id.split(",");
        return id;
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

    public void setYear(Integer year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public int getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public int getMonth_total() {
        return month_total;
    }

    public void setMonth_total(Integer month_total) {
        this.month_total = month_total;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public int getContinuous() {
        return continuous;
    }

    public void setContinuous(Integer continuous) {
        this.continuous = continuous;
    }

    public int getSupplement() {
        return supplement;
    }

    public void setSupplement(Integer supplement) {
        this.supplement = supplement;
    }
}
