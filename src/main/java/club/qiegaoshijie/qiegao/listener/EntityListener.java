package club.qiegaoshijie.qiegao.listener;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;

public class EntityListener implements Listener {

    @EventHandler
    public void onItemDespawnEvent(ItemDespawnEvent e){
        ItemStack i=e.getEntity().getItemStack();
        if(i.hasItemMeta()&&i.getItemMeta().hasLore()){
//            e.setCancelled(true);
        }
    }

}
