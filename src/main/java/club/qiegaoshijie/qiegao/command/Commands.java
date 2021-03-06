package club.qiegaoshijie.qiegao.command;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Comparator;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.command.annotations.Cmd;
import club.qiegaoshijie.qiegao.command.annotations.Command;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.config.Messages;

import club.qiegaoshijie.qiegao.inventory.*;
import club.qiegaoshijie.qiegao.models.DeclareAnimals;
import club.qiegaoshijie.qiegao.models.Skull;
import club.qiegaoshijie.qiegao.runnable.AutoBackupTask;
import club.qiegaoshijie.qiegao.runnable.MessageTask;
import club.qiegaoshijie.qiegao.util.HttpServer;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.dynmap.bukkit.DynmapPlugin;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerSet;

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
    @Cmd(value="version", minArgs=1)
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
        ItemStack it= skull.getSkull(Integer.parseInt(args[1]));
        if (it==null ){
            p.sendMessage("生成失败！对应数据不存在！");
            return;
        }


        Inventory pi=p.getInventory();
        if((pi.firstEmpty())!=-1){
            pi.addItem((ItemStack) it);
        }else{
            p.getWorld().dropItem(p.getLocation(),  it);
        }
        p.sendMessage("生成成功！打开背包查看");

    }
    @Command(value="测试用例", possibleArguments="test")
    @Cmd(value="test", minArgs=1, onlyPlayer=true,permission = "qiegao.test")
    public void test(DefaultCommand defcmd)
    {
        Log.toConsole("1111");
        CommandSender sender = defcmd.getSender();
        Log.toConsole("1111");
        Player p = (Player)sender;
        Log.toConsole("1111");
//        List<String> user=Tools.getSdjPlayer();
//        for (String u : user) {
//            Log.toPlayer(p, u, true);
//        }

        //重置当前区块
//        Location location=p.getLocation();
//        Chunk chunk=location.getChunk();
//        Objects.requireNonNull(Bukkit.getWorld("world")).regenerateChunk(chunk.getX(),chunk.getZ());
//        World world=p.getWorld();
//        Log.toConsole("3");
////        Long s=.getSeed();
//        ChunkGenerator chunkGenerator=Bukkit.getPluginManager().getPlugin("qiegao").getDefaultWorldGenerator(null,null);
////        ChunkGenerator chunkGenerator=world.getGenerator();
////        ChunkGenerator chunkGenerator=Bukkit.getP
//        Log.toConsole("4");
//        Log.toConsole((chunk==null)+"");
//        Log.toConsole((chunkGenerator==null)+"");
//        ChunkGenerator.ChunkData chunkData=chunkGenerator.generateChunkData(world,new Random(world.getSeed()),chunk.getX(),chunk.getZ(),new ChunkGenerator.BiomeGrid()
//        {
//            public void setBiome(int var1, int var2, Biome var3)
//            {
//                chunk.getWorld().setBiome(var1, var2, var3);
//            }
//
//            public Biome getBiome(int var1, int var2)
//            {
//                return chunk.getWorld().getBiome(var1, var2);
//            }
//        });
//        Log.toConsole("5");
//        for (int x = 0; x < 16; x++) {
//            for (int y = 0; y < p.getWorld().getMaxHeight(); y++) {
//                for (int z = 0; z < 16; z++)
//                {
//                    Block target = chunk.getBlock(x, y, z);
////                    Legacy.BlockDataLegacy block = chunkData.getBlock(x, y, z);
//                    if ((target != null) )
//                    {
//                        BlockState state = target.getState();
//                        state.setType(chunkData.getType(x,y,z));
//                        state.setBlockData(chunkData.getBlockData(x,y,z));
//                        state.update(true);
//                    }
//                }
//            }
//        }
//        ItemStack itemStack=new ItemStack(Material.KNOWLEDGE_BOOK);
////        MaterialData materialData=new MaterialData();
//
//        KnowledgeBookMeta itemMeta= (KnowledgeBookMeta) itemStack.getItemMeta();
//        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//        itemMeta.addEnchant(Enchantment.DURABILITY,1,false);
////        itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR,new AttributeModifier("type",111,AttributeModifier.Operation.ADD_NUMBER));
////        net.minecraft.server.v1_13_R2
//
//        ItemStack itemStack1=new ItemStack(Material.POTION);
//        PotionMeta potionMeta= (PotionMeta) itemStack1.getItemMeta();
////        p.add
//        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS,100,1),false);
//        itemStack1.setItemMeta(potionMeta);
////        ShapedRecipe s1 = new ShapedRecipe(new NamespacedKey(Qiegao.getInstance(), "Qiegao"),itemStack1);
////        s1.shape(new String[]{"mmm","t"+"t"+"t","mmm"});
////        s1.setIngredient('m',Material.WHEAT);
////        s1.setIngredient('t',Material.SUGAR);
////        s1.setGroup("qiegao");
//        List<NamespacedKey> list=new ArrayList<>();
//        list.add(new NamespacedKey(Qiegao.getInstance(), "Qiegao"));
//        itemMeta.setRecipes(list);
//
//
//        itemStack.setItemMeta(itemMeta);
//        p.getInventory().addItem(itemStack);

    }
