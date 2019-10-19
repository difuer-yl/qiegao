package club.qiegaoshijie.qiegao.inventory;


import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Messages;
import club.qiegaoshijie.qiegao.models.Task;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TaskGUI {
    private Inventory GUI;
    private ItemStack item;
    public TaskGUI(){

    }
    public TaskGUI(int page){
        Task task=new Task();
        List list= task.getTask(page);
        List<String> lore=new ArrayList<>();
        lore.add("§7"+ Messages.GUI_TASK_TITLE);
        this.GUI=Bukkit.createInventory(null,54,Qiegao.getMessages().getString("task.title"));
        ItemStack it;
        ItemMeta meta;
        for (Object L: list) {
            it= (ItemStack) L;
            meta=it.getItemMeta();
            meta.setLore(lore);
            it.setItemMeta(meta);
            this.GUI.addItem(it);
        }
        if (page==1){
            it=new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        }else{
            it=new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        }
        meta=it.getItemMeta();
        meta.setDisplayName("上一页");
        meta.setLore(lore);
        it.setItemMeta(meta);
        int i;
        for ( i = 45; i <49 ; i++) {
            this.GUI.setItem(i,it);
        }
        it=new ItemStack(Material.PAPER,page);
        meta=it.getItemMeta();
        meta.setDisplayName("当前第 "+page +" 页");
        meta.setLore(lore);
        it.setItemMeta(meta);
        this.GUI.setItem(49,it);
        if (list==null || list.size()<46){
            it=new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        }else{
            it=new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        }
        meta=it.getItemMeta();
        meta.setDisplayName("下一页");
        meta.setLore(lore);
        it.setItemMeta(meta);
        for ( i = 50; i <54 ; i++) {
            this.GUI.setItem(i,it);
        }
    }

    public static void event(InventoryClickEvent e){

        Player p = (Player)e.getWhoClicked();
        ItemStack citem = e.getCurrentItem();
        ItemMeta cm=citem.getItemMeta();

        if(e.getRawSlot()==49)return;
        if (citem.getType()==Material.LIGHT_GRAY_STAINED_GLASS_PANE) return;
        if(cm!=null&&cm.hasLore()){
            Inventory pi=p.getInventory();
            if((pi.firstEmpty())!=-1){
                pi.addItem(citem);
            }else{
                p.getWorld().dropItem(p.getLocation(),  citem);
            }
            return;
        }


        ItemStack it_page=e.getClickedInventory().getItem(49);
        int page=Integer.valueOf(it_page.getAmount());
        if (cm.getDisplayName().equals("上一页")){
            page-=1;
        }else{
            page+=1;
        }

        TaskGUI taskGUI=new TaskGUI(page);
        p.closeInventory();
        p.openInventory(taskGUI.getGUI());
    }

    public Inventory getGUI() {
        return this.GUI;
    }

    public ItemStack getItem() {
        return this.item;
    }
}
