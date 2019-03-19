package club.qiegaoshijie.qiegao.runnable;




import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.dynmap.DynmapAPI;
import org.dynmap.DynmapCore;
import org.dynmap.DynmapWebChatEvent;
import org.dynmap.bukkit.DynmapPlugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class QQBot extends WebSocketClient {


    public QQBot(URI serverUri , Draft draft ) {
        super( serverUri, draft );
    }

    public QQBot( URI serverURI ) {
        super( serverURI );
    }

    public QQBot( URI serverUri, Map<String, String> httpHeaders ) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage( String m ) {
        JsonObject json = new JsonParser().parse(m).getAsJsonObject();
        if(json.get("post_type")==null){
            return ;
        }
        String post_type=json.get("post_type").getAsString();
        if(post_type.equalsIgnoreCase("message")){
            String user=json.getAsJsonObject("sender").get("card").getAsString();
            if (user.isEmpty()){
                user=json.getAsJsonObject("sender").get("nickname").getAsString();
            }
            String userid=json.getAsJsonObject("sender").get("user_id").getAsString();
            String title=json.getAsJsonObject("sender").get("title").getAsString();
            String level=json.getAsJsonObject("sender").get("level").getAsString();
            String message=json.get("message").getAsString();
            String content="";
            if(message.contains("请升级到最新版本后查看。")){
//                continue;
                return;
            }
            if(message.contains("[CQ:")){
//                continue;
                return;
                //message=message.replaceAll("\\[CQ:face,id=\\d+\\]","");

            }else{

                if(content.indexOf("[web]")==0){
                    content="[{\"text\":\"[web]\",\"color\":\"dark_red\"},{\"text\":\"<\",\"color\":\"white\"},{\"text\":\""
                            +user+"\",\"color\":\"dark_green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"QQ号： \"},{\"text\":\""
                            +userid+"\n\",\"color\":\"blue\"},{\"text\":\"用户组： \"},{\"text\":\"" + (title.isEmpty()?level:title) +"\n\",\"color\":\"blue\"}]}}}," +
                            "{\"text\":\">\",\"color\":\"white\"},{\"text\":\""+message+"\",\"color\":\"white\"}]";
                }else{

                content="[{\"text\":\"[QQ]\",\"color\":\"dark_red\"},{\"text\":\"<\",\"color\":\"white\"},{\"text\":\""
                        +user+"\",\"color\":\"dark_green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"QQ号： \"},{\"text\":\""
                        +userid+"\n\",\"color\":\"blue\"},{\"text\":\"用户组： \"},{\"text\":\"" + (title.isEmpty()?level:title) +"\n\",\"color\":\"blue\"}]}}}," +
                        "{\"text\":\">\",\"color\":\"white\"},{\"text\":\""+message+"\",\"color\":\"white\"}]";
                }
            }
//                    Bukkit.getServer().broadcastMessage("§c[QQ]§r<§2"+user+"§r>"+message);

            //
            for (Player p : Bukkit.getOnlinePlayers()) {
                Tools.send(p,content);
            }
            DynmapPlugin.plugin.sendBroadcastToWeb(user,message);

        }else if(post_type.equalsIgnoreCase("notice")){
            String notice_type=json.get("notice_type").getAsString();
            if(notice_type.equalsIgnoreCase("group_increase")){
                String user_id=json.get("user_id").getAsString();
                String content1="欢迎[CQ:at,qq="+user_id+"] 加入切糕世界，请查阅群置顶公告，填写问卷\n https://wj.qq.com/s/2946124/8714/";
                String content="https://docs.qq.com/doc/B08b5w10PPoZ0CLlK21FFPbn0mScP63ZmZgl3IQmKC2Cjyb92Zoj5Z0dyoIe2vnRX54GyL0i0 文档是切糕世界游玩指南，请认真查看，有利于存活！";
                content+="\n 遇事多问，会帮助你在切糕世界愉快的玩耍！";
                content+="\n 最后，祝你在切糕世界玩得开心！";
                content.replace("/§[0-9a-f]/","");

                sendGroup(content1);
                sendGroup(content);

                content1="欢迎加入切糕世界，请查阅群置顶公告，填写问卷\n https://wj.qq.com/s/2946124/8714/";
                sendUser(user_id,content1);

                content="https://docs.qq.com/doc/B08b5w10PPoZ0CLlK21FFPbn0mScP63ZmZgl3IQmKC2Cjyb92Zoj5Z0dyoIe2vnRX54GyL0i0 文档是切糕世界游玩指南，请认真查看，有利于存活！";
                content+="\n 遇事多问，会帮助你在切糕世界愉快的玩耍！";
                content+="\n 最后，祝你在切糕世界玩得开心！";
                sendUser(user_id,content);
            }
        }
//        System.out.println( "received: " + m );
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        // The codecodes are documented in class org.CloseFrame
        Log.toConsole("消息互通关闭成功");
    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }


    public  void sendGroup(String group_id,String content){
        JSONObject jsonObject1=new JSONObject();
        jsonObject1.put("group_id",group_id);
        jsonObject1.put("message",content);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("action","send_group_msg");
        jsonObject.put("post_type","api");
        jsonObject.put("params",jsonObject1);
        send(jsonObject.toString());
    }
    public  void sendGroup(String content){
        sendGroup("772095790",content);
    }
    public  void sendUser(String user_id,String content){
        JSONObject jsonObject1=new JSONObject();
        jsonObject1.put("user_id",user_id);
        jsonObject1.put("message",content);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("action","send_private_msg");
        jsonObject.put("post_type","api");
        jsonObject.put("params",jsonObject1);
        send(jsonObject.toString());
    }

    public void send(String string){
        if (this.isClosed()){
            this.reconnect();
        }
        super.send(string);
    }


    public static void main( String[] args ) throws URISyntaxException {
//        QQBot c = new QQBot( new URI( "ws://localhost:8887" )); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
//        c.connect();

    }
}
