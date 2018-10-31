package club.qiegaoshijie.qiegao.listener;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.util.Log;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerListener implements Listener {

    private Entity entity;
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
    public void onPlayerJoinEvent(PlayerJoinEvent e){
        Location loc=e.getPlayer().getLocation();
        Firework firework = (Firework)loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();

        FireworkEffect effect = FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.AQUA).withFade(Color.BLUE).withTrail().withFlicker().build();
        meta.addEffect(effect);
        meta.setPower(1);
        firework.setFireworkMeta(meta);
    }

}
