package club.qiegaoshijie.qiegao.util.mysql;

public enum SQLCommand {
    CREATE_TABLE1(
            "CREATE TABLE IF NOT EXISTS `TABLE1` (" +
                    "`int` INT UNSIGNED AUTO_INCREMENT," +
                    "`string` VARCHAR(100) NULL DEFAULT NULL," +
                    "PRIMARY KEY (`int`))"
    ),
    //这句话就算如果不存在TABLE1的表，创建之
    //有以下列数
    //int列，存储数据
    //string列，存储字符串
    //主键为int
    ADD_DATA(
            "INSERT INTO `TABLE1` " +
                    "(`int`, `string`)" +
                    "VALUES (?, \'?\')"
    ),
    //添加一行数据，包含2个值
    DELETE_DATA(
            "DELETE FROM `TABLE1` WHERE `int` = ?"
    ),
    //删除主键为[int]的一行数据
    SELECT_DATA(
            "SELECT * FROM `TABLE1` WHERE `int` = ?"
    );
    //查找主键为[int]的一行数据

    /*
     * 这里可以添加更多的MySQL命令，格式如下
     * COMMAND_NAME(
     *    "YOUR_COMMAND_HERE" +
     *    "YOUR_COMMAND_HERE"
     * );
     */

    private String command;

    SQLCommand(String command)
    {
        this.command = command;
    }
    public String commandToString()
    {
        return command;
    }
}