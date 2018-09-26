package club.qiegaoshijie.qiegao.listener;

import club.qiegaoshijie.qiegao.util.Log;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener {

    private Entity entity;
    @EventHandler
    public void  onPlayerInteractEntityEvent(PlayerInteractEntityEvent e){
            this.entity=e.getRightClicked();

    }

    @EventHandler
    public  void  onPlayerInteractEvent (PlayerInteractEvent e){
    }
}
