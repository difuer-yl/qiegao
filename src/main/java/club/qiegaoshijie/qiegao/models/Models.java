package club.qiegaoshijie.qiegao.models;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.mysql.MySQLManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Models {

    private String tableName;
    private  int id;
    private List getFiledsInfo(Object o){
        Field[] fields=o.getClass().getDeclaredFields();
        String[] fieldNames=new String[fields.length];
        List list = new ArrayList();
        Map infoMap=null;
        for(int i=0;i<fields.length;i++){
            infoMap = new HashMap();
            infoMap.put("type", fields[i].getType());
            infoMap.put("name", fields[i].getName());
            infoMap.put("value", getFieldValueByName(fields[i].getName(), o));
            list.add(infoMap);
        }
        return list;
    }
    private Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Boolean insert(){
        boolean b=Qiegao.getSm().insert("insert into `"+getTableName()+"`"+genBackSql());
        if (!b){
            Log.toConsole("insert into `"+getTableName()+"`"+genBackSql());
        }
        return b;
    }

    private  void query(String sql){
        MySQLManager.get().doCommand(sql);
    }
    protected ResultSet getOne(String sql){
        return  Qiegao.getSm().one(sql);
    }
    protected ResultSet _getList(String sql) throws SQLException {
        return Qiegao.getSm().filter(sql);
    }
    protected String getTableName(){
        return tableName.toLowerCase();
    }



    private String genBackSql(){
        List fileds=getFiledsInfo(this);
        String sql="";
        String filed="";
        for (int i=0;i<fileds.size();i++){
            Map n=(Map) fileds.get(i);
            if(n.get("name")=="_tableName")continue;
            if (n.get("name").toString().indexOf("_")==0)continue;
            if(n.get("name")=="id" && n.get("value").equals(0))continue;
//            if(n.get("type") instanceof String){
                sql +=" '"+n.get("value")+"', ";
//            }else{
//                sql +=" "+n.get("value")+", ";
//            }
            filed +=" `"+n.get("name")+"`, ";
        }
        filed=filed.substring(0,filed.length()-2);
        sql=sql.substring(0,sql.length()-2);
        return "("+filed+") VALUES ("+sql+")";

    }
    public Boolean replace(){
        return Qiegao.getSm().insert("replace into `"+getTableName()+"`"+genBackSql());
    }

    public  Boolean update(String sql){
        return Qiegao.getSm().update(sql);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
