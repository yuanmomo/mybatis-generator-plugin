package com.github.yuanmomo.mybatis.mbg.table;

import org.apache.commons.lang3.ArrayUtils;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * To run MBG(MyBatis Generator), a generatorConfig.xml file is needed. However , every table in
 * database has to config an xml element of &lt;table&gt;&lt;/table&gt; .
 * When comes to lots of tables, it will cost a lot of energy to write this &lt;table&gt;&lt;/table&gt; configuration.
 * <p>
 * Here I provide a tool to generate these configurations for all the tables in single one database.
 */
public abstract class TableXMLPrinter {
    protected String ip;
    protected int port;
    protected String user;
    protected String password;

    /**
     * whether printing the runtimeSchema &lt;property&gt;&lt;/property&gt; in &lt;table&gt;&lt;/table&gt;.
     */
    protected boolean isPrintSchema;

    public TableXMLPrinter(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    /**
     * @param dbSchemaArray
     */
    public abstract List<String> print(String... dbSchemaArray);


    /**
     * Get the jdbc url.
     *
     * @param ip
     * @param port
     * @param dbSchema
     * @return
     */
    public abstract String getUrl(String ip, int port, String dbSchema);

    /**
     * loop the dbSchemaArray and print.
     *
     * @param driver
     * @param dbSchemaArray
     */
    public List<String> printWithDriver(
            String driver,
            String... dbSchemaArray) {
        if (ArrayUtils.isEmpty(dbSchemaArray)) {
            return Collections.emptyList();
        }
        if(! isPrintSchema && dbSchemaArray.length > 1){
            isPrintSchema = true;
        }
        List<String> allDbSchema = new ArrayList<>();
        for (String db : dbSchemaArray) {
            List<String> single = this.printTables(db, driver, getUrl(ip, port, db), user, password);
            if (single != null && single.size() > 0) {
                allDbSchema.addAll(single);
            }
        }
        return allDbSchema;
    }

    /**
     * print single one database(scheme).
     *
     * @param schema
     * @param driver
     * @param url
     * @param user
     * @param password
     * @return
     */
    public List<String> printTables(String schema, String driver, String url, String user, String password) {
        List<String> output = new ArrayList<String>();
        // jdbc connection
        Connection con = null;
        // all the tables
        ResultSet tableListRs = null;
        // all the columns in one table
        ResultSet columListRS = null;
        // statement to exec the sql query
        PreparedStatement psmt = null;

        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            DatabaseMetaData metadata = con.getMetaData();

            // 从数据库的元数据中取得所有的边表名
            tableListRs = metadata.getTables(null, schema, "%", new String[]{"TABLE"});
            while (tableListRs.next()) {
                String tableName = tableListRs.getString(3);
                String javaBeanName = JavaBeansUtil.getCamelCaseString(tableName, true);

                output.add(String.format("\t\t<table tableName=\"%s\" domainObjectName=\"%s\">",tableName,javaBeanName));
                if(isPrintSchema){
                    output.add(String.format("\t\t\t<property name=\"runtimeSchema\" value=\"%s\"/>",schema));
                }
//                output.add("\t\t\t<property name=\"generatedBusinessName\" value=\"" + javaBeanName + "Business\"/>");
//                output.add("\t\t\t<property name=\"generatedControllerName\" value=\"" + javaBeanName + "Controller\"/>");

                // 判断表是否含有id的主键
                psmt = con.prepareStatement("desc " + tableName);
                columListRS = psmt.executeQuery();
                while (columListRS.next()) {
                    String extra = columListRS.getString("Extra");
                    if (extra.equalsIgnoreCase("auto_increment")) {
                        String col = columListRS.getString("Field").toLowerCase();
                        output.add("\t\t\t<generatedKey column=\"" + col + "\" sqlStatement=\"MySql\" identity=\"true\"/>");
                    }
                }
                output.add("\t\t</table>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                psmt.close();
                columListRS.close();
                tableListRs.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPrintSchema() {
        return isPrintSchema;
    }

    public void setPrintSchema(boolean printSchema) {
        this.isPrintSchema = printSchema;
    }
}
