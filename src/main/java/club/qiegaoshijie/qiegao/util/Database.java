package club.qiegaoshijie.qiegao.util;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.FileConfig;
import club.qiegaoshijie.qiegao.config.Messages;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {
    private static Connection connection;
    public Database(){
        connection=getConnection();
    }
    public static Connection getConnection()
    {
        try
        {
            if(connection!=null)return connection;
            FileConfig Config= Qiegao.getPluginConfig();
//            if (((Integer)Config.config.get("use-mysql")).intValue() == 1)
//            {
                String database = "jdbc:mysql://" +Config.getString("mysql.host") + ":" + Config.getString("mysql.port") + "/" + Config.getString("mysql.database") + "?useUnicode=true&characterEncoding=utf-8&connectTimeout=10000&useSSL=false";
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection(database, Config.getString("mysql.user"), Config.getString("mysql.pwd"));

                Statement statement = connection.createStatement();
                statement.executeUpdate("SET NAMES 'utf8'");

                statement.close();
//            }
//            else
            {
//                String database = "jdbc:sqlite:" + Messages.getString("sqlite") + "";
//                Class.forName("org.sqlite.JDBC");
//                connection = DriverManager.getConnection(database);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return connection;
    }
}
