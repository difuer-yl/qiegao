package club.qiegaoshijie.qiegao.util;

import club.qiegaoshijie.qiegao.config.Messages;

import java.sql.Connection;
import java.sql.DriverManager;

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
//            if (((Integer)Config.config.get("use-mysql")).intValue() == 1)
//            {
//                String database = "jdbc:mysql://" + Config.host + ":" + Config.port + "/" + Config.database + "?useUnicode=true&characterEncoding=utf-8&connectTimeout=10000&useSSL=false";
//                Class.forName(Config.driver).newInstance();
//                connection = DriverManager.getConnection(database, Config.username, Config.password);
//
//                Statement statement = connection.createStatement();
//                statement.executeUpdate("SET NAMES 'utf8'");
//
//                statement.close();
//            }
//            else
            {
                String database = "jdbc:sqlite:" + Messages.getString("sqlite") + "";
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(database);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return connection;
    }
}
