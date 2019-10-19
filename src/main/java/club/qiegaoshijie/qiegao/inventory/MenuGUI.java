package club.qiegaoshijie.qiegao.inventory;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Messages;
import club.qiegaoshijie.qiegao.models.Maps;
import club.qiegaoshijie.qiegao.models.Skull;
import club.qiegaoshijie.qiegao.util.Log;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.SkullMeta;
import com.mojang.authlib.GameProfile;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MenuGUI
{
    private Inventory GUI;
    private ItemStack item;

    public MenuGUI()
    {
        this.item = item;


        this.GUI = Bukkit.createInventory(null, 54, Messages.GUI_MENU_TITLE);

        List<String> lore=new ArrayList<>();
        lore.add("§7"+Qiegao.getMessages().getString("menu.title"));


        ItemStack animal = new ItemStack(Material.NAME_TAG, 1);
        ItemMeta animalmeta = animal.getItemMeta();
        animalmeta.setDisplayName("动物申报");
        List<String> a=lore;
        a.addAll((List<String>) Qiegao.getMessages().getList("animal"));
        animalmeta.setLore(a);
        animal.setItemMeta(animalmeta);
        this.GUI.setItem(0, animal);




        String textures = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU2MWRlZDhkODM4NWI5MTNhMDkxYWVmNDc4M2ZjY2JmZDNkMzhlZGQ5MGIyZTg5YjcyM2I1YTU3NDM0YmY0In19fQ==";
        SkullMeta meta = (SkullMeta)Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        GameProfile profile = new GameProfile(UUID.fromString("89fda3a9-8fb2-49f6-b414-7a9a85"), null);

        profile.getProperties().put("textures", new Property("textures", textures));
        Field profileField = null;
        try
        {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        }
        catch (NoSuchFieldException|IllegalArgumentException|IllegalAccessException e1)
        {
            e1.printStackTrace();
        }
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        meta.setDisplayName("头颅领取");
        List<String> s=lore;
        s.addAll((List<String>) Qiegao.getMessages().getList("skull"));
        meta.setLore(s);

        skull.setItemMeta(meta);
        this.GUI.setItem(1, skull);

        //任务列表菜单
        ItemStack book = new ItemStack(Material.BOOK, 1);
        ItemMeta bookmeta =  book.getItemMeta();
        String title=Qiegao.getMessages().getString("task.title");
        bookmeta.setDisplayName(title);
        List<String> lore1=new ArrayList<>();
        lore1.add("§7"+Qiegao.getMessages().getString("menu.title"));
        lore1.add("点击查看 "+title);
        bookmeta.setLore(lore1);
        book.setItemMeta(bookmeta);
        this.GUI.addItem(book);


        //定制地图领取
        ItemStack map=new ItemStack(Material.MAP);
        ItemMeta mapMeta=  map.getItemMeta();
        mapMeta.setDisplayName("定制地图");
        List<String> m=new ArrayList<>();
        m.add("§7"+Qiegao.getMessages().getString("menu.title"));
        m.addAll((List<String>) Qiegao.getMessages().getList("maps.tips"));
        mapMeta.setLore(m);
        map.setItemMeta(mapMeta);
        this.GUI.addItem(map);

        ItemStack map1=new ItemStack(Material.NETHER_STAR);
        ItemMeta mapMeta1=  map.getItemMeta();
        mapMeta1.setDisplayName("每日签到");
        List<String> ll = new ArrayList<>();
        ll.add("§7"+Qiegao.getMessages().getString("menu.title"));
        ll.add("每日签到");
        mapMeta1.setLore(ll);
//        mapMeta1.setLore((List<String>) Qiegao.getMessages().getList("maps.tips"));
        map1.setItemMeta(mapMeta1);

        this.GUI.addItem(map1);


    }

    public static void event(InventoryClickEvent e){

        Player p = (Player)e.getWhoClicked();
        ItemStack citem = e.getCurrentItem();
        ItemMeta itemMeta=citem.getItemMeta();


        Log.toConsole("menu");

        if (itemMeta.getDisplayName().equals(" ")) {
            return;
        }
        if (itemMeta.getDisplayName().equals("动物申报")) {
            p.sendMessage("/qiegao tag <区域> <类型> <其他>");
            p.closeInventory();

        }else if(itemMeta.getDisplayName().equals("头颅领取")){
            club.qiegaoshijie.qiegao.models.Skull skull=new Skull();
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



    public Inventory getGUI()
    {
        return this.GUI;
    }

    public ItemStack getItem()
    {
        return this.item;
    }
}

