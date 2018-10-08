package club.qiegaoshijie.qiegao.inventory;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Messages;
import club.qiegaoshijie.qiegao.models.Signin;
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
import java.sql.Date;
import java.util.*;

public class SigninGUI
{
    private Inventory GUI;
    private ItemStack item;

    public SigninGUI(String username)
    {
        this.item = item;
        this.GUI = Bukkit.createInventory(null, 36, "每日签到");

        ItemStack map;
        ItemMeta im;
        Signin si=new Signin();

        for (int i=1;i<=31;i++){
            map=new ItemStack(Material.PAPER,i);
            im=map.getItemMeta();
            im.setDisplayName(""+(i)+"日");
            map.setItemMeta(im);
            this.GUI.addItem(map);
        }
        int month=Calendar.getInstance().get(Calendar.MONTH)+1;
        int year=Calendar.getInstance().get(Calendar.YEAR);
        List l=si.getList("select * from qiegaoworld_signin where username= '"+username+"' and year="+year+" and month="+month);
        for (Object ll: l) {
            Signin s= (Signin) ll;
            map=new ItemStack(Material.MAP,s.getDay());
            map.getItemMeta().setDisplayName(""+s.getDay()+"日");
            this.GUI.setItem(s.getDay()-1,map);
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