//    @Command(value="万圣节活动", possibleArguments="wsj")
//    @Cmd(value="wsj", minArgs=1, onlyPlayer=true,permission = "qiegao.wsj")
//    public void wsj(DefaultCommand defcmd)
//    {
//        CommandSender sender = defcmd.getSender();
//        Player p = (Player)sender;
//        String[] args=defcmd.getArgs();
//        String status="start";
//        int length=60;
//        if (args.length>1){
//            status =args[1];
//        }
//        if (args.length>2){
//            length = Integer.parseInt(args[2]);
//        }
//        if (status.equalsIgnoreCase("start")){
//            Config.setIswsj(new Date().getTime()+length*1000);
//            wsj=new BukkitRunnable(){
//                @Override
//                public void run(){
//                    if (Config.getIswsj()<(new Date().getTime())){
//                        if (!wsj.isCancelled()){
//                            wsj.cancel();
//                        }
//                        for (Player p : Config.playerHashMap.values()) {
//                            ItemStack[] its=p.getInventory().getContents();
//                            for (int _i=0;_i<its.length;_i++){
//                                if (its[_i]==null)continue;
//                                Material material=its[_i].getType();
//                                if (material==Material.PLAYER_HEAD
//                                        ||material==Material.BAKED_POTATO
//                                        ||material==Material.IRON_BOOTS
//                                        ||material==Material.IRON_CHESTPLATE
//                                        ||material==Material.IRON_SWORD
//                                        ||material==Material.IRON_LEGGINGS){
//                                    its[_i]=null;
//                                }
//                            }
//                            p.getInventory().setContents(its);
//                            p.teleport(new Location(p.getWorld(),783,78,1527));
//                        }
//                        String str="§6[万圣节活动]§r挖松露大赛结束了！\n得分排名如下：\n§6================\n§r";
//                        if (Config.fraction.size()==0){
//                            str+="无人得分！\n";
//                        }else{
//                            List<Map.Entry<String, Integer>> infoIds =
//                                    new ArrayList<Map.Entry<String, Integer>>(Config.fraction.entrySet());
//                            //排序
//                            Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
//                                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
//                                    //return (o2.getValue() - o1.getValue());
//                                    return (o1.getKey()).toString().compareTo(o2.getKey());
//                                }
//                            });
//
//                            // 对HashMap中的 value 进行排序后  显示排序结果
//                            for (int i = 0; i < infoIds.size(); i++) {
//                                str+="第"+(i+1)+"名："+infoIds.get(i).getKey()+" "+infoIds.get(i).getValue()+"\n";
//                            }
//                        }
//                        str+="§6================\n";
//                            p.getServer().broadcastMessage(str);
//                        Config.setIswsj(0L);
//                        Config.playerHashMap=new HashMap<>();
//                    }
//                }
//            }.runTaskTimer(Qiegao.getInstance(),0,10);
//            Config.fraction=new HashMap<>();
//            p.getServer().broadcastMessage("§6[万圣节活动]§r挖松露大赛开始了！\n本局比赛将于"+length+"秒后结束。");
////            Bukkit.getServer().broadcastMessage("§a[万圣节活动]§r开始啦！");
//        }else if(status.equalsIgnoreCase("off")) {
//
//            Config.setIsrun(false);
////            Bukkit.getServer().broadcastMessage("§a[万圣节活动]§r结束！请等待管理统计数据！");
//        }else if(status.equalsIgnoreCase("on")){
//            Config.setIsrun(true);
//        }
//    }
    @Command(value="烟花", possibleArguments="yh")
    @Cmd(value="yh", minArgs=1,onlyPlayer=true,permission = "qiegao.yh")
    public void yh(DefaultCommand defcmd) {
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
    public void zyb(DefaultCommand defcmd) {
        CommandSender sender = defcmd.getSender();
        Player p = (Player)sender;
        p.setResourcePack("https://qiegao-1252250917.cos.ap-guangzhou.myqcloud.com/QiegaoWorld_base.zip",Tools.toBytes("76adc7d7491dfc6eed29f37b7ee8061c0efb4f25"));
    }
    @Command(value="ping", possibleArguments="ping")
    @Cmd(value="ping", minArgs=1, onlyPlayer=true,permission = "qiegao.speak")
    public void ping(DefaultCommand defcmd)  {
        HashMap<Player,Integer> ping=new HashMap<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            ping.put(p,Tools.getPing(p));
        }
        List<Map.Entry<Player, Integer>> infoIds =
                new ArrayList<Map.Entry<Player, Integer>>(ping.entrySet());
        //排序
        Collections.sort(infoIds, new Comparator<Map.Entry<Player, Integer>>() {
            public int compare(Map.Entry<Player, Integer> o1, Map.Entry<Player, Integer> o2) {
                //return (o2.getValue() - o1.getValue());
                return (o1.getValue()).toString().compareTo(o2.getValue().toString());
            }
        });
        String str="";
        // 对HashMap中的 value 进行排序后  显示排序结果
        int count=0;
        String color=null;
        for (int i = 0; i < infoIds.size(); i++) {
            int p=infoIds.get(i).getValue();
            if (p <= 100) {
                color = "§a";
            } else if (p <= 200) {
                color = "§e";
            } else {
                color = "§c";
            }
            count+=p;
            str+="§r"+(i+1)+"、"+infoIds.get(i).getKey().getPlayerListName()+" "+color+p+"ms\n";
        }
        int avg=count/ping.size();
        if ( avg<= 100) {
            color = "§a";
        } else if (avg <= 200) {
            color = "§e";
        } else {
            color = "§c";
        }
        str+="当前服务器平均ping值"+" "+color+avg+"ms  \n";
        Log.toSender(defcmd.getSender(),str,false);
    }
    @Command(value="语录", possibleArguments="speak")
    @Cmd(value="speak", minArgs=2, onlyPlayer=true,permission = "qiegao.speak")
    public void speak(DefaultCommand defcmd)  {
        String[] args=defcmd.getArgs();
        if (args[1].equalsIgnoreCase("list")){
            String str="";
            List<String> text = null,quotations=null;
            text= (List<String>) Qiegao.getMessages().getList("speak.text",text);
            quotations= (List<String>) Qiegao.getMessages().getList("speak.quotations",quotations);
            for (int i=0;i<text.size();i++) {
                str+=(i+1)+"、替换文本：\n"+text.get(i)+"\n";
                if (quotations.size()<(i+1)){

                }else{
                    str+="语录：\n"+quotations.get(i)+"\n";
                }
            }
            Log.toSender(defcmd.getSender(),str,false);
        }else if (args[1].equalsIgnoreCase("add")){
            if (args.length<4){
                Log.toSender(defcmd.getSender(),"参数错误",false);
            }else{
                List<String> text = null,quotations=null;
                text= (List<String>) Qiegao.getMessages().getList("speak.text",text);
                quotations= (List<String>) Qiegao.getMessages().getList("speak.quotations",quotations);
                text.add(args[2]);
                quotations.add(args[3]);
                Qiegao.getMessages().set("speak.text",text);
                Qiegao.getMessages().set("speak.quotations",quotations);
                Qiegao.getMessages().save();
                Log.toSender(defcmd.getSender(),"添加成功！",true);
            }
        }else if (args[1].equalsIgnoreCase("del")){
            if (args.length<3){
                Log.toSender(defcmd.getSender(),"参数错误",false);
            }else{
                List<String> text = null,quotations=null;
                text= (List<String>) Qiegao.getMessages().getList("speak.text",text);
                quotations= (List<String>) Qiegao.getMessages().getList("speak.quotations",quotations);
                if (text.size()>=Integer.valueOf(args[2])){
                    text.remove(Integer.valueOf(args[2])-1);

                }
                if (quotations.size()>=Integer.valueOf(args[2])){
                    quotations.remove(Integer.valueOf(args[2])-1);

                }
                Qiegao.getMessages().set("speak.text",text);
                Qiegao.getMessages().set("speak.quotations",quotations);
                Qiegao.getMessages().save();
                Log.toSender(defcmd.getSender(),"删除成功！",true);
            }

        }
    }
