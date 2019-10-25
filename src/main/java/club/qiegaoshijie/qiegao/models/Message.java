package club.qiegaoshijie.qiegao.models;


public class Message extends Models {

    private String content;
    private int num;
    private int status;

    public Message(){
        setTableName("QieGaoWorld_message");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public int isStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
