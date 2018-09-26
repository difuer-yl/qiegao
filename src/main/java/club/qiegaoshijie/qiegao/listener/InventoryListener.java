package club.qiegaoshijie.qiegao.listener;

import club.qiegaoshijie.qiegao.AnvilHandler;
import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.command.Commands;
import club.qiegaoshijie.qiegao.config.Messages;
import club.qiegaoshijie.qiegao.inventory.TaskGUI;
import club.qiegaoshijie.qiegao.models.DeclareAnimals;
import club.qiegaoshijie.qiegao.models.Skull;
import club.qiegaoshijie.qiegao.models.Task;
import club.qiegaoshijie.qiegao.runnable.AnvilTask;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class InventoryListener
        implements Listener
{
    Entity entity;
    AnvilInventory ai;
    @EventHandler
    public void onClickInventory(InventoryClickEvent e)
    {

        if ((e.getWhoClicked() instanceof Player))
        {
            Player p = (Player)e.getWhoClicked();

            if (e.getClickedInventory() == null) {
                return;
            }
            ItemStack citem = e.getCurrentItem();
            ItemMeta cm=citem.getItemMeta();
            //主菜单
            if (e.getClickedInventory().getName().equals(Qiegao.getMessages().getString("menu.title"))) {
                if ((e.getRawSlot() == 19) || (e.getRawSlot() == 25)) {
                    return;
                }
                if (e.getRawSlot() == 22)
                {
                    e.setCancelled(true);
                    return;
                }
                e.setCancelled(true);

                Inventory GUI = e.getClickedInventory();

                if (citem.hasItemMeta())
                {
                    if (citem.getItemMeta().getDisplayName().equals(" ")) {
                        return;
                    }
                    if (citem.getItemMeta().getDisplayName().equals("动物申报")) {
                        p.sendMessage("/qiegao tag <区域> <类型> <其他>");
                        p.closeInventory();

                    }else if(citem.getItemMeta().getDisplayName().equals("头颅领取")){
                        Skull skull=new Skull();
                        List a= skull.getSkull(p.getName());
                        if (a==null || a.size()==0){
                            p.sendMessage("暂无订单或余额不足！");
                            return;
                        }


                        Inventory pi=p.getInventory();
                        for (Object i: a) {
                            ItemStack it= (ItemStack) i;
                            if((pi.firstEmpty())!=-1){
                                pi.addItem((ItemStack) it);
                            }else{
                                p.getWorld().dropItem(p.getLocation(),  it);
                            }
                            p.sendMessage("头颅订单："+( it).getItemMeta().getDisplayName()+"*"+( it).getAmount()+" 领取成功！");
                        }

//                        p.getInventory().addItem()

                    }else if(citem.getItemMeta().getDisplayName().equals("任务列表")){


                        TaskGUI taskGUI=new TaskGUI(1);
                        p.closeInventory();
                        p.openInventory(taskGUI.getGUI());


                    }
                }

            }
            //任务列表
            else if (e.getClickedInventory().getName().equals(Qiegao.getMessages().getString("task.title"))) {
                e.setCancelled(true);
                if(e.getRawSlot()==49)return;
                if (citem.getType()==Material.LIGHT_GRAY_STAINED_GLASS_PANE) return;
                if(cm.hasLore()){
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



        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e)
    {
        if ((e.getPlayer() instanceof Player))
        {
            Player p = (Player)e.getPlayer();
            String name = e.getInventory().getName();
            if (name.equals(Log.translate(Messages.GUI_MENU)))
            {
                if (Commands.openinlay.containsKey(p))
                {
                    Inventory GUI = e.getInventory();
                    ItemStack item19 = GUI.getItem(19);
                    ItemStack item22 = GUI.getItem(22);
                    ItemStack item25 = GUI.getItem(25);
                    if (item19 != null) {
                        p.getInventory().addItem(new ItemStack[] { item19 });
                    }
                    if (item22 != null) {
                        p.getInventory().addItem(new ItemStack[] { item22 });
                    }
                    if (item25 != null) {
                        p.getInventory().addItem(new ItemStack[] { item25 });
                    }
                    Commands.openinlay.remove(p);
                    return;
                }
            }
            if (name.equals(Log.translate(Messages.GUI_SEEGEM))) {
            }
            if (name.equals(Log.translate(Messages.GUI_ACCEPT))) {
            }
        }
    }

    @EventHandler
    public  void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e){
        ItemStack i=e.getPlayer().getInventory().getItemInMainHand();

        if(i.getType().equals(Material.NAME_TAG) && i.getItemMeta().hasLore()){

            Entity a= e.getRightClicked();
            Player p=e.getPlayer();
            String license=i.getItemMeta().getDisplayName();
            if(license.indexOf("-")==-1 || license.length()<7){
                p.sendMessage("§c该牌照不合法");
                e.setCancelled(true);
                p.getInventory().setItemInMainHand(i);
                return;
            }
            if(!Tools.isType(a.getType(),license)){
                p.sendMessage("§c牌照种类与动物不匹配");
                e.setCancelled(true);
                p.getInventory().setItemInMainHand(i);
                return;
            }
            DeclareAnimals da=new DeclareAnimals(license);
            if(da.getId()==0){
                p.sendMessage("§c该牌照未注册");
                e.setCancelled(true);
                p.getInventory().setItemInMainHand(i);
                return;
            }
            if(license.indexOf("共享")!=-1){
                da.setBinding("公共");
            }else{
                da.setBinding(p.getPlayerListName());
            }
            da.setStatus(1);
            if(a.getType().equals(EntityType.HORSE)) {
                Horse horse = (Horse) a;
                da.setFeature(da.getColor(horse.getColor().toString()) + " " + da.getStyle(horse.getStyle().toString())+" 马");

            }else if(a.getType().equals(EntityType.DONKEY)){
                da.setFeature("驴");
            }else if(a.getType().equals(EntityType.SKELETON_HORSE)){
                da.setFeature("骷髅马");
            }else if(a.getType().equals(EntityType.MULE)){
                da.setFeature("骡子");
            }else if(a.getType().equals(EntityType.PIG)){
                da.setFeature("猪");
            }else{
                e.setCancelled(true);
                return;
            }
            da.replace();
        }


    }
    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent e){

    }




}
