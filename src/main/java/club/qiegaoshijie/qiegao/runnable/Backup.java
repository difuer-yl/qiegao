package club.qiegaoshijie.qiegao.runnable;


import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class Backup
{
    private static boolean backingUp = false;
    private static boolean timer = false;
    private static boolean timerTrue = false;
    private static Thread thread = null;
    private static int percent = 0;
    private static int lastPercent = -1;

    private static File file=null;
    private static File base=null;
    private static File log=null;
    private static Long millis=0L;
    private static boolean isDebug=false;
    private static BufferedReader yesterday_log_reader=null;
    private static BufferedWriter log_writer=null;
    private static HashMap<String,Long> yesterday_log_readers=new HashMap<>();

    public static void startTimer()
    {
        timer = true;
        base=Bukkit.getWorldContainer();
        if (!timerTrue)
        {
            timerTrue = true;
            new BukkitRunnable()
            {
                public void run()
                {
                    int y= Calendar.getInstance().get(Calendar.YEAR);
                    int m= Calendar.getInstance().get(Calendar.MONTH)+1;
                    int d= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    file= new File(base,"backup/"+y+"/"+m+"/"+d+".zip");
                    Calendar calendar=Calendar.getInstance();
                    calendar.clear();
                    calendar.set(y,m,d);
                    millis= calendar.getTimeInMillis()/1000;
                    Long yesterday=millis-86400;
                    log=new File(base,"backup/log/"+millis+".log");

                    File yesterday_log=new File(base,"backup/log/"+yesterday+".log");
                    try {
                        if (!log.exists()){
                            if(!log.getParentFile().mkdirs()&&!log.createNewFile()){
                                Log.toConsole("[自动备份]创建日志文件失败");
                                return;
                            }
                        }

                        log_writer=new BufferedWriter(new FileWriter(log));
                        if (yesterday_log.exists()){
                            yesterday_log_reader=new BufferedReader(new FileReader(yesterday_log));
                            String temp=null;
                            while ((temp=yesterday_log_reader.readLine())!=null){
                                String[] temps=temp.split(":");
                                if (temps.length==2)
                                    yesterday_log_readers.put(temps[0], Long.valueOf(temps[1]));
                            }
                            yesterday_log_reader.close();
                        }


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (isDebug){
                        file.deleteOnExit();
                    }
                    if (isDebug ||System.currentTimeMillis() - Qiegao.getPluginConfig().getLong("lastBackup")*1000 >= 86400000) {
                        if ((Backup.timer) && (Qiegao.getPluginConfig().getBoolean("autoBackup",true)))
                        {
                            if (!Backup.backingUp) {

                                Backup.backup();
                            }
                        }
                        else
                        {
                            Backup.timerTrue = false;
                            cancel();
                        }
                    }
                }
            }.runTaskTimer(Qiegao.getInstance(), 200L, 72000L);
        }
    }

    public static boolean isBackingUp()
    {
        return backingUp;
    }

    public static String backup()
    {
        Qiegao.getPluginConfig().set("lastBackup", millis);
        Qiegao.getPluginConfig().set("debug",false);
        try
        {
            Qiegao.getPluginConfig().save();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        backingUp = true;
        thread = new Thread()
        {
            private long size = 0L;
            private long savedBytes = 0L;
            private int fileCount=0;

            private long folderSize(File directory)
            {
                long length = 0L;
                File[] arrayOfFile;
                int j = (arrayOfFile = directory.listFiles()).length;
                for (int i = 0; i < j; i++)
                {
                    File file = arrayOfFile[i];
                    if (file.isFile()) {
                        length += file.length();
                    } else {
                        length += folderSize(file);
                    }
                }
                return length;
            }

            public void run()
            {
                try
                {
                    if (!file.exists()){

                        if(!file.getParentFile().mkdirs()||!file.createNewFile()){
                            Log.toConsole("[自动备份]创建备份文件失败");
                        }
                    }
                    Long start=System.currentTimeMillis();
                    zipFolder(base.getAbsolutePath(), file.getAbsolutePath());
                    Long end=System.currentTimeMillis();
                    Bukkit.getConsoleSender().sendMessage("§a[自动备份] 备份完成！耗时："+(end-start)/1000L+"s  共备份"+this.fileCount+"个文件！");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage("§c[自动备份] FAILED TO SAVE BACKUP!");
                }
                int loop = 0;
//                while ((loop < 1000) && (main.backupFolder.list().length > Qiegao.getPluginConfig().getInt("maxBackupsBeforeErase")))
//                {
//                    loop++;
//                    File delete = null;
//                    File[] arrayOfFile;
//                    int j = (arrayOfFile = main.backupFolder.listFiles()).length;
//                    for (int i = 0; i < j; i++)
//                    {
//                        File fl = arrayOfFile[i];
//                        if (delete == null) {
//                            delete = fl;
//                        } else if (fl.lastModified() < delete.lastModified()) {
//                            delete = fl;
//                        }
//                    }
//                    if (delete != null) {
//                        try
//                        {
//                            delete.delete();
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//                }
                Backup.backingUp = false;
            }

            public void zipFolder(String srcFolder, String destZipFile)
                    throws Exception
            {
                ZipOutputStream zip = null;
                FileOutputStream fileWriter = null;

                fileWriter = new FileOutputStream(destZipFile);
                zip = new ZipOutputStream(fileWriter);

                for (World w :Bukkit.getWorlds()) {
                    String temp_path=srcFolder+File.separator+w.getName();
                    Bukkit.getConsoleSender().sendMessage("§a[自动备份] 开始备份世界: " + w.getName());
                    this.size = folderSize(new File(temp_path));
                    this.savedBytes=0L;
//                    zipFolder(temp_path, file.getAbsolutePath());
                    addFolderToZip("", temp_path, zip);
                    Backup.updatePercent(this.size, this.savedBytes);
                }

                zip.flush();
                zip.close();
                log_writer.close();
            }

            private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
            {
                if (Qiegao.getPluginConfig().getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§a[自动备份] Zipping file: §7" + srcFile);
                }
                try
                {
                    File folder = new File(srcFile);
                    this.savedBytes += folder.length();
                    Backup.updatePercent(this.size, this.savedBytes);
                    if (folder.isDirectory())
                    {
                        addFolderToZip(path, srcFile, zip);
                    }
                    else
                    {
                        if (yesterday_log_readers.containsKey(path + "/" + folder.getName())){
                            if (folder.lastModified()/1000<=yesterday_log_readers.get(path + "/" + folder.getName())){
                                log_writer.write(path + "/" + folder.getName()+":"+yesterday_log_readers.get(path + "/" + folder.getName())+"\n");
                                return;
                            }
                        }
                        log_writer.write(path + "/" + folder.getName()+":"+millis+"\n");
                        this.fileCount++;
                        byte[] buf = new byte['?'];

                        FileInputStream in = new FileInputStream(srcFile);
                        zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                        int len;
                        while ((len = in.read(buf)) > 0)
                        {
                            zip.write(buf, 0, len);
                        }
                        in.close();
                    }
                }
                catch (Exception e)
                {
                    Bukkit.getConsoleSender().sendMessage("§c[自动备份] FAILED TO ZIP FILE: §7" + srcFile);
                }
            }

            private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
            {

                if (Qiegao.getPluginConfig().getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§a[自动备份] Zipping folder: §7" + srcFolder);
                }
                try
                {
                    File folder = new File(srcFolder);
                    this.savedBytes += folder.length();
                    Backup.updatePercent(this.size, this.savedBytes);
                    String[] arrayOfString;
                    int j = (arrayOfString = folder.list()).length;
                    for (int i = 0; i < j; i++)
                    {
                        String fileName = arrayOfString[i];
                        if (path.equals("")) {
                            addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
                        } else {
                            addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
                        }
                    }
                }
                catch (Exception e)
                {
                    Bukkit.getConsoleSender().sendMessage("§c[自动备份] FAILED TO ZIP FOLDER: §7" + srcFolder);
                }
            }

        };
        thread.start();
        return "§a开始备份.";
    }

    protected static void updatePercent(long size, long savedBytes)
    {
        double per = savedBytes* 100.0D / size ;
        percent = (int)per;
        if (percent != lastPercent)
        {
            lastPercent = percent;
            Bukkit.getConsoleSender().sendMessage("§a[自动备份] 进度: " + percent + "% " + savedBytes / 1048576L + "/" + size / 1048576L + " MB.");
        }
    }

    public static String cancel()
    {
        if (thread.isAlive()) {
            thread.destroy();

        }
        backingUp = false;

        return "§a取消备份.";
    }

    public static void setTimer(boolean b)
    {
        timer = b;
    }

    public static int getPercent()
    {
        return percent;
    }
}

