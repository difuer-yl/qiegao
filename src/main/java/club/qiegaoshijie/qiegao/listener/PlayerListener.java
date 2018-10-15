package club.qiegaoshijie.qiegao.listener;

import club.qiegaoshijie.qiegao.util.Log;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class PlayerListener implements Listener {

    private Entity entity;
    @EventHandler
    public void  onPlayerInteractEntityEvent(PlayerInteractEntityEvent e){
            this.entity=e.getRightClicked();

    }

    @EventHandler
    public  void  onPlayerInteractEvent (PlayerInteractEvent e){

        Player p=e.getPlayer();
        Block b=e.getClickedBlock();
        ItemStack i=e.getPlayer().getInventory().getItemInMainHand();
        if(false&&e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            //Loops through all loaded physical locations.

            if(i.getType()==Material.EGG &&b.getType()== Material.CHEST){
                e.setCancelled(true);
                ItemStack displayItem=new ItemStack(Material.EGG);
                Location loc=b.getLocation();
                loc.setPitch(0);
                loc.setY(loc.getY()+2);
                Block blo=p.getWorld().getBlockAt(loc);
//                ItemStack d=new ItemStack(Material.BIRCH_PRESSURE_PLATE);
//                p.getWorld()
                blo.setType(Material.WHITE_CARPET);
                Item reward = p.getWorld().dropItem(loc.clone().add(.5, 1, .5), displayItem);
                reward.setVelocity(new Vector(0, .2, 0));
                reward.setCustomName("这是一个蛋");
                reward.setCustomNameVisible(true);
                reward.setPickupDelay(Integer.MAX_VALUE);
            }

        }
    }
    @EventHandler
    public void  onNotePlayEvent(NotePlayEvent e){
        Block note=e.getBlock();
    }
}
