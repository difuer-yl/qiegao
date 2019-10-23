package club.qiegaoshijie.qiegao.listener;

import club.qiegaoshijie.qiegao.config.Messages;
import club.qiegaoshijie.qiegao.inventory.MenuGUI;
import club.qiegaoshijie.qiegao.inventory.SigninGUI;
import club.qiegaoshijie.qiegao.inventory.TaskGUI;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public class InventoryListener
        implements Listener
{
    Entity entity;
    AnvilInventory ai;
    //点击库存
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
            String menu=Tools.isMenu(citem);
            if (menu!=null){
                e.setCancelled(true);
                if (Messages.GUI_MENU_TITLE.equals(menu)){
                    MenuGUI.event(e);
                }else if(Messages.GUI_SIGNIN_TITLE.equals(menu)){
                    SigninGUI.event(e);
                }else if(Messages.GUI_TASK_TITLE.equals(menu)){
                    TaskGUI.event(e);
                }
            }
        }
    }

    //关闭库存
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e)
    {
        return ;
//        if ((e.getPlayer() instanceof Player))
//        {
//            Player p = (Player)e.getPlayer();
//            String name = e.getInventory().getName();
//            if (name.equals(Log.translate(Messages.GUI_MENU)))
//            {
//                if (Commands.openinlay.containsKey(p))
//                {
//                    Inventory GUI = e.getInventory();
//                    ItemStack item19 = GUI.getItem(19);
//                    ItemStack item22 = GUI.getItem(22);
//                    ItemStack item25 = GUI.getItem(25);
//                    if (item19 != null) {
//                        p.getInventory().addItem(new ItemStack[] { item19 });
//                    }
//                    if (item22 != null) {
//                        p.getInventory().addItem(new ItemStack[] { item22 });
//                    }
//                    if (item25 != null) {
//                        p.getInventory().addItem(new ItemStack[] { item25 });
//                    }
//                    Commands.openinlay.remove(p);
//                    return;
//                }
//            }
//            if (name.equals(Log.translate(Messages.GUI_SEEGEM))) {
//            }
//            if (name.equals(Log.translate(Messages.GUI_ACCEPT))) {
//            }if (name.equals("圣诞节礼物交换箱")) {
//                if (Config.getSdjStatus()==0){
//                    try {
//                        if (new Date().getTime()>1545667200000L){
//                            Location location=Messages.locationHashMap.get(p.getName());
//                            ItemStack[] itemStacks=e.getInventory().getContents();
//                            ((Chest) Bukkit.getWorld("world").getBlockAt(location).getState()).getBlockInventory().setContents(itemStacks);
//                            for (ItemStack i : itemStacks) {
//                                if (i==null||i.getType()==Material.AIR||i.getType()==Material.RED_STAINED_GLASS_PANE){
//
//                                }else{
//                                    return;
//                                }
//                            }
//
//                            p.getWorld().getBlockAt(location).setType(Material.AIR);
//                        }else{
//                            ResultSet sd_chest_data = Qiegao.getSm().one("select * from qiegaoworld_otherdata where type='sdj_Storage' and name='"+p.getName()+"'");
//                            if (sd_chest_data==null || !sd_chest_data.next() ){
//                                int _x=100,_y=100;
//                                Location chest_location=null;
//                                for ( _x=-6400;_x<-6300;_x++){
//                                    for ( _y=7000;_y<7100;_y++){
//                                        chest_location=new Location(Bukkit.getWorld("world"),_x,255,_y);
//                                        if (Bukkit.getWorld("world").getBlockAt(chest_location).getType()==Material.AIR){
//                                            Bukkit.getWorld("world").getBlockAt(chest_location).setType(Material.CHEST);
//                                            Chest chest= (Chest) Bukkit.getWorld("world").getBlockAt(chest_location).getState();
//                                            chest.getBlockInventory().setContents(e.getInventory().getContents());
//                                            Qiegao.getSm().insert("insert into qiegaoworld_otherdata( type,name,data )values('sdj_Storage','"+e.getPlayer().getName()+"','"+chest_location.getX()+"&"+chest_location.getZ()+"')");
//                                            return;
//                                        }
//                                    }
//                                }
//                            }else{
//                                String  x_z= String.valueOf(sd_chest_data.getString("data"));
//                                String[] x_z_array=x_z.split("&");
//                                Location chest_location=new Location(Bukkit.getWorld("world"),Float.valueOf(x_z_array[0]),255,Float.valueOf(x_z_array[1]));
//                                ((Chest)Bukkit.getWorld("world").getBlockAt(chest_location).getState()).getBlockInventory().setContents(e.getInventory().getContents());
//                            }
//                        }
//
//                    } catch (SQLException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//
//
//            }
//        }
    }


    //当玩家改变他们当前持有的物品时被触发
    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent e){

    }

    //生物死亡
    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent e){
//        e.getEntity()
//        Log.toConsole("生物死亡");
    }


    //TODO: 特殊属性
    @EventHandler
    public void  onLore(PrepareItemCraftEvent e){
        ItemStack is1=e.getInventory().getResult();
        if(is1!=null&&is1.hasItemMeta()&&is1.getItemMeta().hasLore()){
            ItemMeta im=is1.getItemMeta();
            List l=im.getLore();
            if (l.contains("唯一")){
                is1.setType(Material.AIR);
                e.getInventory().setResult(is1);

            }
        }
    }

}
