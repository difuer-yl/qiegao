package club.qiegaoshijie.qiegao.models;

public class User extends Models {
    private String username;
    private String nickname;
    private String uuid;
    private int qqnumber;


    public User(){
        setTableName("QieGaoWorld_user");
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getQqnumber() {
        return qqnumber;
    }

    public void setQqnumber(Integer qqnumber) {
        this.qqnumber = qqnumber;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
