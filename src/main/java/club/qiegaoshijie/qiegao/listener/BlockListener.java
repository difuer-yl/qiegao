package club.qiegaoshijie.qiegao.listener;

import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.util.Log;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NBTTagString;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.dynmap.bukkit.DynmapPlugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BlockListener implements Listener {

    private HashMap<Location,Long> timeMap=new HashMap<Location, Long>();
    private Long now=0L;
    /**
     * 方块破坏事件
     * @param e
     */
    @EventHandler
    public void onBlockBreakEvent (BlockBreakEvent e){
        Block b=e.getBlock();
        Player p=e.getPlayer();
        Location loc=b.getLocation();
        ///execute in minecraft:overworld run tp @s 900.65 73.00 1585.13 -4124.52 59.55
        ///execute in minecraft:overworld run tp @s 795.68 78.00 1525.46 -4324.31 57.90
//        int s_x=925,s_y=1562,e_x=933,e_y=1555;
        if (Config.getIswsj()>(new Date().getTime())){
            int s_x=795,s_y=1525,e_x=900,e_y=1585;
            if ((loc.getX()<s_x || loc.getX()>e_x)&&(loc.getY()>e_y||loc.getY()<s_y))return;
            if (b.getType()== Material.GRASS_BLOCK){

                Long time=0L;
                if (now !=Config.getIswsj()){
                    timeMap=new HashMap<>();
                    now=Config.getIswsj();
                }
                if (timeMap.containsKey(b.getLocation())){
                    time=timeMap.get(b.getLocation());
                }
                if (time< new Date().getTime()){
                    int n= (int) (Math.random()*100);
                    ItemStack sl;
                    ItemMeta im;
                    List<String> lore=new ArrayList<>();
                    int num= (int) (Math.random()*3)+1;
                    if (n<=20){

                        sl=new ItemStack(Material.BROWN_MUSHROOM,num);
//                ItemStack sl=new ItemStack(Material.SUGAR,num);
//                net.minecraft.server.v1_13_R2.ItemStack nmsItem= CraftItemStack.asNMSCopy(sl);
//                NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
//                compound.set("type", new NBTTagString("wsj"));
//                nmsItem.setTag(compound);
//                //之后将nmsItem转换为BukkitAPI中的ItemStack
//                sl = CraftItemStack.asBukkitCopy(nmsItem);

                        im=sl.getItemMeta();
                        im.setDisplayName("§r§6松露");

                        lore.add("§r§8特供松露，营养价值极高");
                        lore.add("§r§8万圣节活动特供");
                        im.setLore(lore);
                        sl.setItemMeta(im);

                        loc.setY(loc.getY()+1);
                        p.getWorld().dropItem(loc,sl);
                        if(Config.fraction.containsKey(p.getName())){
                            Config.fraction.replace(p.getName(),Config.fraction.get(p.getName())+num);
                        }else{
                            Config.fraction.put(p.getName(),num);
                        }
                    }
                    if (timeMap.containsKey(b.getLocation())){
                        timeMap.replace(b.getLocation(),(new Date().getTime())+num*60*1000);
                    }else{
                        timeMap.put(b.getLocation(),(new Date().getTime())+num*60*1000);
                    }


                    if (n<90){
                        sl=new ItemStack(Material.SUGAR);
                        im=sl.getItemMeta();
                        im.setDisplayName("§6糖果");

                    }else if (n<98){
                        sl=new ItemStack(Material.POTION);
                        im=sl.getItemMeta();
                        im.setDisplayName("§6快乐水");
                    }else{
                        sl=new ItemStack(Material.EMERALD);
                        im=sl.getItemMeta();
                        im.setDisplayName("§6钻戒");
                    }
                    im=sl.getItemMeta();
                    lore=new ArrayList<>();
                    lore.add("§r§8万圣节活动特供");
                    im.setLore(lore);
                    sl.setItemMeta(im);
                    p.getWorld().dropItem(loc,sl);

                }else{
                    p.sendMessage("§a[万圣节活动]§r§c这块草皮都被薅秃了，行行好，换个地方薅吧！");
                }

            }
            e.setCancelled(true);
        }
        if(b.getType().equals(Material.BIRCH_SIGN)||b.getType().equals(Material.BIRCH_WALL_SIGN)){
            Sign sign=(Sign)b.getState();
            if (sign.getLine(0).contains("切糕路标系统")){
                DynmapPlugin.plugin.getMarkerAPI().getMarkerSet(sign.getLine(2).substring(0,2)).findMarker(sign.getLine(2)).deleteMarker();
                e.setCancelled(true);
                b.setType(Material.AIR);
            }
        }


    }

    /**
     * 告示牌
     * @param e
     */
    @EventHandler
    public void onSignChangeEvent(SignChangeEvent e){
        if (e.getLine(0).equalsIgnoreCase("[万圣节活动]")){
            e.setLine(0,"§6"+e.getLine(0));
            if (e.getLine(1).equalsIgnoreCase("加入活动")){
                e.setLine(1,"§a"+e.getLine(1));
            }else if(e.getLine(1).equalsIgnoreCase("离开活动")){
                e.setLine(1,"§c"+e.getLine(1));
            }
        }
    }
}
