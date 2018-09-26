package club.qiegaoshijie.qiegao.util;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class SoundGetter
{
    public static Sound getSound(String v18, String v19)
    {
        Sound sound = null;
        try
        {
            String s = Bukkit.getServer().getClass().getPackage().getName();
            String version = s.substring(s.lastIndexOf('.') + 1);
            if (version.startsWith("v1_8")) {
                sound = Sound.valueOf(v18);
            } else {
                sound = Sound.valueOf(v19);
            }
        }
        catch (Exception localException) {}
        return sound;
    }
}
