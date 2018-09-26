package club.qiegaoshijie.qiegao.util.mysql;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.util.Log;

public class MySQLManager {
    private String ip;
    private String databaseName;
    private String userName;
    private String userPassword;
    private Connection connection;
    private int port;
    public static MySQLManager instance = null;

    public static MySQLManager get() {
        return instance == null ? instance = new MySQLManager() : instance;
    }

    public void enableMySQL()
    {
        ip = Config.getString("mysql.ip");
        databaseName = Config.getString("mysql.db");
        userName = Config.getString("mysql.user");
        userPassword = Config.getString("mysql.pwd");
        port = Integer.parseInt(Config.getString("mysql.port"));
        connectMySQL();
//        String cmd = SQLCommand.CREATE_TABLE1.commandToString();
//        try {
//            PreparedStatement ps = connection.prepareStatement("");
//            doCommand(ps);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    private void connectMySQL()
    {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + databaseName + "?autoReconnect=true&testOnBorrow=true&validationQuery=select 1", userName, userPassword);
        } catch (SQLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        }
    }
    public void doCommand(String s)
    {
        try {
            PreparedStatement ps;
            ps = getCON().prepareStatement(s);
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
            ps = getCON().prepareStatement(s);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("执行指令失败，以下为错误提示");
            e.printStackTrace();
        }
        return null;
    }

    public Connection getCON(){
        if(connection==null){
            connectMySQL();
            Log.toConsole(ip);
        }

        return connection;
    }
    public void close(){
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
