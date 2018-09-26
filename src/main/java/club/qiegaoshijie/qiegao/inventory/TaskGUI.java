package club.qiegaoshijie.qiegao.inventory;


import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.models.Task;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TaskGUI {
    private Inventory GUI;
    private ItemStack item;
    public TaskGUI(){

    }
    public TaskGUI(int page){
        Task task=new Task();
        List list= task.getTask(page);
        this.GUI=Bukkit.createInventory(null,54,Qiegao.getMessages().getString("task.title"));
        ItemStack it;
        ItemMeta meta;
        for (Object L: list) {
            it= (ItemStack) L;
            this.GUI.addItem(it);
        }
        if (page==1){
            it=new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        }else{
            it=new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        }
        meta=it.getItemMeta();
        meta.setDisplayName("上一页");
        it.setItemMeta(meta);
        int i;
        for ( i = 45; i <49 ; i++) {
            this.GUI.setItem(i,it);
        }
        it=new ItemStack(Material.PAPER,page);
        meta=it.getItemMeta();
        meta.setDisplayName("当前第 "+page +" 页");
        it.setItemMeta(meta);
        this.GUI.setItem(49,it);
        if (list==null || list.size()<46){
            it=new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        }else{
            it=new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        }
        meta=it.getItemMeta();
        meta.setDisplayName("下一页");
        it.setItemMeta(meta);
        for ( i = 50; i <54 ; i++) {
            this.GUI.setItem(i,it);
        }
    }

    public Inventory getGUI() {
        return this.GUI;
    }

    public ItemStack getItem() {
        return this.item;
    }
}
