package club.qiegaoshijie.qiegao.listener;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.command.Commands;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.config.Messages;
import club.qiegaoshijie.qiegao.inventory.SigninGUI;
import club.qiegaoshijie.qiegao.inventory.TaskGUI;
import club.qiegaoshijie.qiegao.models.DeclareAnimals;
import club.qiegaoshijie.qiegao.models.Maps;
import club.qiegaoshijie.qiegao.models.Signin;
import club.qiegaoshijie.qiegao.models.Skull;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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


                    }else if(citem.getItemMeta().getDisplayName().equals("任务列表")){


                        TaskGUI taskGUI=new TaskGUI(1);
                        p.closeInventory();
                        p.openInventory(taskGUI.getGUI());


                    }else if(citem.getItemMeta().getDisplayName().equals("每日签到")){


                        p.closeInventory();
                        p.openInventory(((SigninGUI)SigninGUI.getGUI(p.getName())).getGui());


                    }else if(citem.getItemMeta().getDisplayName().equals("定制地图")){
                        Maps maps=new Maps();
                        List a= maps.getMap(p.getName());
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
                            p.sendMessage("地图画订单："+((MapMeta) it.getItemMeta()).getMapId()+" 领取成功！");
                        }


                    }
                }

            }
            //任务列表
            else if (e.getClickedInventory().getName().equals(Qiegao.getMessages().getString("task.title"))) {
                e.setCancelled(true);
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
            //每日签到
            else if (e.getClickedInventory().getName().equals("每日签到")) {
                e.setCancelled(true);
                if (citem.getType()!=Material.PAPER && citem.getType()!=Material.MAP){
                    return;
                }
                int day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                if (day<e.getRawSlot()+1){
                    p.sendMessage("你是来自未来的穿越者吗？");
                    return;
                }

                if (citem.getType()==Material.MAP){
                    p.sendMessage("该日已签");
                    return;
                }


                if(day != e.getRawSlot()+1){
                    if (Signin.getSignin(p.getName()).getSupplement()<=0){
                        p.sendMessage("补签次数不足");
                        return;
                    }else{
                        Signin.signin(p,e.getRawSlot()+1);
                    }
                }else{
                    Signin.signin(p);
                }




                //刷新GUI
//                p.closeInventory();
                Inventory inv=((SigninGUI)SigninGUI.getGUI(p.getName())).getGui();
                p.openInventory(inv);
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
            }if (name.equals("圣诞节礼物交换箱")) {
                if (Config.getSdjStatus()==0){
                    try {
                        if (new Date().getTime()>1545667200000L){
                            Location location=Messages.locationHashMap.get(p.getName());
                            ItemStack[] itemStacks=e.getInventory().getContents();
                            ((Chest) Bukkit.getWorld("world").getBlockAt(location).getState()).getBlockInventory().setContents(itemStacks);
                            for (ItemStack i : itemStacks) {
                                if (i==null||i.getType()==Material.AIR||i.getType()==Material.RED_STAINED_GLASS_PANE){

                                }else{
                                    return;
                                }
                            }

                            p.getWorld().getBlockAt(location).setType(Material.AIR);
                        }else{
                            ResultSet sd_chest_data = Qiegao.getSm().one("select * from qiegaoworld_otherdata where type='sdj_Storage' and name='"+p.getName()+"'");
                            if (sd_chest_data==null || !sd_chest_data.next() ){
                                int _x=100,_y=100;
                                Location chest_location=null;
                                for ( _x=-6400;_x<-6300;_x++){
                                    for ( _y=7000;_y<7100;_y++){
                                        chest_location=new Location(Bukkit.getWorld("world"),_x,255,_y);
                                        if (Bukkit.getWorld("world").getBlockAt(chest_location).getType()==Material.AIR){
                                            Bukkit.getWorld("world").getBlockAt(chest_location).setType(Material.CHEST);
                                            Chest chest= (Chest) Bukkit.getWorld("world").getBlockAt(chest_location).getState();
                                            chest.getBlockInventory().setContents(e.getInventory().getContents());
                                            Qiegao.getSm().insert("insert into qiegaoworld_otherdata( type,name,data )values('sdj_Storage','"+e.getPlayer().getName()+"','"+chest_location.getX()+"&"+chest_location.getZ()+"')");
                                            return;
                                        }
                                    }
                                }
                            }else{
                                String  x_z= String.valueOf(sd_chest_data.getString("data"));
                                String[] x_z_array=x_z.split("&");
                                Location chest_location=new Location(Bukkit.getWorld("world"),Float.valueOf(x_z_array[0]),255,Float.valueOf(x_z_array[1]));
                                ((Chest)Bukkit.getWorld("world").getBlockAt(chest_location).getState()).getBlockInventory().setContents(e.getInventory().getContents());
                            }
                        }

                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }


            }
        }
    }

    @EventHandler
    public  void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e){
        ItemStack i=e.getPlayer().getInventory().getItemInMainHand();
        Player p=e.getPlayer();
        Entity a= e.getRightClicked();

        if(i.getType().equals(Material.NAME_TAG) ){
            ItemMeta im=i.getItemMeta();
            if (im!=null&&im.hasLore()){
                List<String> lore=im.getLore();
                if (lore.equals(Qiegao.getMessages().get("animal"))){
                    String license=i.getItemMeta().getDisplayName();
                    if(license.indexOf("-")==-1 || license.length()<6){
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
                    }else if(a.getType().equals(EntityType.LLAMA)){
                        Llama llama= (Llama) a;
                        switch (llama.getColor()){
                            case GRAY:da.setFeature("灰羊驼");break;
                            case BROWN:da.setFeature("棕羊驼");break;
                            case WHITE:da.setFeature("白羊驼");break;
                            case CREAMY:da.setFeature("奶油色羊驼");break;
                        }

                    }else if(a.getType().equals(EntityType.OCELOT)){
                        Ocelot ocelot= (Ocelot) a;
                        switch (ocelot.getCatType()){
                            case RED_CAT:da.setFeature("橘猫");break;
                            case BLACK_CAT:da.setFeature("黑猫");break;
                            case SIAMESE_CAT:da.setFeature("暹罗猫");break;
                            case WILD_OCELOT:da.setFeature("野猫");break;
                        }
                    }else{
                        da.setFeature(a.getType().getName());
                    }
                    da.replace();
                }

            }else{

            }


        }



        if (a!=null){
//            ItemFrame itemFrame= (ItemFrame) a;
        }


    }
    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent e){

    }



    public void onEntityDeathEvent(EntityDeathEvent e){
//        e.getEntity()
        Log.toConsole("生物死亡");
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
