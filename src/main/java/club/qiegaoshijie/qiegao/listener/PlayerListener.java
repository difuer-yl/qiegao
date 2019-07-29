package club.qiegaoshijie.qiegao.listener;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.config.Messages;
import club.qiegaoshijie.qiegao.runnable.QQBot;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import club.qiegaoshijie.qiegao.util.sqlite.SqliteManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PlayerListener implements Listener {

    private Entity entity;
    private HashMap<String, BukkitTask> join=new HashMap<>();
    @EventHandler
    public void  onPlayerInteractEntityEvent(PlayerInteractEntityEvent e){
            this.entity=e.getRightClicked();

    }

    /**
     * 表示当玩家与对象或空中交互时调用的事件
     * @param e
     */
    @EventHandler
    public  void  onPlayerInteractEvent (PlayerInteractEvent e){

        Player p=e.getPlayer();
        Block b=e.getClickedBlock();
        ItemStack i=e.getPlayer().getInventory().getItemInMainHand();
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            //Loops through all loaded physical locations.

            if(i.getType()==Material.EGG &&b.getType()== Material.CHEST){
//                e.setCancelled(true);
//                ItemStack displayItem=new ItemStack(Material.EGG);
//                Location loc=b.getLocation();
//                loc.setPitch(0);
//                loc.setY(loc.getY()+2);
//                Block blo=p.getWorld().getBlockAt(loc);
//                blo.setType(Material.WHITE_CARPET);
//                Item reward = p.getWorld().dropItem(loc.clone().add(.5, 1, .5), displayItem);
//                reward.setVelocity(new Vector(0, .2, 0));
//                reward.setCustomName("这是一个蛋");
//                reward.setCustomNameVisible(true);
//                reward.setPickupDelay(Integer.MAX_VALUE);
            }
            //设置音符盒metedata -- 报站功能
            if(i.getType()==(Material.NAME_TAG) ){
                if (b.getType()==Material.NOTE_BLOCK&&p.isSneaking()){
                    if ("清除".equals(i.getItemMeta().getDisplayName())){
                        b.removeMetadata("music",Qiegao.getInstance());
                    }else{

                        b.setMetadata("music",new FixedMetadataValue(Qiegao.getInstance(),i.getItemMeta().getDisplayName()));
                    }
                    e.setCancelled(true);
                }

            }
            if (b.getType()==Material.SIGN){
                Sign sign= (Sign) b.getState();
                if (sign.getLine(0).equalsIgnoreCase("§6[万圣节活动]")){
                    if (Config.getIswsj()<(new Date().getTime())){
                        p.sendMessage("活动未开启！");
                        return;
                    }
                    if (sign.getLine(1).equalsIgnoreCase("§a加入活动")){
                        ItemStack[] its=p.getInventory().getArmorContents();
                        for (ItemStack it : its) {
                            if (it ==null||it.getType()==Material.AIR){

                            }else{
                                p.sendMessage("§6[万圣节活动] §r请先脱光光哦！");
                                return;
                            }
                        }
                        its=p.getInventory().getContents();
                        for (ItemStack it : its) {
                            if (it ==null||it.getType()==Material.AIR){

                            }else{
                                p.sendMessage("§6[万圣节活动] §r请先清空背包，以免不必要的损失！");
                                return;
                            }
                        }
                        ///execute in minecraft:overworld run tp @s 835.64 70.00 1564.60 -7014.47 46.50
//                        p.teleport(new Location(p.getWorld(),916,73,1525));
                        p.teleport(new Location(p.getWorld(),835,70,1564));
                        p.setExhaustion(0);

                        p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                        p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                        p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                        p.getInventory().setItem(0,new ItemStack(Material.IRON_SWORD));
                        p.getInventory().setItem(1,new ItemStack(Material.BAKED_POTATO,16));

                        //生成猪头
                        SkullMeta meta = (SkullMeta)Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
                        GameProfile profile = new GameProfile(UUID.fromString("b9d5b95a-237c-461e-a316-e1ede14ab301"), null);

                        profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVmYjJkZjc1NGM5OGI3NDJkMzVlN2I4MWExZWVhYzlkMzdjNjlmYzhjZmVjZDNlOTFjNjc5ODM1MTZmIn19fQ=="));
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
                        ItemStack skull = new ItemStack(Material.PLAYER_HEAD,1);

                        meta.setDisplayName("§r§6万圣节活动专用");
                        skull.setItemMeta(meta);
                        p.getInventory().setHelmet(skull);
                        if (!Config.playerHashMap.containsKey(p.getName())){
                            Config.playerHashMap.put(p.getName(),p);

                        }
                    }else if(sign.getLine(1).equalsIgnoreCase("§c离开活动")){
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
                        if (Config.playerHashMap.containsKey(p.getName())){
                            Config.playerHashMap.remove(p.getName());
                        }
                        ///execute in minecraft:overworld run tp @s 783.52 78.00 1527.60 -2608.47 67.50
                        p.teleport(new Location(p.getWorld(),783,78,1527));
                    }
                }
                if (sign.getLine(0).equalsIgnoreCase("清空背包")){
                    p.getInventory().clear();
                }
            }
            if (true||b.getType()==Material.CHEST){

            }
            if(i.getType()==Material.WOODEN_HOE){
                List<String> lore=i.getItemMeta().getLore();
                if(lore.contains("§c天选之锄")){
                    HashMap<String,Location> location=Config.quandi;
                    if (location.containsKey(p.getName()+"end")){
                        location.replace(p.getName()+"end",b.getLocation());
                    }else{
                        location.put(p.getName()+"end",b.getLocation());
                    }
                    Log.toPlayer(p,"成功选定第二个点:"+b.getLocation().toString(),true);
                    e.setCancelled(true);
                }
            }

        }else if(e.getAction() == Action.LEFT_CLICK_BLOCK){
            if(i.getType()==Material.WOODEN_HOE){
                List<String> lore=i.getItemMeta().getLore();
                if(lore.contains("§c天选之锄")){
//                    Qiegao.getMessages()
                    HashMap<String,Location> location=Config.quandi;
                    if (location.containsKey(p.getName()+"start")){
                        location.replace(p.getName()+"start",b.getLocation());
                    }else{
                        location.put(p.getName()+"start",b.getLocation());
                    }
                    Log.toPlayer(p,"成功选定第一个点"+b.getLocation().toString(),true);
                    e.setCancelled(true);
                }
            }

        }
    }
    @EventHandler
    public void  onNotePlayEvent(NotePlayEvent e){

        Block note=e.getBlock();
        List<MetadataValue> me=  note.getMetadata("music");
        String s="";
        for (MetadataValue m: me ) {
            if (m.asString()!=null){
                s=m.asString();
            }
        }
        note.getWorld().playSound(note.getLocation(),"gt." +s+"", SoundCategory.MUSIC,12,1);

    }

    /**
     * 玩家登录事件
     * @param e
     */
    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent e){
        if (!Config.getIsrun())return;
        join.put(e.getPlayer().getName(),
        new BukkitRunnable() {
            //
            private  int num=0;
            @Override
            public void run() {
                if (num>10){
                    BukkitTask bt=join.get(e.getPlayer().getName());
                    if (!bt.isCancelled()){
                        bt.cancel();
                    }
                    e.getPlayer().setResourcePack("https://qiegao-1252250917.cos.ap-guangzhou.myqcloud.com/QiegaoWorld_base.zip", Tools.toBytes("4895edb26154a2f040b963004184224906b99564"));
                    return;
                }
                Location loc=e.getPlayer().getLocation();
                Firework firework = (Firework)loc.getWorld().spawn(loc, Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                FireworkEffect.Builder effect;
                if (num%2==0){
                    effect = FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE);

                }else{
                    effect = FireworkEffect.builder().with(FireworkEffect.Type.STAR);

                }
                effect=effect.withColor(Color.PURPLE,Color.ORANGE);
                if (Math.random()>0.5){
                    effect = effect.withTrail();

                }
                effect=effect.withFlicker();
                if (Math.random()>0.5){
                    effect = effect.withFade(Color.BLUE);

                }

                meta.addEffect(effect.build());
                meta.setPower(1);
                firework.setFireworkMeta(meta);
                num++;
            }
        }.runTaskTimer(Qiegao.getInstance(),5,20));



    }
    @EventHandler(priority= EventPriority.LOWEST)
    public void onChatting(AsyncPlayerChatEvent event) {
        if (event.getMessage().toLowerCase().indexOf("login")!=-1||event.getMessage().toLowerCase().indexOf("lg")!=-1){
            event.getPlayer().sendMessage(ChatColor.GOLD + "切糕世界" + ChatColor.GREEN + " >> " + ChatColor.LIGHT_PURPLE + "请正确使用登录命令 " + ChatColor.GOLD + "\"/login\"");
            event.setCancelled(true);
            return;
        }
        if ((event.isAsynchronous()) && (event.getMessage().equals("1")))
        {
//            event.setCancelled(true);
            String color="";
            int ping=Tools.getPing(event.getPlayer());
            if (ping <= 100) {
                color = "§a";
            } else if (ping <= 200) {
                color = "§e";
            } else {
                color = "§c";
            }
            String color_tps = null;
            double tps=Tools.getTps();
            if (tps >= 18.5D) {
                color_tps = "§a";
            } else if (tps >= 15.1D) {
                color_tps = "§e";
            } else {
                color_tps = "§c";
            }

            event.getPlayer().sendMessage(ChatColor.GOLD + "切糕世界" + ChatColor.GREEN + " >> " + ChatColor.LIGHT_PURPLE + "你没有掉线 你的当前延迟为:" +color+ping + "ms" + ChatColor.LIGHT_PURPLE + " 服务器tps:" + color_tps+tps + ChatColor.LIGHT_PURPLE + "/" + ChatColor.GREEN + "20.0");
            event.getPlayer().sendMessage(ChatColor.GOLD + "切糕世界" + ChatColor.GREEN + " >> " + ChatColor.LIGHT_PURPLE + "如果单纯只想发1，请发" + ChatColor.GOLD + "\"!1\"");
        }
        else if ((event.isAsynchronous()) && (event.getMessage().equals("!1")))
        {
            event.setMessage(event.getMessage().replaceAll("!1", "1"));
        }
        List<String> text = null,quotations=null;
        text= (List<String>) Qiegao.getMessages().getList("speak.text",text);
        if ((event.isAsynchronous()) && text.contains(event.getMessage()))
        {
            quotations= (List<String>) Qiegao.getMessages().getList("speak.quotations",quotations);
            for (int i=0;i<text.size();i++) {
                if (text.get(i).equals(event.getMessage())){
                    if (quotations.size()<(i+1)){

                    }else{
                        event.setMessage(quotations.get(i)+" ——市长语录");

                    }
                }
            }
        }


    }
    @EventHandler(priority= EventPriority.MONITOR)
    public void onBroadcastMessageEvent(BroadcastMessageEvent e){
        if (e.getMessage().toLowerCase().indexOf("login")!=-1||e.getMessage().toLowerCase().indexOf("lg")!=-1){
            e.setCancelled(true);
            return;
        }
        if (e.getMessage().indexOf("§c[QQ]§r")!=-1||e.getMessage().indexOf("[切糕新闻]")!=-1||e.getMessage().indexOf("[切糕报时]")!=-1){
            return;
        }
        String content=e.getMessage().replaceAll("§[0-9a-f]","");
        try {
            if (content.indexOf("[WEB]")==-1){
                Qiegao.getInstance().getQqBot().sendGroup("[mc]"+content);
            }else{
                Qiegao.getInstance().getQqBot().sendGroup(content);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        if (e.getMessage().toLowerCase().indexOf("login")!=-1||e.getMessage().toLowerCase().indexOf("lg")!=-1){
            e.setCancelled(true);
            return;
        }
        String content=e.getPlayer().getPlayerListName().substring(2)+":"+e.getMessage().replaceAll("§[0-9a-f]","");
        try {
            Qiegao.getInstance().getQqBot().sendGroup(content);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }



    @EventHandler
    public void  onPlayerJoin(PlayerJoinEvent e){
        Player player=e.getPlayer();
        List help=Config.helpHashMap;
        if (help.size()>0&&help.contains(player.getName().toLowerCase())){
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
            Config.helpHashMap.remove(player.getName());
        }
    }

    /**
     * 与盔甲架交互
     * @param e
     */
    @EventHandler
    public void onPlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent e){
        Entity entity= e.getRightClicked();
        Player player=e.getPlayer();
        Location sd_chest_location=new Location(Bukkit.getWorld("world"),841.5,81.0,-33.5);
        if (Config.getSdjStatus()==0){
            if(entity.getLocation().getX()!=sd_chest_location.getX()||entity.getLocation().getY()!=sd_chest_location.getY()||entity.getLocation().getZ()!=sd_chest_location.getZ()){
                return ;
            }

            try {
                ResultSet sd_chest_data = Qiegao.getSm().one("select * from qiegaoworld_otherdata where type='sdj_Storage' and name='"+player.getName()+"'");
                Inventory sd_chest_inventory=null;
                sd_chest_inventory= Bukkit.createInventory(null,9,"圣诞节礼物交换箱");
                //1545667200000L
                if (new Date().getTime()>1545667200000L){
                    if (sd_chest_data == null || !sd_chest_data.next()) {
                        Log.toPlayer(player,"您没有参与礼物交换活动，无法交换礼物！",true);
                    } else {

                        sd_chest_data = Qiegao.getSm().filter("select * from qiegaoworld_otherdata where type='sdj_Storage_user'");
                        while (sd_chest_data.next()){
                            String x_z = String.valueOf(sd_chest_data.getString("data"));
                            String[] x_z_array = x_z.split("&");
                            Location chest_location = new Location(Bukkit.getWorld("world"), Float.valueOf(x_z_array[0]), 255, Float.valueOf(x_z_array[1]));
                            Messages.locationHashMap.put(sd_chest_data.getString("name"),chest_location);
                            Log.toConsole(sd_chest_data.getString("name"));
                        }
                        Location location=null;
                        if (Messages.locationHashMap.containsKey(player.getName())){
                            location=Messages.locationHashMap.get(player.getName());
                        }else{
                            if (Messages.sdj_chesh_list.size()==0){
                                int _x=100,_y=100;
                                Location chest_location=null;
                                for ( _x=-6400;_x<-6300;_x++){
                                    for ( _y=7000;_y<7100;_y++){
                                        chest_location=new Location(Bukkit.getWorld("world"),_x,255,_y);
                                        if (Bukkit.getWorld("world").getBlockAt(chest_location).getType()==Material.CHEST){
                                            if (!Messages.locationHashMap.containsValue(chest_location)){
                                                Messages.sdj_chesh_list.add(chest_location);
                                            }
                                        }
                                    }
                                }
                            }
                            int d= (int) (Math.random()*Messages.sdj_chesh_list.size());
                            location=Messages.sdj_chesh_list.get(d);
                            Qiegao.getSm().insert("insert into qiegaoworld_otherdata( type,name,data )values('sdj_Storage_user','"+e.getPlayer().getName()+"','"+location.getX()+"&"+location.getZ()+"')");

                        }
                        if (Bukkit.getWorld("world").getBlockAt(location).getType()==Material.AIR){
                            Log.toPlayer(player,"您已领取礼物，无法再次领取",true);
                            e.setCancelled(true);
                            return;
                        }
                        Inventory tmp = ((Chest) Bukkit.getWorld("world").getBlockAt(location).getState()).getBlockInventory();
                        for (int _i = 0; _i < 9; _i++) {
                            sd_chest_inventory.setItem(_i, tmp.getItem(_i));
                        }


//                        player.getWorld().getBlockAt(location).setType(Material.AIR);
                        player.openInventory(sd_chest_inventory);
                    }
                }else {
                    if (sd_chest_data == null || !sd_chest_data.next()) {
                        ItemStack RED_STAINED_GLASS_PANE = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                        ItemMeta rsgp_meta = RED_STAINED_GLASS_PANE.getItemMeta();
                        rsgp_meta.setDisplayName("§r§c无法使用");
                        RED_STAINED_GLASS_PANE.setItemMeta(rsgp_meta);
                        sd_chest_inventory.setItem(4, RED_STAINED_GLASS_PANE);
                        sd_chest_inventory.setItem(5, RED_STAINED_GLASS_PANE);
                        sd_chest_inventory.setItem(6, RED_STAINED_GLASS_PANE);
                        sd_chest_inventory.setItem(7, RED_STAINED_GLASS_PANE);
                        sd_chest_inventory.setItem(8, RED_STAINED_GLASS_PANE);
                    } else {
                        String x_z = String.valueOf(sd_chest_data.getString("data"));
                        String[] x_z_array = x_z.split("&");
                        Location chest_location = new Location(Bukkit.getWorld("world"), Float.valueOf(x_z_array[0]), 255, Float.valueOf(x_z_array[1]));
                        Inventory tmp = ((Chest) Bukkit.getWorld("world").getBlockAt(chest_location).getState()).getBlockInventory();
                        for (int _i = 0; _i < 9; _i++) {
                            sd_chest_inventory.setItem(_i, tmp.getItem(_i));
                        }
                    }
                    player.openInventory(sd_chest_inventory);
                }
                e.setCancelled(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            e.setCancelled(true);
        }else if(Config.getSdjStatus()==1){
            if(entity.getLocation().getX()!=sd_chest_location.getX()||entity.getLocation().getY()!=sd_chest_location.getY()||entity.getLocation().getZ()!=sd_chest_location.getZ()){
                return ;
            }

//            try {
////                ResultSet sd_chest_data = sqliteManager.one("select * from qiegaoworld_otherdata where type='sdj_Storage' and name='"+player.getName()+"'");
////                Inventory sd_chest_inventory=null;
////                sd_chest_inventory= Bukkit.createInventory(null,9,"圣诞节礼物交换箱");
////                if (sd_chest_data==null || !sd_chest_data.next() ){
////                    Log.toPlayer(player,"您没有参加礼物交换活动，无法领取礼物！",true);
////
////                }else{
////                    String  x_z= String.valueOf(sd_chest_data.getString("data"));
////                    String[] x_z_array=x_z.split("&");
////                    Location chest_location=new Location(Bukkit.getWorld("world"),Float.valueOf(x_z_array[0]),255,Float.valueOf(x_z_array[1]));
////                    Inventory tmp=((Chest)Bukkit.getWorld("world").getBlockAt(chest_location).getState()).getBlockInventory();
////                    for (int _i=0;_i<9;_i++){
////                        sd_chest_inventory.setItem(_i,tmp.getItem(_i));
////                    }
////                }
////                player.openInventory(sd_chest_inventory);
//                e.setCancelled(true);
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//            }
            e.setCancelled(true);
        }
    }

    /**
     * 与传送门接触时
     * @param event
     */
    @EventHandler
    public void onPlayerPortalEvent(PlayerPortalEvent event){
//        Location location=new Location(,);

//        event.setPortalTravelAgent();
//        new HashMap<>().containsValue()
    }

    @EventHandler
    public void onPlayerTeleportEvent(PlayerTeleportEvent event){
        World world=event.getTo().getWorld();
        World world2=event.getFrom().getWorld();
//        String name="AdvancedAchievements";
//        Log.toConsole("from"+world2.getName());
//        Log.toConsole("to"+world.getName());
//        if(world.getName().equalsIgnoreCase("test")&&!world2.getName().equalsIgnoreCase("test")){
//            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
//                    Log.toConsole("plugin:"+plugin.getName()+Bukkit.getPluginManager().isPluginEnabled(plugin));
//                if (name.equalsIgnoreCase(plugin.getName())) {
//
//                    Bukkit.getPluginManager().disablePlugin(plugin);
//                    Log.toPlayer(event.getPlayer(),"禁用"+name+"成功！",true);
//                    return ;
//
//                }
//            }
//            Log.toPlayer(event.getPlayer(),"禁用"+name+"失败！",true);
//        }else if(world2.getName().equalsIgnoreCase("test")&&!world.getName().equalsIgnoreCase("test")){
//            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
//                Log.toConsole("plugin:"+plugin.getName());
//                if (name.equalsIgnoreCase(plugin.getName())&&!Bukkit.getPluginManager().isPluginEnabled(plugin)) {
//
//                    Bukkit.getPluginManager().enablePlugin(plugin);
//                    Log.toPlayer(event.getPlayer(),"启用"+name+"成功！",true);
//                    return ;
//
//                }
//            }
//        }
    }



}
