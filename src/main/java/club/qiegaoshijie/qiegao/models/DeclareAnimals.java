package club.qiegaoshijie.qiegao.models;

import club.qiegaoshijie.qiegao.Qiegao;
import club.qiegaoshijie.qiegao.util.Log;
import org.bukkit.entity.EntityType;

import javax.swing.text.Style;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeclareAnimals extends Models{
    private int declare_time;
    private String username;
    private String binding;  //隶属于（public为公共）
    private String license;
    private String feature;
    private int status;  //状态（0:未知, 1:存活, 2:丢失, 3:已死亡）

    public DeclareAnimals(){
        setTableName("QieGaoWorld_declareanimals");
    }
    public DeclareAnimals(int id)  {
        setTableName("QieGaoWorld_declareanimals");
//        get(id);
    }
    public DeclareAnimals(String license)  {
        setTableName("QieGaoWorld_declareanimals");
        setLicense(license);

        get(license);
    }


    public int getDeclare_time() {
        return declare_time;
    }


    public void setDeclare_time(Integer declare_time) {
        this.declare_time = declare_time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getColor(String c){
        switch (c){
            case "BLACK":
                return "黑色";
            case "BROWN":
                return "褐色";
            case "CHESTNUT":
                return "栗色";
            case "CREAMY":
                return "奶油色";
            case "DARK_BROWN":
                return "深褐色";
            case "GRAY":
                return "灰色";
            case "WHITE":
                return "白色";
            default:
                return "";
        }
    }

    public String getStyle(String s){
        switch (s){
            case "BLACK_DOTS":
                return "黑色斑点";
            case "none":
                return "";
            case "WHITE_DOTS":
                return "白色斑点";
            case "WHITEFIELD":
                return "白色条纹";
            case "WHITE":
                return "白色袜子或条纹";
            default:
                return "";
        }
    }


    public List<String>  getList(String line)
    {
        List list=this.where("`license` LIKE '\"+line+\"%'").select();

        List<String> list1= new ArrayList<>();
        DeclareAnimals declareAnimals=null;
        for (Object object :list) {
            declareAnimals= (DeclareAnimals) object;
            list1.add(declareAnimals.getLicense().split(" ")[0]);
        }
        return list1;

    }
    public void get(String license)  {
        DeclareAnimals declareAnimals= (DeclareAnimals) this.where("license='"+license+"'").find();
        if (declareAnimals==null)return;
        setFeature(declareAnimals.getFeature());
        setStatus(declareAnimals.getStatus());
        setLicense(declareAnimals.getLicense());
        setUsername(declareAnimals.getUsername());
        setDeclare_time(declareAnimals.getDeclare_time());
        setBinding(declareAnimals.getBinding());
        setId(declareAnimals.getId());


    }

}
