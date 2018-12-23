package club.qiegaoshijie.qiegao.util.sqlite;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.config.Config;
import club.qiegaoshijie.qiegao.config.Messages;
import club.qiegaoshijie.qiegao.models.DeclareAnimals;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.Tools;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.inventory.ItemStack;

public class SqliteManager
{
    private String connectionString;
    private String databaseFile;
    private static final int POOLSIZE = 1;
    private Connection[] cpool = new Connection[1];
    private int cpoolCount = 0;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public SqliteManager(String databaseFile){
        init(databaseFile);
    }

    public boolean init(String databaseFile)
    {

        this.databaseFile = databaseFile;
        this.connectionString = ("jdbc:sqlite:" + this.databaseFile);
        Log.toConsole("Opening SQLite file " + this.databaseFile + " as map store",true);
        try
        {
            Class.forName("org.sqlite.JDBC");
            return true;
        }
        catch (ClassNotFoundException cnfx)
        {
            Log.toConsole("SQLite-JDBC classes not found - sqlite data source not usable");
        }
        return false;
    }





    private HashMap<String, Integer> mapKey = new HashMap();

    private Connection getConnection()
            throws SQLException
    {


        Connection c = null;
        if (c == null) {
            c = DriverManager.getConnection(this.connectionString);
//            configureConnection(c);
        }

        return c;


    }

    private static Connection configureConnection(Connection conn)
            throws SQLException
    {
        Statement statement = conn.createStatement();
        statement.execute("PRAGMA journal_mode = WAL;");
//        statement.execute("PRAGMA journal_size_limit = 10000;");
        statement.close();
        return conn;
    }

    private void releaseConnection(Connection c, boolean err)
    {
        if (c == null) {
            return;
        }
        synchronized (this.cpool)
        {
            if (!err) {
                for (int i = 0; i < 2; i++) {
                    if (this.cpool[i] == null)
                    {
                        this.cpool[i] = c;
                        c = null;
                        this.cpool.notifyAll();
                        break;
                    }
                }
            }
            if (c != null)
            {
                try
                {
                    c.close();
                }
                catch (SQLException localSQLException) {}
                this.cpoolCount -= 1;
                this.cpool.notifyAll();
            }
        }
    }






    public ResultSet  filter(String sql)
    {
        Connection c = null;
        boolean err = false;
        ResultSet rs=null;
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
//
//    public boolean setPlayerFaceImage(String playername, PlayerFaces.FaceType facetype, BufferOutputStream encImage)
//    {
//        Connection c = null;
//        boolean err = false;
//        boolean exists = hasPlayerFaceImage(playername, facetype);
//        if ((encImage == null) && (!exists)) {
//            return false;
//        }
//        try
//        {
//            c = getConnection();
//            PreparedStatement stmt;
//            if (encImage == null)
//            {
//                PreparedStatement stmt = c.prepareStatement("DELETE FROM Faces WHERE PlayerName=? AND TypeIDx=?;");
//                stmt.setString(1, playername);
//                stmt.setInt(2, facetype.typeID);
//            }
//            else if (exists)
//            {
//                PreparedStatement stmt = c.prepareStatement("UPDATE Faces SET Image=? WHERE PlayerName=? AND TypeID=?;");
//                stmt.setBytes(1, encImage.buf);
//                stmt.setString(2, playername);
//                stmt.setInt(3, facetype.typeID);
//            }
//            else
//            {
//                stmt = c.prepareStatement("INSERT INTO Faces (PlayerName,TypeID,Image) VALUES (?,?,?);");
//                stmt.setString(1, playername);
//                stmt.setInt(2, facetype.typeID);
//                stmt.setBytes(3, encImage.buf);
//            }
//            doExecuteUpdate(stmt);
//            stmt.close();
//        }
//        catch (SQLException x)
//        {
//            Log.severe("Face write error - " + x.getMessage());
//            err = true;
//        }
//        finally
//        {
//            releaseConnection(c, err);
//        }
//        return !err;
//    }
//
//    private void processPurgeMapTiles(DynmapWorld world, MapType map, MapType.ImageVariant var)
//    {
//        Connection c = null;
//        boolean err = false;
//        Integer mapkey = getMapKey(world, map, var);
//        if (mapkey == null) {
//            return;
//        }
//        try
//        {
//            c = getConnection();
//
//            Statement stmt = c.createStatement();
//
//            doExecuteUpdate(stmt, "DELETE FROM Tiles WHERE MapID=" + mapkey + ";");
//            stmt.close();
//        }
//        catch (SQLException x)
//        {
//            Log.severe("Tile purge error - " + x.getMessage());
//            err = true;
//        }
//        finally
//        {
//            releaseConnection(c, err);
//        }
//    }
//
//    public boolean setPlayerFaceImage(String playername, PlayerFaces.FaceType facetype, BufferOutputStream encImage)
//    {
//        Connection c = null;
//        boolean err = false;
//        boolean exists = hasPlayerFaceImage(playername, facetype);
//        if ((encImage == null) && (!exists)) {
//            return false;
//        }
//        try
//        {
//            c = getConnection();
//            PreparedStatement stmt;
//            if (encImage == null)
//            {
//                PreparedStatement stmt = c.prepareStatement("DELETE FROM Faces WHERE PlayerName=? AND TypeIDx=?;");
//                stmt.setString(1, playername);
//                stmt.setInt(2, facetype.typeID);
//            }
//            else if (exists)
//            {
//                PreparedStatement stmt = c.prepareStatement("UPDATE Faces SET Image=? WHERE PlayerName=? AND TypeID=?;");
//                stmt.setBytes(1, encImage.buf);
//                stmt.setString(2, playername);
//                stmt.setInt(3, facetype.typeID);
//            }
//            else
//            {
//                stmt = c.prepareStatement("INSERT INTO Faces (PlayerName,TypeID,Image) VALUES (?,?,?);");
//                stmt.setString(1, playername);
//                stmt.setInt(2, facetype.typeID);
//                stmt.setBytes(3, encImage.buf);
//            }
//            doExecuteUpdate(stmt);
//            stmt.close();
//        }
//        catch (SQLException x)
//        {
//            Log.severe("Face write error - " + x.getMessage());
//            err = true;
//        }
//        finally
//        {
//            releaseConnection(c, err);
//        }
//        return !err;
//    }






    public String getMarkersURI(boolean login_enabled)
    {
        return "standalone/SQLite_markers.php?marker=";
    }

    public String getTilesURI(boolean login_enabled)
    {
        return "standalone/SQLite_tiles.php?tile=";
    }


    private ResultSet doExecuteQuery(PreparedStatement statement)
            throws SQLException
    {
        for (;;)
        {
            try
            {
                return statement.executeQuery();
            }
            catch (SQLException x)
            {
                if (!x.getMessage().contains("[SQLITE_BUSY]")) {
                    throw x;
                }
            }
        }
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
