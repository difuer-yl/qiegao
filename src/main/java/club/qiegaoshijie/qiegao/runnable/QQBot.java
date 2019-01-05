package club.qiegaoshijie.qiegao.runnable;

import cc.moecraft.icq.event.EventHandler;
import cc.moecraft.icq.event.IcqListener;
import cc.moecraft.icq.event.events.message.EventGroupMessage;
import cc.moecraft.icq.user.User;
import club.qiegaoshijie.qiegao.util.Log;
import org.bukkit.Bukkit;

public class QQBot extends IcqListener {
    @EventHandler
    public void onEventGroupMessage(EventGroupMessage e){
        Long groupId=e.groupId;
        Log.toConsole(groupId+"");
        if(groupId.equals("772095790")){
            String message=e.getMessage();
            User user=e.getSender();
            Bukkit.getServer().broadcastMessage("§c[QQ]§r<§2"+user.getInfo().getNickname()+"§r>"+message);
        }

    }
}
