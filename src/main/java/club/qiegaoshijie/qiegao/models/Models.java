package club.qiegaoshijie.qiegao.models;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.util.Log;
import club.qiegaoshijie.qiegao.util.mysql.MySQLManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Models {

    private static String _tableName;
    private int id;
    protected String _pk="id";
    protected MySQLManager _sm=null;
    HashMap<String,Field> _fields=new HashMap<>();
    private String _order="";
    private String _where="";
    private String _limit="";

    public Models(){
        getFiledsInfo(this);
        _sm=Qiegao.getSm();
//        _sm=new MySQLManager();
    }



    private HashMap<String,Field>  getFiledsInfo(Object o){

        return getFiledsInfo(o,false);
    }
    private HashMap<String,Field>  getFiledsInfo(Object o,Boolean boole){
        if (boole||this._fields.size()==0){
            _fields.clear();
            Class tempClass=this.getClass();

            List<Field> fieldList = new ArrayList<>() ;
            while (tempClass != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
                fieldList.addAll(Arrays.asList(tempClass .getDeclaredFields()));
                tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
            }
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> infoMap=null;
            for(int i=0;i<fieldList.size();i++){
                if (fieldList.get(i).getName().toString().indexOf("_")==0)continue;
                _fields.put(fieldList.get(i).getName(),fieldList.get(i));
            }
        }
        return this._fields;

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
        this._tableName = tableName;
    }

    public Boolean insert(){
        boolean b=Qiegao.getSm().insert("insert into `"+getTableName()+"`"+getBackSql());
        if (!b){
            Log.toConsole("insert into `"+getTableName()+"`"+getBackSql());
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
//    protected String getTableName(){
//        return tableName.toLowerCase();
//    }
    protected static String getTableName(){
        return _tableName;
    }



    private String getBackSql(){
        HashMap<String,Field>  fileds=getFiledsInfo(this);
        String sql="";
        String filed="";
        for (int i=0;i<fileds.size();i++){
            Field n= fileds.get(i);
            if(n.getName()=="_tableName")continue;
            if (n.getName().toString().indexOf("_")==0)continue;
            if(n.getName()=="id" && getFieldValueByName(n.getName(), this).equals(0))continue;
//            if(n.get("type") instanceof String){
                sql +=" '"+getFieldValueByName(n.getName(), this).equals(0)+"', ";
//            }else{
//                sql +=" "+n.get("value")+", ";
//            }
            filed +=" `"+n.getName()+"`, ";
        }
        filed=filed.substring(0,filed.length()-2);
        sql=sql.substring(0,sql.length()-2);
        return "("+filed+") VALUES ("+sql+")";

    }
    public Boolean replace(){
        return Qiegao.getSm().insert("replace into `"+getTableName()+"`"+getBackSql());
    }

    public  Boolean update(String sql){
        return Qiegao.getSm().update(sql);
    }

    private String getWhere(){
        HashMap<String,Field> fileds=getFiledsInfo(this,true);
        String sql=" 1=1 ";
        for (String s :fileds.keySet() ) {
            Field n= fileds.get(s);
            if(n.getName().indexOf("_")!=-1)continue;

//            if(n.get("name")=="id" && n.get("value").equals(0))continue;
//            if(n.get("type") instanceof String){
            Object value=getFieldValueByName(n.getName(), this);
            if (value==null || value.equals(0)) continue;
            sql +=" and (`"+n.getName()+"` = '"+value+"') ";
        }
//        for (int i=0;i<fileds.size();i++){
//            Field n= fileds.get(i);
//            if(n.getName().indexOf("_")!=-1)continue;
//
////            if(n.get("name")=="id" && n.get("value").equals(0))continue;
////            if(n.get("type") instanceof String){
//            if (getFieldValueByName(n.getName(), this)==null || getFieldValueByName(n.getName(), this).equals(0)) continue;
//            sql +=" and (`"+n.getName()+"` = '"+getFieldValueByName(n.getName(), this)+"') ";
////
//        }


        return sql;

    }

    private Models bindData(ResultSet l){

        try {
            Models o= (Models) Class.forName(this.getClass().getName()).newInstance();
            for (String i :_fields.keySet()) {
                    set(o,"set"+captureName(_fields.get(i).getName()),l.getObject(_fields.get(i).getName()));
            }
            return o;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
//                    set(_fields.get(i).getName(),l.getObject(_fields.get(i).getName()));
    }

    public  Models find(Object px){
        setPk(px);
        ResultSet l= _sm.one("select * from "+getTableName()+" where "+getWhere());
        try {
            if(l!=null && l.next()){
                Models o=bindData(l);

//                Class a=this.getClass();
                return o;
            }
            l.close();
        } catch (SQLException  e) {
            e.printStackTrace();
        }

        return  null;
    }

    public Models find(){
        String sql="select * from "+getTableName();

        if (this._where!=""){
            sql +=" where " + this._where;
        }
        if (this._order != ""){
            sql +=" order by "+this._order;
        }
        System.out.println(sql);
        ResultSet l= _sm.one(sql);
        List<Models> os=new ArrayList<>();
        clear();
        try {
            if(l!=null && l.next()){
                Models o=bindData(l);

//                Class a=this.getClass();
                return o;
            }
            l.close();

        } catch (SQLException  e) {
            e.printStackTrace();
        }

        return null;
    }

    public Models order(String fieldname){
        return order(fieldname,"ASC");
    }
    public Models order(String fieldname,String order){
        this._order=" "+fieldname+" "+order;
        return this;
    }

    public Models where(String where){
        this._where=where;
        return this;
    }
    public Models limit(String limit){
        this._limit=limit;
        return this;
    }

    public List<Models> select(){
        String sql="select * from "+getTableName();

        if (this._where!=""){
            sql +=" where " + this._where;
        }
        if (this._order != ""){
            sql +=" order by "+this._order;
        }
        if (this._limit != ""){
            sql +=" limit "+this._limit;
        }
        ResultSet l= _sm.filter(sql);
        List<Models> os=new ArrayList<>();
        clear();
        try {
            while (l!=null&&l.next()){
                os.add(bindData(l));
            }
            l.close();
            return os;
        } catch (SQLException  e) {
            e.printStackTrace();
        }

        return os;

    }

    public int count(){
        String sql="select count(`id`) as count from "+getTableName();

        if (this._where!=""){
            sql +=" where " + this._where;
        }
        if (this._order != ""){
            sql +=" order by "+this._order;
        }
        if (this._limit != ""){
            sql +=" limit "+this._limit;
        }
        ResultSet l= _sm.filter(sql);
        clear();
        try {
            if(l!=null && l.next()){
                l.close();
                return l.getInt("count");
            }
            l.close();
        } catch (SQLException  e) {
            e.printStackTrace();
        }

        return 0;
    }


    private void clear(){
        this._limit="";
        this._order="";
        this._where="";
    }



    public void setAttr(HashMap<String,String> filed,Object value){
        Class clazz = this.getClass();
        try {
//            Method m1 = clazz.getDeclaredMethod(filed.get("name"));
//            m1.invoke(this,changeType(filed.get("type"),(String) value));
            set(this,"set"+captureName(filed.get("name")),value);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setAttr(String name,Object value){
        Class clazz = this.getClass();
        try {
            set(this,"set"+captureName(name),value);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private int getInt(String value){
        return Integer.valueOf(value);
    }
    private Object changeType(String type,String value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method=this.getClass().getDeclaredMethod("get"+captureName(type));
        return method.invoke(value);
    }
    public static String captureName(String name) {
        char[] cs=name.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public  void setPk(Object id) {
        try {
            set(this,"set"+captureName(this._pk),id);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public  void set(Object info,String fun,Object value) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        Method method = info.getClass().getMethod(fun,Class.forName(value.getClass().getCanonicalName()));
        method.invoke(info, value);

    }
    public void set(String name,Object value) throws IllegalAccessException {
        _fields.get(name).set(this,value);
    }

    public  Object get(Object info,String fun) throws Exception {
        Method method = info.getClass().getMethod(fun);
        return method.invoke(info);
    }

}
