package net.yuanmomo.test.generator.mybatis.mapper;

import net.yuanmomo.test.generator.BaseTest;
import net.yuanmomo.test.generator.bean.ToDo;
import net.yuanmomo.test.generator.bean.ToDoParam;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Hongbin.Yuan on 2017-06-09 02:49.
 */

public class ToDoTest extends BaseTest {

    @Test
    public void testSelect() {
        SqlSession sqlSession = null;
        try {
            execBeforeCase("todo.sql");
            sqlSession = sqlSessionFactory.openSession();
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
            sqlSession.close();
        }
    }

}
