package club.qiegaoshijie.qiegao.recipe;

import club.qiegaoshijie.qiegao.Qiegao;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MoonCake
{
    public static void Load(){
//        recipe.put()
        ;
        String[] xian=new String[]{"t","n","m","j","z","y","m",};

        init();
//        Bukkit.getServer().clearRecipes();
        Bukkit.getServer().resetRecipes();
        for (int i : itemDict.keySet()) {
            ItemStack itemStack=getItemStack(i);
            ShapedRecipe s1 = new ShapedRecipe(new NamespacedKey(Qiegao.getInstance(), "yuebing"+i),itemStack);
            s1.shape(new String[]{"mmm","t"+i+"t","mmm"});
            s1.setIngredient('m',Material.WHEAT);
            s1.setIngredient('t',Material.SUGAR);
            s1.setIngredient( (""+i).charAt(0),itemDict.get(i));
            s1.setGroup("qiegao");

            Bukkit.getServer().addRecipe(s1);
        }
//        s1.setIngredient('t',Material.SUGAR);

    }
    private static HashMap<Integer,Material> itemDict=new HashMap<>();
    private static HashMap<Integer, String> potionDict=new HashMap<>();
    private static void init(){
        itemDict.put(1,Material.SUGAR);//"基础月饼，或者说没有馅的月饼3饱食度"
        itemDict.put(2,Material.COOKED_BEEF);//"牛肉馅，额外4饱食度"
        itemDict.put(3,Material.INK_SAC);//"墨囊馅，失明buff"
        itemDict.put(4,Material.RABBIT_FOOT);//兔子脚馅，跳跃buff
        itemDict.put(5,Material.CHORUS_FRUIT);//紫颂果馅，漂浮buff
        itemDict.put(6,Material.GLOWSTONE_DUST);//萤石粉馅，发光buff
        itemDict.put(7,Material.COAL);//煤炭馅，凋零buff
        itemDict.put(8,Material.SPIDER_EYE);//蜘蛛眼馅，中毒buff
        itemDict.put(9,Material.BLAZE_POWDER);//烈焰粉，点燃buff

        potionDict.put(1,"-");

        potionDict.put(2,"-4");
        potionDict.put(3,"BLINDNESS");
        potionDict.put(4,"JUMP");
        potionDict.put(5,"LEVITATION");
        potionDict.put(6,"GLOWING");
        potionDict.put(7,"WITHER");
        potionDict.put(8,"POISON");
        potionDict.put(9,"-点燃");

        /**
         * 麦 麦 麦
         * 糖 馅 糖
         * 麦 麦 麦
         */

    }

    private static ItemStack getItemStack(int i){

        ItemStack itemStack=new ItemStack(Material.COOKIE);

        ItemMeta itemMeta=itemStack.getItemMeta();
        itemMeta.setDisplayName("月饼");
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.DURABILITY,i,true);
        itemStack.setItemMeta(itemMeta);

        return  itemStack;
    }
    public static String getPotion(int i){
        return potionDict.get(i);
    }
    //
//    private static


}
