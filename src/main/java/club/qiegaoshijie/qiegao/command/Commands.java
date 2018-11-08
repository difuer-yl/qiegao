package club.qiegaoshijie.qiegao.command;


import java.util.*;

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
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Commands
{
    public static HashMap<Player, MenuGUI> openinlay = new HashMap();
    private static BukkitTask yh,wsj;





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
        String line="";
        String other="";
        if (a[1].equalsIgnoreCase("糕宠")){
             line=a[1]+"-";
            if(a.length==3){
                other=a[2];
            }
        }else{
            List<String> type=(List<String>) Qiegao.getMessages().getList("tag.type");
            if(type.indexOf(a[2])==-1){
                p.sendMessage("§c种类错误");
                return;
            }
             line=a[1].substring(0,2)+"-"+a[2].substring(0,1);

            if(a.length==4){
                other=a[3];
            }
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
    @Command(value="万圣节活动", possibleArguments="wsj")
    @Cmd(value="wsj", minArgs=1, onlyPlayer=true,permission = "qiegao.wsj")
    public void wsj(DefaultCommand defcmd)
    {
        CommandSender sender = defcmd.getSender();
        Player p = (Player)sender;
        String[] args=defcmd.getArgs();
        String status="start";
        int length=60;
        if (args.length>1){
            status =args[1];
        }
        if (args.length>2){
            length = Integer.parseInt(args[2]);
        }
        if (status.equalsIgnoreCase("start")){
            Config.setIswsj(new Date().getTime()+length*1000);
            wsj=new BukkitRunnable(){
                @Override
                public void run(){
                    if (Config.getIswsj()<(new Date().getTime())){
                        if (!wsj.isCancelled()){
                            wsj.cancel();
                        }
                        for (Player p : Config.playerHashMap.values()) {
                            ItemStack[] its=p.getInventory().getContents();
                            for (int _i=0;_i<its.length;_i++){
                                if (its[_i]==null)continue;
                                Material material=its[_i].getType();
                                if (material==Material.PLAYER_HEAD
                                        ||material==Material.BAKED_POTATO
                                        ||material==Material.IRON_BOOTS
                                        ||material==Material.IRON_CHESTPLATE
                                        ||material==Material.IRON_SWORD
                                        ||material==Material.IRON_LEGGINGS){
                                    its[_i]=null;
                                }
                            }
                            p.getInventory().setContents(its);
                            p.teleport(new Location(p.getWorld(),783,78,1527));
                        }
                        String str="§6[万圣节活动]§r挖松露大赛结束了！\n得分排名如下：\n§6================\n§r";
                        if (Config.fraction.size()==0){
                            str+="无人得分！\n";
                        }else{
                            List<Map.Entry<String, Integer>> infoIds =
                                    new ArrayList<Map.Entry<String, Integer>>(Config.fraction.entrySet());
                            //排序
                            Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
                                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                                    //return (o2.getValue() - o1.getValue());
                                    return (o1.getKey()).toString().compareTo(o2.getKey());
                                }
                            });

                            // 对HashMap中的 value 进行排序后  显示排序结果
                            for (int i = 0; i < infoIds.size(); i++) {
                                str+="第"+(i+1)+"名："+infoIds.get(i).getKey()+" "+infoIds.get(i).getValue()+"\n";
                            }
                        }
                        str+="§6================\n";
                            p.getServer().broadcastMessage(str);
                        Config.setIswsj(0L);
                        Config.playerHashMap=new HashMap<>();
                    }
                }
            }.runTaskTimer(Qiegao.getInstance(),0,10);
            Config.fraction=new HashMap<>();
            p.getServer().broadcastMessage("§6[万圣节活动]§r挖松露大赛开始了！\n本局比赛将于"+length+"秒后结束。");
//            Bukkit.getServer().broadcastMessage("§a[万圣节活动]§r开始啦！");
        }else if(status.equalsIgnoreCase("off")) {

            Config.setIsrun(false);
//            Bukkit.getServer().broadcastMessage("§a[万圣节活动]§r结束！请等待管理统计数据！");
        }else if(status.equalsIgnoreCase("on")){
            Config.setIsrun(true);
        }
    }
    @Command(value="烟花", possibleArguments="yh")
    @Cmd(value="yh", minArgs=1,permission = "qiegao.yh")
    public void yh(DefaultCommand defcmd)
    {
        CommandSender sender = defcmd.getSender();
        Player p = (Player)sender;
        String[] args=defcmd.getArgs();
        String status="start";
        int shu=1;
        int cishu=1;
        int pinlv=1;
        if (args.length>1){
            shu =Integer.valueOf(args[1]);
        }
        if (args.length>2){
            cishu =Integer.valueOf(args[2]);
        }
        if (args.length>3){
            pinlv =Integer.valueOf(args[3]);
        }
        int finalCishu = cishu;
        int finalShu = shu;
        Location loc=p.getLocation();
        yh=new BukkitRunnable() {
            //
            private  int num=0;
            @Override
            public void run() {
                if (num > finalCishu){
//                    this.getTaskId();
                    if (!yh.isCancelled()){
                        yh.cancel();
                    }
                    return;
                }


                for (int j=0;j<finalShu;j++){
                    loc.setX(loc.getX()+j-Math.ceil(j/2));
                    Firework firework = (Firework)loc.getWorld().spawn(loc, Firework.class);
                    FireworkMeta meta = firework.getFireworkMeta();
                    FireworkEffect.Builder effect=FireworkEffect.builder();
                    switch ((int) (Math.random()*5)){
                        case 0:
                            effect=effect.with(FireworkEffect.Type.STAR);break;
                        case 1:
                            effect=effect.with(FireworkEffect.Type.BALL);break;
                        case 2:
                            effect=effect.with(FireworkEffect.Type.BALL_LARGE);break;
                        case 3:
                            effect=effect.with(FireworkEffect.Type.BURST);break;
                        case 4:
                            effect=effect.with(FireworkEffect.Type.CREEPER);break;
                    }
                    int color_num= (int) (Math.random()*4);
                    int[] color_array=new int[color_num+1];
                    for (int i=0;i<=color_num;i++){
                        color_array[i]=(int) (Math.random()*17);
                    }
                    for (int color : color_array) {
                        switch (color){
                            case 0:effect.withColor(Color.AQUA);break;
                            case 1:effect.withColor(Color.BLACK);break;
                            case 2:effect.withColor(Color.BLUE);break;
                            case 3:effect.withColor(Color.FUCHSIA);break;
                            case 4:effect.withColor(Color.GRAY);break;
                            case 5:effect.withColor(Color.GREEN);break;
                            case 6:effect.withColor(Color.LIME);break;
                            case 7:effect.withColor(Color.MAROON);break;
                            case 8:effect.withColor(Color.NAVY);break;
                            case 9:effect.withColor(Color.OLIVE);break;
                            case 10:effect.withColor(Color.ORANGE);break;
                            case 11:effect.withColor(Color.PURPLE);break;
                            case 12:effect.withColor(Color.RED);break;
                            case 13:effect.withColor(Color.SILVER);break;
                            case 14:effect.withColor(Color.TEAL);break;
                            case 15:effect.withColor(Color.WHITE);break;
                            case 16:effect.withColor(Color.YELLOW);break;
                        }
                    }
                    if (Math.random()>0.5){
                        effect = effect.withTrail();
                    }
                    if (Math.random()>0.5){
                        effect=effect.withFlicker();
                    }
                    if (Math.random()>0.5){
                        effect = effect.withFade(Color.BLUE);
                        int fade_num= (int) (Math.random()*4);
                        int[] fade_array=new int[fade_num+1];
                        for (int i=0;i<=fade_num;i++){
                            fade_array[i]=(int) (Math.random()*17);
                        }
                        for (int color : fade_array) {
                            switch (color){
                                case 0:effect.withFade(Color.AQUA);break;
                                case 1:effect.withFade(Color.BLACK);break;
                                case 2:effect.withFade(Color.BLUE);break;
                                case 3:effect.withFade(Color.FUCHSIA);break;
                                case 4:effect.withFade(Color.GRAY);break;
                                case 5:effect.withFade(Color.GREEN);break;
                                case 6:effect.withFade(Color.LIME);break;
                                case 7:effect.withFade(Color.MAROON);break;
                                case 8:effect.withFade(Color.NAVY);break;
                                case 9:effect.withFade(Color.OLIVE);break;
                                case 10:effect.withFade(Color.ORANGE);break;
                                case 11:effect.withFade(Color.PURPLE);break;
                                case 12:effect.withFade(Color.RED);break;
                                case 13:effect.withFade(Color.SILVER);break;
                                case 14:effect.withFade(Color.TEAL);break;
                                case 15:effect.withFade(Color.WHITE);break;
                                case 16:effect.withFade(Color.YELLOW);break;
                            }
                        }
                    }

                    meta.addEffect(effect.build());
                    meta.setPower(2);
                    firework.setFireworkMeta(meta);

                }

                num++;
            }
        }.runTaskTimer(Qiegao.getInstance(),5,20*pinlv);
    }
    @Command(value="资源包加载", possibleArguments="zyb")
    @Cmd(value="zyb", minArgs=1, onlyPlayer=true)
    public void zyb(DefaultCommand defcmd)
    {
        CommandSender sender = defcmd.getSender();
        Player p = (Player)sender;
        p.setResourcePack("https://qiegao-1252250917.cos.ap-guangzhou.myqcloud.com/QiegaoWorld_base.zip",Tools.toBytes("76adc7d7491dfc6eed29f37b7ee8061c0efb4f25"));
    }





}

