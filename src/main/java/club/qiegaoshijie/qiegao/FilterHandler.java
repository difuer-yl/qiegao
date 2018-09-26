package club.qiegaoshijie.qiegao;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FilterHandler
{
    public static List<String> getIllegalWords()
    {
            return new ArrayList();
    }

    public static List<String> getIllegalWordsInString(String string)
    {
        ArrayList<String> caughtWords = new ArrayList();
        string = string.toLowerCase();
        for (String illegalWord : getIllegalWords())
        {
            illegalWord = illegalWord.toLowerCase();
            if (string.contains(illegalWord)) {
                caughtWords.add(illegalWord);
            }
        }
        return caughtWords;
    }

    public static List<String> getIllegalWordsInItemName(ItemStack item)
    {
        if ((item == null) || (!item.hasItemMeta()) || (!item.getItemMeta().hasDisplayName())) {
            return new ArrayList();
        }
        return getIllegalWordsInString(item.getItemMeta().getDisplayName());
    }
}
