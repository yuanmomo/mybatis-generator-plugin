package com.github.yuanmomo.test.generator;

/**
 * Created by Hongbin.Yuan on 2017-06-09 02:48.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;

public class BaseTest {

    protected static SqlSessionFactory sqlSessionFactory;

    @BeforeClass
    public static void setUp() throws Exception {
        // create an SqlSessionFactory
        InputStream inputStream = null;
        try {
            System.out.println(String.format(" Current run path: %s ",new File(".").getAbsoluteFile()));
            inputStream = new FileInputStream("src/test/resources/mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            inputStream.close();
        }
    }

//    /**
//     *  run a sql script.
//     *
//     * @param scriptFile
//     * @throws SQLException
//     * @throws IOException
//     */
//    protected static void execBeforeCase(String scriptFile) throws SQLException, IOException {
//        SqlSession session = null;
//        Connection conn = null;
//        Reader reader = null;
//        try {
//            session = sqlSessionFactory.openSession();
//            conn = session.getConnection();
//            System.out.println(String.format(" Current run path: %s ",new File(".").getAbsoluteFile()));
//            reader = Resources.getResourceAsReader("sql/" + scriptFile);
//            ScriptRunner runner = new ScriptRunner(conn);
//            runner.setLogWriter(null);
//            runner.runScript(reader);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            conn.close();
//            reader.close();
//            session.close();
//        }
//    }
}
