package club.qiegaoshijie.qiegao.inventory;

import club.qiegaoshijie.qiegao.config.Messages;
import club.qiegaoshijie.qiegao.models.Signin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SigninGUI extends BaseGUI
{
    private ItemStack item;
    private Inventory GUI;
    private String username;


    public SigninGUI(){
        setObj(this);
    }

    public void createGUI(String username)
    {
        this.username=username;
         GUI = Bukkit.createInventory(null, 36, "每日签到");

        ItemStack map;
        ItemMeta im;
        Signin si=new Signin();
        List<String> lore=new ArrayList<>();
        lore.add("§7"+ Messages.GUI_SIGNIN_TITLE);
        int maxday=Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        int month=Calendar.getInstance().get(Calendar.MONTH)+1;
        int year=Calendar.getInstance().get(Calendar.YEAR);
        for (int i=1;i<=maxday;i++){
            map=new ItemStack(Material.PAPER,i);
            im=map.getItemMeta();
            im.setDisplayName(month+"月"+(i)+"日");
            im.setLore(lore);
            map.setItemMeta(im);
            GUI.addItem(map);
        }
        Signin signin=new Signin();
        List l=signin.where("username= '"+username+"' and year="+year+" and month="+month).select();
//        List l=si.getList("select * from QieGaoWorld_signin where username= '"+username+"' and year="+year+" and month="+month);
        for (Object ll: l) {
            Signin s= (Signin) ll;
            map=new ItemStack(Material.MAP,s.getDay());
            ItemMeta itm=map.getItemMeta();
            itm.setDisplayName(month+"月"+s.getDay()+"日");
            itm.setLore(lore);
            map.setItemMeta(itm);
            GUI.setItem(s.getDay()-1,map);
        }
        ItemStack total=new ItemStack(Material.DIAMOND);
        ArrayList<String> a=new ArrayList<>();
        a.add("§7每日签到");
        a.add("§7§m========================");
        a.add("§r本月签到次数： "+Signin.getSignin(username).getMonth_total());
        a.add("§7§m========================");
        a.add("§r总签到次数： "+Signin.getSignin(username).getTotal());
        a.add("§7§m========================");
        a.add("§r连续签到次数： "+Signin.getSignin(username).getContinuous());
        a.add("§7§m========================");
        a.add("§r剩余补签次数： "+Signin.getSignin(username).getSupplement());
        a.add("§7§m========================");
        ItemMeta tim=total.getItemMeta();
        tim.setDisplayName("§r签到记录");
        tim.setLore(a);
        total.setItemMeta(tim);
        GUI.setItem(35,total);

        total=new ItemStack(Material.BOOK);
        tim=total.getItemMeta();
        tim.setDisplayName("§r签到规则");
        a=new ArrayList<>();
        a.add("§7每日签到");
        a.add("§r每日签到可累计签到次数");
        a.add("§r连续签到获取连续签到次数");
        a.add("§r断签连续签到次数将归零");
        tim.setLore(a);
        total.setItemMeta(tim);
        GUI.setItem(33,total);
        total=new ItemStack(Material.BOOK);
        tim=total.getItemMeta();
        tim.setDisplayName("§r补签规则");
        a=new ArrayList<>();
        a.add("§7每日签到");
        a.add("§r当月连续签到7天，奖励一次补签");
        a.add("§r后续依此");
        a.add("§r补签次数当月有效，过期作废");
        a.add("§r每次补签消耗一次补签次数");
        tim.setLore(a);
        total.setItemMeta(tim);
        GUI.setItem(34,total);
        setGUI(username,this);

    }

    public void updateTotal(int day){
        ItemStack total=new ItemStack(Material.DIAMOND);
        ArrayList<String> a=new ArrayList<>();
        a.add("§7每日签到");
        a.add("§7§m========================");
        a.add("§r本月签到次数： "+Signin.getSignin(username).getMonth_total());
        a.add("§7§m========================");
        a.add("§r总签到次数： "+Signin.getSignin(username).getTotal());
        a.add("§7§m========================");
        a.add("§r连续签到次数： "+Signin.getSignin(username).getContinuous());
        a.add("§7§m========================");
        a.add("§r剩余补签次数： "+Signin.getSignin(username).getSupplement());
        a.add("§7§m========================");
        ItemMeta tim=total.getItemMeta();
        tim.setDisplayName("§r签到记录");
        tim.setLore(a);
        total.setItemMeta(tim);

        this.GUI.setItem(35,total);

        //更新签到日图标
        ItemStack d=this.GUI.getItem(day-1);
        d.setType(Material.MAP);
        this.GUI.setItem(day-1,d);
        //更新GUI
        SigninGUI.setGUI(username,this);

    }

    public static void event(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        ItemStack citem = e.getCurrentItem();

        if (citem.getType()!=Material.PAPER && citem.getType()!=Material.MAP){
            return;
        }
        int day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        if (day<e.getRawSlot()+1){
            p.sendMessage("你是来自未来的穿越者吗？");
            return;
        }

        if (citem.getType()==Material.MAP){
            p.sendMessage("该日已签");
            return;
        }


        if(day != e.getRawSlot()+1){
            if (Signin.getSignin(p.getName()).getSupplement()<=0){
                p.sendMessage("补签次数不足");
                return;
            }else{
                Signin.signin(p,e.getRawSlot()+1);
            }
        }else{
            Signin.signin(p);
        }




        //刷新GUI
//                p.closeInventory();
        Inventory inv=((SigninGUI)SigninGUI.getGUI(p.getName())).getGui();
        p.openInventory(inv);
    }

    public Inventory getGui(){
        return  this.GUI;
    }

    public ItemStack getItem()
    {
        return this.item;
    }
}

