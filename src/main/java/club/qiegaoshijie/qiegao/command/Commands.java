package club.qiegaoshijie.qiegao.command;


import java.util.HashMap;
import java.util.List;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.command.annotations.Cmd;
import club.qiegaoshijie.qiegao.command.annotations.Command;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.config.Messages;

import club.qiegaoshijie.qiegao.inventory.*;
import club.qiegaoshijie.qiegao.models.DeclareAnimals;
import club.qiegaoshijie.qiegao.models.Skull;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import club.qiegaoshijie.qiegao.util.sqlite.SqliteManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.util.Vector;

public class Commands
{
    public static HashMap<Player, MenuGUI> openinlay = new HashMap();



    @Command(value="主菜单", possibleArguments="menu")
    @Cmd(value="menu", minArgs=1, onlyPlayer=true)
    public void menu(DefaultCommand defcmd)
    {
        CommandSender sender = defcmd.getSender();
        Player p = (Player)sender;
        MenuGUI GUI = new MenuGUI();
        p.openInventory(GUI.getGUI());
        openinlay.put(p, GUI);
    }
    @Command(value="任务列表", possibleArguments="task")
    @Cmd(value="task", minArgs=1, onlyPlayer=true)
    public void task(DefaultCommand defcmd)
    {
        CommandSender sender = defcmd.getSender();
        Player p = (Player)sender;
        TaskGUI GUI = new TaskGUI(1);
        p.openInventory(GUI.getGUI());
    }

    @Command(value="动物上牌", possibleArguments="tag")
    @Cmd(value="tag", minArgs=1, onlyPlayer=true)
    public void tag(DefaultCommand defcmd)
    {
        String[] a=defcmd.getArgs();
        Player p= (Player) defcmd.getSender();
        int fi=p.getInventory().firstEmpty();
        if(fi== -1){
            p.sendMessage("§c背包已满，请保留至少一个空格");
            return;
        }
        if(a.length<3) {
            p.sendMessage("§c请输入完整命令");
            return;
        }
        List<String> area= (List<String>) Qiegao.getMessages().getList("tag.area");
        if(area.indexOf(a[1])==-1){
            p.sendMessage("§c区域错误");
            return;
        }
        List<String> type=(List<String>) Qiegao.getMessages().getList("tag.type");
        if(type.indexOf(a[2])==-1){
            p.sendMessage("§c种类错误");
            return;
        }
        String line=a[1]+"-"+a[2].substring(0,1);
        String other="";
        if(a.length==4){
            other=a[3];
        }
        DeclareAnimals da=new DeclareAnimals();
        if(other=="共享"){
            da.setBinding("public");
        }else{
            da.setBinding(p.getPlayerListName());
        }
        List<String> list=da.getList(line);
        String license="";
        for (int i=1; ;i++){
            license=String.format(line+"%03d",i);
            if(!list.contains(license))break;
            if(i>999){
                p.sendMessage("§c牌照生成失败！请联系管理员");
                return;
            }
        }

        da.setLicense(license+ " " +other);
        da.setStatus(0);
        da.setUsername(p.getName());
        da.setDeclare_time(Tools.getTime());
        da.setFeature("");
        Boolean b=da.insert();
        if (b){
            ItemStack animal = new ItemStack(Material.NAME_TAG, 1);
            ItemMeta animalmeta = animal.getItemMeta();
            animalmeta.setDisplayName(license +" "+other);
            animalmeta.setLore((List<String>) Qiegao.getMessages().getList("animal"));
            animal.setItemMeta(animalmeta);
            p.getInventory().setItem(fi,animal);
            p.sendMessage("牌照生成成功！请查看背包！");
        }else{
            p.sendMessage("§c牌照生成失败！请联系管理员");
        }
    }

    @Command(value="版本信息", possibleArguments="version")
    @Cmd(value="version", minArgs=1, onlyPlayer=true)
    public void version(DefaultCommand defcmd)
    {
        CommandSender sender = defcmd.getSender();
        Player p = (Player)sender;
        p.sendMessage("当前版本："+Config.getString("version"));
    }

    @Command(value="生成地图", possibleArguments="map")
    @Cmd(value="map", minArgs=1, onlyPlayer=true,permission = "qiegao.map")
    public void map(DefaultCommand defcmd)
    {
        CommandSender sender = defcmd.getSender();
        Player p = (Player)sender;
        String[] args=defcmd.getArgs();
        int num=1;
        if(args.length>2){
            num= Integer.parseInt(args[2]);
        }
        ItemStack mp=new ItemStack(Material.FILLED_MAP,num);
        MapMeta mm= (MapMeta) mp.getItemMeta();
        mm.setMapId(Integer.parseInt(args[1]));
        mp.setItemMeta(mm);
        p.getInventory().addItem(mp);
        p.sendMessage("生成成功！打开背包查看");

    }
    @Command(value="生成头颅", possibleArguments="skull")
    @Cmd(value="skull", minArgs=1, onlyPlayer=true,permission = "qiegao.skull")
    public void skull(DefaultCommand defcmd)
    {
        CommandSender sender = defcmd.getSender();
        Player p = (Player)sender;
        String[] args=defcmd.getArgs();
        int num=1;
        if(args.length>2){
            num= Integer.parseInt(args[2]);
        }
        if(args.length <2){
            return ;
        }
        Skull skull=new Skull();
        List a= skull.getSkull(Integer.parseInt(args[1]));
        if (a==null || a.size()==0){
            return;
        }


        Inventory pi=p.getInventory();
        for (Object i: a) {
            ItemStack it= (ItemStack) i;
            if((pi.firstEmpty())!=-1){
                pi.addItem((ItemStack) it);
            }else{
                p.getWorld().dropItem(p.getLocation(),  it);
            }
        }
        p.sendMessage("生成成功！打开背包查看");

    }
    @Command(value="测试用例", possibleArguments="test")
    @Cmd(value="test", minArgs=1, onlyPlayer=true,permission = "qiegao.test")
    public void test(DefaultCommand defcmd)
    {
        CommandSender sender = defcmd.getSender();
        Player p = (Player)sender;
        String[] args=defcmd.getArgs();
        ItemStack displayItem=new ItemStack(Material.EGG);
        Location loc=p.getLocation();
        loc.setZ(loc.getZ()+2);
        Item reward = p.getWorld().dropItem(loc.clone().add(.5, 1, .5), displayItem);
        reward.setVelocity(new Vector(0, .0, 0));
        reward.setCustomName("这是一个蛋");
        reward.setCustomNameVisible(true);
        reward.setPickupDelay(Integer.MAX_VALUE);

    }





}

