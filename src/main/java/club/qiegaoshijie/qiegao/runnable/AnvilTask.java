package club.qiegaoshijie.qiegao.runnable;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import club.qiegaoshijie.qiegao.AnvilHandler;
import club.qiegaoshijie.qiegao.Qiegao;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class AnvilTask
        extends BukkitRunnable
{
    private static HashMap<Inventory, AnvilTask> anvilTasks = new HashMap();
    private AnvilInventory inv;
    private Player player;

    public AnvilTask(AnvilInventory inv, Player player)
    {
        this.inv = inv;
        this.player = player;
        anvilTasks.put(inv, this);
        runTaskTimer(Qiegao.getInstance(), 1L, 3L);
    }

    public void run()
    {
        if (inv==null||this.inv.getViewers().size() == 0) {
            cancel();
        }
        AnvilHandler.getTranslatedItem(this.player, this.inv, this);
    }

    public static AnvilTask getTask(AnvilInventory inv)
    {
        return (AnvilTask)anvilTasks.get(inv);
    }
}