//    @Command(value="圣诞节", possibleArguments="sdj")
//    @Cmd(value="sdj", minArgs=1,permission = "qiegao.sdj")
//    public void sdj(DefaultCommand defcmd)  {
//        String[] args=defcmd.getArgs();
//        if (args.length==1){
//            Log.toSender(defcmd.getSender(),"当前状态："+Config.getSdjStatus(),true);
//        }else if (args[1].equalsIgnoreCase("status")){
//            if (args.length<3){
//                Log.toSender(defcmd.getSender(),"当前状态："+Config.getSdjStatus(),true);
//            }else{
//                Config.setSdjStatus(Integer.valueOf(args[2]));
//                Qiegao.getPluginConfig().set("sdjStatus",Integer.valueOf(args[2]));
//            }
//        }
//    }
    @Command(value="统计地图id", possibleArguments="mapid")
    @Cmd(value="mapid", minArgs=1,permission = "qiegao.mapid")
    public void mapid(DefaultCommand defcmd)  {
        String[] args=defcmd.getArgs();
        Location location=null;
//        BoundingBox boundingBox=new BoundingBox(Integer.valueOf(args[1]),Integer.valueOf(args[1]),Integer.valueOf(args[1]),Integer.valueOf(args[1]),Integer.valueOf(args[1]),Integer.valueOf(args[1]));
        Player player= (Player) defcmd.getSender();
        Collection<Entity> entityList= Bukkit.getWorld("world").getNearbyEntities(player.getLocation(),10,1,10);

        List<Integer> id_=new ArrayList<>();
        List<Integer> id=new ArrayList<>();
        for (Entity entity : entityList) {
            if (!entity.getType().equals(EntityType.ITEM_FRAME)){
                continue;
            }
            ItemFrame itemFrame= (ItemFrame) entity;
            ItemStack itemStack=itemFrame.getItem();
            if (itemStack==null || !itemStack.getType().equals(Material.FILLED_MAP)){
                continue;
            }
            MapMeta mapMeta= (MapMeta) itemStack.getItemMeta();
            id_.add(mapMeta.getMapId());
        }
        Collections.sort(id_);
        if (args.length>=2){
            String[] fanwei=args[1].split("-");
            for (int i=Integer.valueOf(fanwei[0]);i<=Integer.valueOf(fanwei[1]);i++){
                if (!id_.contains(i)){
                    id.add(i);
                }
            }
        }else{
            id=id_;
        }

        if (args.length==3&&args[2].equalsIgnoreCase("true")){
            for (int i : id) {
                ItemStack mp=new ItemStack(Material.FILLED_MAP);
                MapMeta mm= (MapMeta) mp.getItemMeta();
                mm.setMapId((i));
                mp.setItemMeta(mm);
                if(player.getInventory().firstEmpty()!=-1){
                    player.getInventory().addItem(mp);
                }else{
                    player.getWorld().dropItem(player.getLocation(),mp);
                }
            }

        }

        Log.toPlayer(player,id.toString(),true);
    }@Command(value="qq聊天内容监听状态", possibleArguments="qqbot")
    @Cmd(value="qqbot", minArgs=1,permission = "qiegao.qqbot")
    public void qqbot(DefaultCommand defcmd)  {
        String[] args=defcmd.getArgs();
        if (args.length==2){
            if (args[1].equalsIgnoreCase("off")){
                Qiegao.getPluginConfig().set("qqbot",false);
                Qiegao.getInstance().getQqServer().close();
                if (!Qiegao.getInstance().getQQ().isCancelled())
                    Qiegao.getInstance().getQQ().cancel();
            }else if(args[1].equalsIgnoreCase("on")){
                Qiegao.getPluginConfig().set("qqbot",true);
                Qiegao.getInstance().setQQ(new BukkitRunnable() {

                    @Override
                    public void run() {

                        Qiegao.getInstance().setQqServer(new HttpServer(31091));
                    }
                }.runTaskAsynchronously(Qiegao.getInstance()));
            }
        }
    }
    @Command(value="改变生物群系", possibleArguments="biome")
    @Cmd(value="biome", minArgs=1,permission = "qiegao.biome")
    public void biome(DefaultCommand defcmd)  {
        String[] args=defcmd.getArgs();
        Player player= (Player) defcmd.getSender();
        if (args.length==2){
//            Biome biome=Biome.valueOf(args[1]);
//            player.getWorld().getChunkAt(player.getLocation()).;
//            Bukkit.
            int x= (int) player.getLocation().getX();
            int z= (int) player.getLocation().getZ();
            int y= (int) player.getLocation().getY();
            int r=Integer.valueOf(args[1]);
            for (int i=x-r;i<=x+r;i++){
                for (int j=z-r;j<=z+r;j++){
                    player.getWorld().setBiome(i,j,Biome.SNOWY_TAIGA);
//                    player.getWorld().getBlockAt(i,y-1,j).setBiome(Biome.SNOWY_TAIGA);
                }

            }
            Log.toPlayer(player,"转换完成!",true);
        }
    }
    @Command(value="我来苟你！！！", possibleArguments="savelife")
    @Cmd(value="savelife", minArgs=2,permission = "qiegao.savelife")
    public void savelife(DefaultCommand defcmd)  {
        String[] args=defcmd.getArgs();

        if (args.length==2){
            Config.helpHashMap.add(args[1].toLowerCase());

            Log.toSender(defcmd.getSender(),args[1]+"已加入救援名单！",true);
        }
    }
    @Command(value="地标", possibleArguments="landmark")
    @Cmd(value="landmark", minArgs=3)
    public void landmark(DefaultCommand defcmd)  {
        String[] args=defcmd.getArgs();
        Player player= (Player) defcmd.getSender();
        if(args.length<3) {
            player.sendMessage("§c请输入完整命令");
            return;
        }
        List<String> area= (List<String>) Qiegao.getMessages().getList("landmark.area");
        if(area.indexOf(args[1])==-1){
            player.sendMessage("§c区域错误");
            return;
        }
        String line="";
        line=args[1].substring(0,2)+"-";
        String other="";
        if(args.length==3){
            other=args[2];
        }
        MarkerSet markerSet= DynmapPlugin.plugin.getMarkerAPI().getMarkerSet(args[1].substring(0,2));
        Set<Marker> markers=markerSet.getMarkers();
        List<String> idlist=new ArrayList<>();
        for (Marker m : markers) {
            idlist.add(m.getMarkerID());
        }
        String license="";
        for (int i=1; ;i++){
            license=String.format(line+"%03d",i);
            if(!idlist.contains(license))break;
            if(i>999){
                player.sendMessage("§c牌照生成失败！请联系管理员");
                return;
            }
        }


        RayTraceResult rayTraceResult=player.rayTraceBlocks(3);
        Block block=rayTraceResult.getHitBlock();

        BlockFace blockFace=rayTraceResult.getHitBlockFace();
        if (blockFace==null){
            Log.toConsole("null");
        }
        Block block1=block.getRelative(blockFace);

        switch (blockFace){
            case UP:
                block1.setType(Material.BIRCH_SIGN);

                break;
            case DOWN:
                return;
            default:
                block1.setType(Material.BIRCH_WALL_SIGN);


        }
        BlockData data = block1.getBlockData();
        if(data instanceof Directional){
            ((Directional) data).setFacing(blockFace);
            block1.setBlockData(data,true);
        }
        block1.getState().update();
        Sign sign = (Sign)block1.getState();
        sign.setLine(0, "切糕路标系统");
        sign.setLine(1, other);
        sign.setLine(2, license);
        sign.setLine(3, "最终解释权归糕委会所有");
        sign.update();
        markerSet.createMarker(license,other,false,player.getWorld().getName(),sign.getX(),sign.getY(),sign.getZ(),markerSet.getDefaultMarkerIcon(),true);

    }
    @Command(value="添加白名单", possibleArguments="wladd")
    @Cmd(value="wladd", minArgs=1,permission = "qiegao.wladd")
    public void wladd(DefaultCommand defcmd)  {
        String[] args=defcmd.getArgs();
//        Player player= (Player) defcmd.getSender();
        if(args.length<2) {
//            player.sendMessage("");
            Log.toSender(defcmd.getSender(),"§c请输入完整命令",true);
            return;
        }
        String name=args[1];
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"wladd "+name.toLowerCase());
        String content1="玩家："+name+" 已添加白名单！";
