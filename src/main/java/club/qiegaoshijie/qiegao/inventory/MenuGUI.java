package club.qiegaoshijie.qiegao.inventory;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Messages;
import club.qiegaoshijie.qiegao.util.Log;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.SkullMeta;
import com.mojang.authlib.GameProfile;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class MenuGUI
{
    private Inventory GUI;
    private ItemStack item;

    public MenuGUI()
    {
        this.item = item;
        this.GUI = Bukkit.createInventory(null, 54, Qiegao.getMessages().getString("menu.title"));
        ItemStack animal = new ItemStack(Material.NAME_TAG, 1);
        ItemMeta animalmeta = animal.getItemMeta();
        animalmeta.setDisplayName("动物申报");
        animalmeta.setLore((List<String>) Qiegao.getMessages().getList("animal"));
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
        meta.setLore((List<String>) Qiegao.getMessages().getList("skull"));

        skull.setItemMeta(meta);
        this.GUI.setItem(1, skull);

        //任务列表菜单
        ItemStack book = new ItemStack(Material.BOOK, 1);
        ItemMeta bookmeta =  book.getItemMeta();
        String title=Qiegao.getMessages().getString("task.title");
        bookmeta.setDisplayName(title);
        List<String> lore=new ArrayList<>();
        lore.add("点击查看 "+title);
        bookmeta.setLore(lore);
        book.setItemMeta(bookmeta);
        this.GUI.addItem(book);



        ItemStack map=new ItemStack(Material.MAP);
        ItemMeta mapMeta=  map.getItemMeta();
        mapMeta.setDisplayName("定制地图");
        mapMeta.setLore((List<String>) Qiegao.getMessages().getList("maps.tips"));
        map.setItemMeta(mapMeta);

        this.GUI.addItem(map);


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

