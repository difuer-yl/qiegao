package club.qiegaoshijie.qiegao.util.mysql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.config.FileConfig;
import club.qiegaoshijie.qiegao.util.Log;

public class MySQLManager {
    private String ip;
    private String databaseName;
    private String userName;
    private String userPassword;
    private String flag;
    private Connection c;
    private int port;
    public static MySQLManager instance = null;

    public static MySQLManager get() {
        return instance == null ? instance = new MySQLManager() : instance;
    }

    public MySQLManager(){
        enableMySQL();
    }

    public void enableMySQL()
    {
        ip = Config.getString("mysql.host");
        databaseName = Config.getString("mysql.database");
        userName = Config.getString("mysql.user");
        userPassword = Config.getString("mysql.pwd");
        port = Integer.parseInt(Config.getString("mysql.port"));
        flag = Config.MYSQL_FLAG;
        connectMySQL();
    }

    private void connectMySQL()
    {
        try {
//            c=DriverManager.getConnection("jdbc:mysql://193.112.19.185:3306/qiegaoshijie?useSSL=false&autoReconnect=true&testOnBorrow=true&validationQuery=select 1" , "root", "hN%jP$YW4*6D@*^s");;
            c = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + databaseName + flag, userName, userPassword);
        } catch (SQLException e) {
            // TODO 自动生成的 catch 块
            Log.toConsole("jdbc:mysql://" + ip + ":" + port + "/" + databaseName + flag);
            e.printStackTrace();

        }
    }
    public void doCommand(String s)
    {
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(s);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("执行指令失败，以下为错误提示");
            e.printStackTrace();
        }
    }
    public ResultSet getList(String s)
    {
        try {
            PreparedStatement ps;
            ps = getConnection().prepareStatement(s);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("执行指令失败，以下为错误提示");
            e.printStackTrace();
        }
        return null;
    }

    public Connection getConnection(){
        if(c==null){
            connectMySQL();
            Log.toConsole(ip);
        }

        return c;
    }
    public void close(){
        if(c!=null){
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    public ResultSet  filter(String sql)
    {
        Connection c = null;
        boolean err = false;
        ResultSet rs=null;
        if (Config.DEBUG){
            Log.toConsole(sql);
        }

        try
        {
            c = getConnection();
            Statement stmt = c.createStatement();
            rs = doExecuteQuery(stmt, sql);

        }
        catch (SQLException x)
        {
            err=true;
            return null;
        }
        finally
        {
//            releaseConnection(c, err);
            if (false&&c!=null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return  rs;

    }
    public ResultSet one(String sql){
        return  filter(sql+" limit 0,1");
    }


    public  Boolean insert(String sql){
        Connection c = null;
        PreparedStatement stmt=null;
        boolean err = false;
        if (Config.DEBUG){
            Log.toConsole(sql);
        }
        try
        {
            c = getConnection();

            {
                stmt = c.prepareStatement(sql);
            }
            doExecuteUpdate(stmt);

        }
        catch (SQLException x)
        {
            Log.toConsole("Tile purge error - " + x.getMessage());
            err = true;
            return  false;
        }finally
        {
//            releaseConnection(c, err);
            if (c!=null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
    public  Boolean insert(String sql,Object o){
        Connection c = null;
        PreparedStatement stmt=null;
        boolean err = false;
        try
        {
            c = getConnection();

            stmt = c.prepareStatement(sql);
//            ObjectOutputStream oos =null;
//            try {
//                oos = new ObjectOutputStream(new DataOutputStream());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(bos);
                oos.writeObject(o);
                oos.flush();
                oos.close();
                bos.close();
                byte[] byte_data = bos.toByteArray();
                stmt.setObject(1, byte_data);
//            stmt.setBlob(1,o.);
                doExecuteUpdate(stmt);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        catch (SQLException x)
        {
            Log.toConsole("Tile purge error - " + x.getMessage());
            err = true;
            return  false;
        }finally
        {
//            releaseConnection(c, err);
            if (c!=null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public Boolean update(String sql){
        return insert(sql);
    }

    private ResultSet doExecuteQuery(Statement statement, String sql)
            throws SQLException
    {
        for (;;)
        {
            try
            {
                return statement.executeQuery(sql);
            }
            catch (SQLException x)
            {
                if (!x.getMessage().contains("[SQLITE_BUSY]")) {
                    throw x;
                }
            }
        }
    }

    private int doExecuteUpdate(PreparedStatement statement)
            throws SQLException
    {
        for (;;)
        {
            try
            {
                return statement.executeUpdate();
            }
            catch (SQLException x)
            {
                if (!x.getMessage().contains("[SQLITE_BUSY]")) {
                    throw x;
                }
            }
        }
    }

    private int doExecuteUpdate(Statement statement, String sql)
            throws SQLException
    {
        for (;;)
        {
            try
            {
                return statement.executeUpdate(sql);
            }
            catch (SQLException x)
            {
                if (!x.getMessage().contains("[SQLITE_BUSY]")) {
                    throw x;
                }
            }
        }
    }
}
