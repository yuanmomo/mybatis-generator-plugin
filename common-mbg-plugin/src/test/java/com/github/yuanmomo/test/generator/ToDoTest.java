package com.github.yuanmomo.test.generator;

import com.github.yuanmomo.test.generator.bean.ToDo;
import com.github.yuanmomo.test.generator.bean.ToDoParam;
import com.github.yuanmomo.test.generator.mybatis.mapper.ToDoMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hongbin.Yuan on 2017-06-09 02:49.
 */

public class ToDoTest extends BaseTest {

    @Test
    public void testSelect() {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        try {
            execBeforeCase("todo.sql");

            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            ToDo todo = new ToDo();
            todo.setId(1L);
            todo.setRemark(888L);
            todo.setToDo(7777);
            Assert.assertTrue(mapper.insertSelective(todo) > 0);

            ToDoParam toDoParam = new ToDoParam();
            toDoParam.createCriteria();
            toDoParam.setStart(0);
            toDoParam.setStart(1);
            toDoParam.setOrderByClause("id desc");
            List<ToDo> toDoList = mapper.selectByExample(toDoParam);
            Assert.assertTrue(toDoList.size() == 1);
            Assert.assertTrue(toDoList.get(0) != null);
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sqlSession.rollback(true);
            sqlSession.close();
        }
    }

    @Test
    public void testBatchInsert() {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        try {

            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            List<ToDo> toDoList = new ArrayList<>();
            ToDo todo1 = new ToDo();
            todo1.setToDo(1);
            todo1.setRemark(1L);
            toDoList.add(todo1);

            ToDo todo2 = new ToDo();
            todo2.setToDo(2);
            todo2.setRemark(2L);
            toDoList.add(todo2);

            int count = mapper.batchInsert(toDoList);
            System.out.println(toDoList);

            Assert.assertTrue(count == 2);
            for (ToDo toDo : toDoList) {
                Assert.assertTrue(toDo.getId() != null);
            }
        } finally {
            sqlSession.rollback(true);
            sqlSession.close();
        }
    }

    @Test
    public void testBatchInsertByConection() {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        Connection connection = null;
        try {
            connection = sqlSession.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement pstm = connection.prepareStatement(
                    "insert into table_to_do (to_do,remark) values(?, ?), (?, ?), (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            pstm.setString(1, "1");
            pstm.setString(2, "1");


            pstm.setString(3, "2");
            pstm.setString(4, "2");

            pstm.setString(5, "3");
            pstm.setString(6, "3");

            pstm.addBatch();
            pstm.executeBatch();

            ResultSet rs = pstm.getGeneratedKeys();
            while (rs.next()) {
                Object value = rs.getObject(1);
                System.out.println(value);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            sqlSession.rollback(true);
            sqlSession.close();
        }
    }
}