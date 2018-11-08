package club.qiegaoshijie.qiegao.listener;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
                e.setCancelled(true);
                ItemStack displayItem=new ItemStack(Material.EGG);
                Location loc=b.getLocation();
                loc.setPitch(0);
                loc.setY(loc.getY()+2);
                Block blo=p.getWorld().getBlockAt(loc);
                blo.setType(Material.WHITE_CARPET);
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

        }
    }
    @EventHandler
    public void  onNotePlayEvent(NotePlayEvent e){

        Block note=e.getBlock();
        Log.toConsole(note.getType().toString());
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
                Log.toConsole(num+"");
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



        Log.toConsole("登录烟花");
    }


}