//        Tools.sendGroup("772095790",content1);
        Qiegao.getSm().insert("insert into `WhiteList` VALUES ( null,'"+name+"') ");
        Qiegao.getInstance().getQqBot().sendGroup(content1);
    }
    @Command(value="观察者", possibleArguments="spectator")
    @Cmd(value="spectator", minArgs=1)
    public void spectator(DefaultCommand defcmd)  {
        List players=new ArrayList();
        Player player= (Player) defcmd.getSender();
        players.add("spectator");
        if (players.contains(player.getName())){
            player.setGameMode(GameMode.SPECTATOR);
            Log.toSender(defcmd.getSender(),"已切换到观察者模式！",true);
        }else{
            Log.toSender(defcmd.getSender(),"你不在名单中，请联系管理员！",true);
        }
    }
    @Command(value="圈地", possibleArguments="save")
    @Cmd(value="save", minArgs=1,permission = "qiegao.save")
    public void save(DefaultCommand defcmd)  {
        Player player= (Player) defcmd.getSender();
        HashMap<String,Location> location=Config.quandi;

        MarkerSet markerSet= DynmapPlugin.plugin.getMarkerAPI().getMarkerSet("地皮");
        Set<Marker> markers=markerSet.getMarkers();
        List<String> idlist=new ArrayList<>();
        for (Marker m : markers) {
            idlist.add(m.getMarkerID());
        }
        if (!location.containsKey(player.getName()+"start")){
            Log.toPlayer(player,"请选择第一个点",true);
        }
        if (!location.containsKey(player.getName()+"end")){
            Log.toPlayer(player,"请选择第二个点",true);
        }
        String license="";
        int i=0;
        for ( i=1; ;i++){
            license=String.format("第%04d号地皮",i);
            if(!idlist.contains(license)){
                break;
            }
            if(i>9999){
//                player.sendMessage("§c牌照生成失败！请联系管理员");
                return;
            }
        }


        RayTraceResult rayTraceResult=player.rayTraceBlocks(3);
        Block block=rayTraceResult.getHitBlock();

        BlockFace blockFace=rayTraceResult.getHitBlockFace();
        if (blockFace==null){
            Log.toConsole("null");
        }
        Location start=location.get(player.getName()+"start");
        start.setY(start.getY()+1);
        Block block1=player.getWorld().getBlockAt(start);

        block1.setType(Material.BIRCH_SIGN);
        Sign sign = (Sign)block1.getState();
        sign.setLine(0, "切糕地皮系统");
        sign.setLine(1, license);
        sign.setLine(2, "未占用");
        sign.setLine(3, "最终解释权归糕委会所有");
        sign.update();
//        markerSet.createMarker(license,license,false,player.getWorld().getName(),sign.getX()
//                ,sign.getY(),sign.getZ(),markerSet.getDefaultMarkerIcon(),true);
        markerSet.createAreaMarker(license,license,false,player.getWorld().getName()
                ,new double[]{location.get(player.getName()+"start").getX(),location.get(player.getName()+"end").getX()}
                ,new double[]{location.get(player.getName()+"start").getZ(),location.get(player.getName()+"end").getZ()},true);
        Log.toPlayer(player,license+"生成成功!",true);

    }

    @Command(value="随机", possibleArguments="rand")
    @Cmd(value="rand", minArgs=3)
    public void rand(DefaultCommand defcmd)  {
        List players=new ArrayList();
        Player player= (Player) defcmd.getSender();
        String[] args=defcmd.getArgs();
        int num=Integer.valueOf(args[1]);
        int max=Integer.valueOf(args[2]);
        if(num==0||max==0){
            Log.toPlayer(player,"参数错误",true);
        }
        String msg=player.getDisplayName()+"投"+num+"d"+max+"投出了";
        for (int i=0;i<num;i++){
            msg+=Math.round(Math.ceil(Math.random()*max))+"，";
        }
        Bukkit.broadcastMessage(msg.substring(0,msg.length()-1));
    }

    @Command(value="统计", possibleArguments="count")
    @Cmd(value="count", minArgs=1,permission = "qiegao.count")
    public void count(DefaultCommand defcmd)  {
        List<Entity> entityList= Bukkit.getWorld("world").getEntities();
        Log.toConsole(entityList.size()+"");
        HashMap<EntityType,Integer> entityTypeIntegerHashMap =new HashMap<>();
        for (Entity entity :entityList) {
            if (entityTypeIntegerHashMap.containsKey(entity.getType())){
                int c=entityTypeIntegerHashMap.get(entity.getType())+1;
                entityTypeIntegerHashMap.replace(entity.getType(),c);
            }else{
                entityTypeIntegerHashMap.put(entity.getType(),1);
            }
        }
        Log.toConsole(entityTypeIntegerHashMap.size()+"");
        String message="";
        for (EntityType et :entityTypeIntegerHashMap.keySet() ) {
            message +=et.getName()+":"+entityTypeIntegerHashMap.get(et)+"\n";
        }
        Log.toSender(defcmd.getSender(),message,true);
    }

    @Command(value="糕历", possibleArguments="day")
    @Cmd(value="day", minArgs=1)
    public void day(DefaultCommand defcmd)  {
        int d= (int) (Bukkit.getWorld("world").getFullTime()/24000);
        String message=Tools.getGaoLi(d);
        Log.toSender(defcmd.getSender(),message,true);
    }

}

