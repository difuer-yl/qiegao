package club.qiegaoshijie.qiegao.util;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * 此类由 Hykilpikonna 在 2018/05/24 创建!
 * Created by Hykilpikonna on 2018/05/24!
 * Github: https://github.com/hykilpikonna
 * QQ: admin@moecraft.cc -OR- 871674895
 *
 * @author Hykilpikonna
 */
@Data
public class HttpServer
{
    private final int port;

    private boolean started = true;
    private boolean paused = false;

    public HttpServer(int port)
    {
        this.port = port;
    }

    /**
     * 处理请求
     * @param data JSON
     */
    private void process(String data)
    {


    }

    /**
     * 启动HTTP服务器
     */
    @SuppressWarnings("deprecation")
    public void start()
    {
        ServerSocket serverSocket;
        try
        {
            serverSocket = new ServerSocket(this.port);
            Log.toConsole("§a启动成功! 开始接收消息...");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        Socket socket = null;
        OutputStream out = null;

        while (started)
        {
            if (paused) continue;
            try
            {
                // 关闭上次的Socket, 这样就能直接continue了
                if (out != null) out.close();
                if (socket != null && !socket.isClosed()) socket.close();

                // 获取新的请求
                socket = serverSocket.accept();

                // 读取请求字符
                InputStream inputStream = socket.getInputStream();
                DataInputStream reader = new DataInputStream(inputStream);
//                BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream, "utf8"));
                out = socket.getOutputStream();

                String line = reader.readLine();
                if (line == null || line.isEmpty())
                {
                    Log.toConsole("信息为空");
                    continue;
                }

                // 读取请求信息
                String[] info = line.split(" ");
                String method = info[0];
                String requestUrl = info[1];
                String httpVersion = info[2];

                if (!method.equalsIgnoreCase("post"))
                {
                    Log.toConsole("method"+method);
                    continue;
                }

                // 读取信息
                ArrayList<String> otherInfo = readOtherInfo(reader);
                String contentType = "UNINITIALIZED";
                String charset = "UNINITIALIZED";
                String userAgent = "UNINITIALIZED";
                int contentLength = -1;

                for (String oneInfo : otherInfo)
                {
                    if (oneInfo.contains("Content-Type: "))
                    {
                        oneInfo = oneInfo.replace("Content-Type: ", "");
                        if (!oneInfo.contains("application/json")) continue;
                        if (!oneInfo.contains("charset=UTF-8")) continue;

                        String[] split = oneInfo.split("; ");
                        contentType = split[0];
                        charset = split[1];
                    }
                    else if (oneInfo.contains("User-Agent: ")) userAgent = oneInfo.replace("User-Agent: ", "");
                    else if (oneInfo.contains("Content-Length: ")) contentLength = Integer.parseInt(oneInfo.replace("Content-Length: ", ""));
                }

                // 验证信息
                if (contentType.equals("UNINITIALIZED") || !contentType.equals("application/json"))
                {
                    Log.toConsole("json");
                    continue;
                }
                if (charset.equals("UNINITIALIZED") || !charset.equals("charset=UTF-8"))
                {
                    Log.toConsole("utf-8");
                    continue;
                }
                // 获取Post数据
                String data = "UNINITIALIZED";
                byte[] buffer ;
                int size = 0;

                if (contentLength != 0)
                {
                    buffer = new byte[contentLength];
//                    while(size < contentLength) buffer[size++] = (byte) reader.read();
//                    data = new String(buffer, 0, size);
//                    byte[] buffer=new byte[size];
                    reader.read(buffer);
                    data=new String(buffer,"utf8");
                }
                JsonObject json = new JsonParser().parse(data).getAsJsonObject();
                String user=json.getAsJsonObject("sender").get("card").getAsString();
                if (user.isEmpty()){
                    user=json.getAsJsonObject("sender").get("nickname").getAsString();
                }
                String userid=json.getAsJsonObject("sender").get("user_id").getAsString();
                String message=json.get("message").getAsString();
                if(message.contains("[CQ:"))continue;
                Bukkit.getServer().broadcastMessage("§c[QQ]§r<§2"+user+"§r>"+message);
            }
            catch (Throwable e)
            {
                Log.toConsole("请求接收失败: ");
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    sendResponseAndClose(out, "[]");

                    // 关闭接收
                    socket.close();
                }
                catch (Exception e)
                {
                    Log.toConsole("关闭接收失败: ");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取所有行
     * @param reader 读取器
     * @return 所有行的列表
     */
    @SuppressWarnings("deprecation")
    public static ArrayList<String> readOtherInfo(DataInputStream reader)
    {
        ArrayList<String> result = new ArrayList<>();

        while (true)
        {
            try
            {
                String line = reader.readLine();
                if (line.isEmpty()) break;

                result.add(line);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                break;
            }
        }

        return result;
    }

    /**
     * 回复JSON
     * @param out 输出流
     * @param jsonString JSON字符串
     */
    public void sendResponseAndClose(OutputStream out, String jsonString)
    {
        String response = "";
        response += "HTTP/1.1 204 OK\n";
        response += "Content-Type: application/json; charset=UTF-8\n";
        response += "\n";

        try
        {
            out.write(response.getBytes());
            // out.write(jsonString.getBytes());
            out.flush();

            out.close();
        }
        catch (IOException e)
        {
            Log.toConsole("消息发送失败: " + e.toString());
        }
    }
}

