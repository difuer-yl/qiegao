package club.qiegaoshijie.qiegao.runnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.util.Tools;
import org.bukkit.*;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.scheduler.BukkitRunnable;

public class DynmapTask extends BukkitRunnable
{
    private Qiegao plugin;
    private int i;

    public DynmapTask(Qiegao instance,int i)
    {
        this.plugin = instance;
        this.i=i;
    }


    public void run()
    {
        if (!Bukkit.getPluginManager().isPluginEnabled("dynmap")){
            return ;
        }
        ResultSet sd_chest_data = Qiegao.getSm().filter("select * from qiegaoworld_message where status=1 ");
        List<Integer> id=new ArrayList<>();
        HashMap<Integer,Integer> id_time=new HashMap<>();
        try {
            if (sd_chest_data==null  ){
                return;
            }else{
                while (sd_chest_data.next()){
                    id_time.put(sd_chest_data.getInt("id"),sd_chest_data.getInt("num"));
                    if(sd_chest_data.getInt("id")==this.i){
                        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                            sendAnnouncement(p,sd_chest_data.getString("content"));
                        }
                        continue;
                    }
                    id.add(sd_chest_data.getInt("id"));
                }
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (id_time.size()==0||Bukkit.getServer().getOfflinePlayers().length==0){
            new DynmapTask(Qiegao.getInstance(), 0).runTaskLater(Qiegao.getInstance(),20L*600);
        }
        int _id=0;
        if(id.size()==0){
            _id=this.i;
        }else{
            _id=id.get((int) (Math.random()*id.size()));

        }
        int _time=id_time.get(_id);

        int final_id = _id;
        new DynmapTask(Qiegao.getInstance(), final_id).runTaskLater(Qiegao.getInstance(),_time*20L);
        //runTaskLater

    }
    protected static void sendAnnouncement(Player p, String line)
    {
        if ((line == null) || (line.isEmpty())) {
            return;
        }
        if (Qiegao.placeholderHook) {
            line = PlaceholderAPI.setPlaceholders(p, line);
        }
        if (line == null) {
            return;
        }
        if (line.contains("%player%")) {
            line = line.replace("%player%", p.getName());
        }
        if (line.contains("%displayname%")) {
            line = line.replace("%displayname%", p.getDisplayName());
        }
        if (line.contains("%online%")) {
            line = line.replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        }
        if (line.startsWith("[text]"))
        {
            line = line.replace("[text]", "");

            p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
        else if (line.contains("&&"))
        {
            String[] parts = line.split("&&");

            List<String> components = new ArrayList<>();
            String[] arrayOfString1;
            int j = (arrayOfString1 = parts).length;
            for (int i = 0; i < j; i++)
            {
                String part = arrayOfString1[i];
                components.add(part);
            }
            Tools.send(p, components);
        }
        else
        {
            Tools.send(p, line.replace("'","\""));
        }
    }


}
