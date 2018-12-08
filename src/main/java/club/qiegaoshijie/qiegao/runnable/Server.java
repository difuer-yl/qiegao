package club.qiegaoshijie.qiegao.runnable;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.util.Log;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 *
 * 服务器端程序：
 *
 * 1. 监听一端口，等待客户接入； 2. 一旦有客户接入，就构造一个Socket会话对象； 3. 将这个会话交给线程处理，然后主程序继续监听。
 *
 * @author OKJohn
 * @version 1.0
 */

public class Server extends ServerSocket {
    String s="";
    int time=0;
    public Server(int serverPort) throws IOException {
        // 用指定的端口构造一个ServerSocket
        super(serverPort);
        try {
            while (true) {
//                if (!Qiegao.getPluginConfig().getBoolean("qqbot",true)){
//                    close();
//                    break;
//                }
                // 监听一端口，等待客户接入
                Socket socket = accept();
                // 将会话交给线程处理
                new ServerThread(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(); // 关闭监听端口
        }
    }

    // inner-class ServerThread
    class ServerThread extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        // Ready to conversation
        public ServerThread(Socket s) throws IOException {
            this.socket = s;
            // 构造该会话中的输入输出流
            in = new BufferedReader(new InputStreamReader(socket
                    .getInputStream(), "utf8"));
            out = new PrintWriter(socket.getOutputStream(), true);
            start();
        }

        // Execute conversation
        public void run() {
            try {

                // Communicate with client until "bye " received.

                while (true) {
                    // 通过输入流接收客户端信息
                    String line = in.readLine();
//                    Bukkit.getServer().broadcastMessage(line);
//                    Bukkit.getServer().broadcastMessage(line);
                    if (line == null || "".equals(line.trim())) { // 是否终止会话
                        break;
                    }
                    if (line.indexOf("GET")!=-1){
                         line=URLDecoder.decode(line.substring(1),"UTF-8");
                         line=line.substring(line.indexOf("qq?")+6,line.indexOf(" HTTP/1.1"));

                        String[] ss=line.split("&");
                        HashMap<String,String> par=new HashMap<>();
                        for (String sss : ss) {
                            String[] ssss=sss.split("=");
                            par.put(ssss[0],ssss[1]);
                        }
                        Bukkit.getServer().broadcastMessage("§c[QQ]§r<§2"+par.get("user")+"§r>"+par.get("content"));
//                        String[] a=ss[1].split("-qiegao-");
//                        if (s.equalsIgnoreCase(a[0])&&time==Integer.valueOf(a[1])){
//                            break;
//                        }else{
//                            String[] aaaaa=a[0].split(":");
//                            Bukkit.getServer().broadcastMessage("§c[QQ]§r<§2"+aaaaa[0]+"§r>"+aaaaa[1]);
//                            break;
//                        }

                    }
                    // 通过输出流向客户端发送信息
//                    out.println(line);
//                    out.flush();

                }

                out.close();
                in.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // main method
    public static void main(String[] args) throws IOException {

    }
}
