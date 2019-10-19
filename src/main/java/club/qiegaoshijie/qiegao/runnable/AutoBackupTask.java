package club.qiegaoshijie.qiegao.runnable;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AutoBackupTask extends BukkitRunnable
{
    private Qiegao plugin;
    private int i;
    BufferedReader yesterday_log_reader=null;
    BufferedWriter log_writer=null;
    HashMap<String,Long> yesterday_log_readers=new HashMap<>();
    File file=null;
    File log=null;
    Long millis=0L;
    static final int BUFFER = 8192;

    public AutoBackupTask(Qiegao instance)  {
        this.plugin = instance;
        this.i=i;
        int y= Calendar.getInstance().get(Calendar.YEAR);
        int m= Calendar.getInstance().get(Calendar.MONTH)+1;
        int d= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        File base=Bukkit.getWorldContainer();
//        Log.toConsole();

        file= new File(base,"backup/"+y+"/"+m+"/"+d+".zip");
        Calendar calendar=Calendar.getInstance();
        calendar.clear();
        calendar.set(y,m,d);
        millis= calendar.getTimeInMillis()/1000;
        Long yesterday=millis-86400;
        log=new File(base,"backup/log/"+millis+".log");

        File yesterday_log=new File(base,"backup/log"+yesterday+".log");
        try {
            if(!log.getParentFile().mkdirs()||!log.createNewFile()){
                Log.toConsole("创建文件失败");
                return;
            }
            log_writer=new BufferedWriter(new FileWriter(log));
            if (yesterday_log.exists()){
                this.yesterday_log_reader=new BufferedReader(new FileReader(yesterday_log));
                String temp=null;
                while ((temp=yesterday_log_reader.readLine())!=null){
                    String[] temps=temp.split(":");
                    yesterday_log_readers.put(temps[0], Long.valueOf(temps[1]));
                }
                yesterday_log_reader.close();
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void run()
    {

        if (file.exists()){
            Log.toConsole("1111");
//            new AutoBackupTask(Qiegao.getInstance()).runTaskLater(Qiegao.getInstance(),20L*6000);
        }else{
            Log.toConsole("2222");
            InputStream input = null;
            ZipOutputStream zipOut = null;
            File mapfiles=Bukkit.getWorldContainer();

            try {
                file=file.getCanonicalFile();
                Log.toConsole(file.getCanonicalPath());
                if(!file.getParentFile().mkdirs()||!file.createNewFile()){
                    Log.toConsole("创建文件失败");
                }

//                if(!createDir(file)||!file.createNewFile()){
//                    Log.toConsole("创建文件失败");
//
//                    new AutoBackupTask(Qiegao.getInstance()).runTaskLater(Qiegao.getInstance(),20L*6000);
//                    return;
//                }

                zipOut = new ZipOutputStream(new FileOutputStream(
                        file));
                zipOut.setComment("hello");
                Log.toConsole(mapfiles.getPath());
                List<World> worlds= Bukkit.getWorlds();
                for (World w :worlds) {
                    zipOut=directory2Zip(zipOut,mapfiles.getPath()+File.separator+w.getName());

                }

                Log.toConsole("备份成功");
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }finally {
                try {
                    zipOut.close();
                    log_writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
//        new AutoBackupTask(Qiegao.getInstance()).runTaskLater(Qiegao.getInstance(),6000*20L);
        //runTaskLater

    }

    private ZipOutputStream directory2Zip(ZipOutputStream zipOutputStream,String filePath) throws IOException {
        File file=new File(filePath);
        File[] files = file.listFiles();
        FileInputStream input=null;
        Log.toConsole(filePath);
        if (files==null)return zipOutputStream;
        for(int i = 0; i < files.length; ++i){
            if (files[i].isDirectory()){
                zipOutputStream=directory2Zip(zipOutputStream,file.getPath()
                        + File.separator + files[i].getName());
                continue;
            }
            if (yesterday_log_readers.containsKey(files[i].getPath())){
                if (files[i].lastModified()/1000<=yesterday_log_readers.get(files[i].getPath())){

                    log_writer.write(files[i].getPath()+":"+yesterday_log_readers.get(files[i].getPath()));
                    continue;
                }
            }
            log_writer.write(files[i].getPath()+":"+millis+"\n");


//            input = new FileInputStream(files[i]);
//            zipOutputStream.putNextEntry(new ZipEntry(file.getPath()
//                    + File.separator + files[i].getName()));
//            int temp = 0;
//            while((temp = input.read()) != -1){
//                zipOutputStream.write(temp);
//                zipOutputStream.flush();
//            }
//            input.close();
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(files[i]));
                ZipEntry entry = new ZipEntry(file.getPath()
                    + File.separator + files[i].getName());
                zipOutputStream.putNextEntry(entry);
                int count;
                byte data[] = new byte[BUFFER];
                while ((count = bis.read(data, 0, BUFFER)) != -1) {
                    zipOutputStream.write(data, 0, count);
                }

            }finally {
                if(null != bis){
                    bis.close();
                }
            }
        }
        return zipOutputStream;
    }

    private boolean createDir(File file) throws IOException {
        Log.toConsole(file.getPath());
       if (file.exists()){
           Log.toConsole(file.getPath()+"已存在");
           return true;
       }else{
           Log.toConsole("尝试创建："+file.getPath());
            return createDir(file.getParentFile())&&file.mkdir();

       }
//        return file.mkdir();



    }


}
