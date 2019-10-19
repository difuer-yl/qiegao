package club.qiegaoshijie.qiegao.listener;

import club.qiegaoshijie.qiegao.util.Log;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

public class EntityListener implements Listener {

    @EventHandler
    public void onItemDespawnEvent(ItemDespawnEvent e){
        ItemStack i=e.getEntity().getItemStack();
        if(i.hasItemMeta()&&i.getItemMeta().hasLore()){
//            e.setCancelled(true);
        }
    }

    /**
     * 当一个世界产生实体时调用
     * @param e
     */
    @EventHandler
    public void onEntitySpawnEvent(EntitySpawnEvent e){
        Location location=e.getLocation();
        int[] _x=new int[]{620,676};
        int[] _y=new int[]{39,63};
        int[] _z=new int[]{3163,3219};
//        e.getEntity().
        EntityType[] entityTypes=new EntityType[]{EntityType.CREEPER,EntityType.BAT,EntityType.ENDERMAN
                ,EntityType.GUARDIAN,EntityType.SKELETON,EntityType.SPIDER,EntityType.ZOMBIE,EntityType.ZOMBIE_VILLAGER};
        if(location.getWorld().getName().equalsIgnoreCase("world")){
            int x=location.getBlockX();
            int y=location.getBlockY();
            int z=location.getBlockZ();
            if(x>=_x[0]&&x<=_x[1]&&y>=_y[0]&&y<=_y[1]&&z>=_z[0]&&z<=_z[1]&&Arrays.asList(entityTypes).contains(e.getEntityType())){
//                Log.toConsole("true");
                e.setCancelled(true);
            }
        }
    }

    /**
     * 当射弹击中物体时调用
     * @param event
     */
    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent event){
        Entity entity=event.getHitEntity();
        if (entity instanceof Player){
            Entity entity1=entity.getVehicle();
            if (entity1!=null && entity1 instanceof  Player){
                entity.leaveVehicle();
            }
        }
    }
}
