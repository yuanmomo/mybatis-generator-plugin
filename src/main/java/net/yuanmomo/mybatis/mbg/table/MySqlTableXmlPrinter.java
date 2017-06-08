package net.yuanmomo.mybatis.mbg.table;

import java.util.List;

/**
 * Created by Hongbin.Yuan on 2017-06-09 01:16.
 */

public class MySqlTableXmlPrinter extends TableXMLPrinter{
    private static final String MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String MYSQL_JDBC_URL = "jdbc:mysql://${host}:${port}/${db}?useUnicode=true&characterEncoding=utf-8";

    private static final String MYSQL_JDBC_DEFAULT_HOST = "127.0.0.1";
    private static final int MYSQL_JDBC_DEFAULT_PORT = 3306;


    public MySqlTableXmlPrinter( String user, String password) {
        super(MYSQL_JDBC_DEFAULT_HOST, MYSQL_JDBC_DEFAULT_PORT, user, password);
    }

    public MySqlTableXmlPrinter(String ip, int port, String user, String password) {
        super(ip, port, user, password);
    }

    @Override
    public List<String> print(String... dbSchemaArray) {
        return this.printWithDriver(MYSQL_JDBC_DRIVER,dbSchemaArray);
    }

    @Override
    public String getUrl(String ip, int port, String dbSchema) {
        return MYSQL_JDBC_URL.replace("${host}",ip)
                .replace("${port}",String.valueOf(port))
                .replace("${db}",dbSchema) ;
    }
}
