package com.github.yuanmomo.mybatis.mbg.table;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import com.github.yuanmomo.mybatis.mbg.util.PropertiesUtil;

import lombok.Data;

/**
 * To run MBG(MyBatis Generator), a generatorConfig.xml file is needed. However , every table in
 * database has to config an xml element of &lt;table&gt;&lt;/table&gt; .
 * When comes to lots of tables, it will cost a lot of energy to write this &lt;table&gt;&lt;/table&gt; configuration.
 * <p>
 * Here I provide a tool to generate these configurations for all the tables in single one database.
 */
@Data
public class TableXMLPrinter {

    /**
     * Whether printing the runtimeSchema.
     */
    protected static boolean isPrintSchema;

    /**
     * loop the dbSchemaArray and print.
     *
     * @param dbSchemaArray database schema array.
     */
    public static List<String> print(String propertiesFile, String... dbSchemaArray) throws IOException {
        Properties properties = PropertiesUtil.load(propertiesFile);

        if (ArrayUtils.isEmpty(dbSchemaArray)) {
            return Collections.emptyList();
        }
        if(! isPrintSchema && dbSchemaArray.length > 1){
            isPrintSchema = true;
        }
        String driver = PropertiesUtil.getStringProp(properties,"driver");
        String url = PropertiesUtil.getStringProp(properties,"url");
        String username = PropertiesUtil.getStringProp(properties,"username");
        String password = PropertiesUtil.getStringProp(properties,"password");

        List<String> allDbSchema = new ArrayList<>();
        for (String db : dbSchemaArray) {
            List<String> single = printTables(db, driver, url, username, password);
            if (single != null && single.size() > 0) {
                allDbSchema.addAll(single);
            }
        }
        return allDbSchema;
    }

    /**
     * print single one database(scheme).
     *
     * @param driver jdbc driver
     * @param url   jdbc url
     * @param user   database username.
     * @param password database password.
     * @return tables xml configurations.
     */
    public static List<String> printTables(String schema, String driver, String url, String user, String password) {
        List<String> output = new ArrayList<String>();
        // jdbc connection
        Connection con = null;
        // all the tables
        List<String> tableList = new ArrayList<>();
        // all the columns in one table
        ResultSet columListRS = null;
        // statement to exec the sql query
        PreparedStatement psmt = null;

        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            //Creating a Statement object
            psmt = con.prepareStatement("show tables");
            //Retrieving the data
            ResultSet rs = psmt.executeQuery();

            System.out.println("------------------------------------------");
            System.out.println("Tables in the current database: ");
            while(rs.next()) {
                String tableName = rs.getString(1);

                tableList.add(tableName);
                System.out.println(String.format("Find table: %s", tableName));
            }

            System.out.println("------------------------------------------");
            // 从数据库的元数据中取得所有的边表名
            for (String tableName : tableList) {
                String javaBeanName = JavaBeansUtil.getCamelCaseString(tableName, true);

                output.add(String.format("\t\t<table tableName=\"%s\" domainObjectName=\"%s\">",tableName,javaBeanName));
                if(isPrintSchema){
                    output.add(String.format("\t\t\t<property name=\"runtimeSchema\" value=\"%s\"/>",schema));
                }

                System.out.println(String.format("Print table : %s", tableName));
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
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return output;
    }
}
