package com.github.yuanmomo.test.generator.mybatis.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.yuanmomo.test.generator.BaseTest;
import com.github.yuanmomo.test.generator.bean.ToDo;
import com.github.yuanmomo.test.generator.bean.ToDoParam;

/**
 *
 */

public class ToDoMapperTest extends BaseTest {

    public static final String TABLE_NAME = "table_to_do";
    public static SqlSession sqlSession = null;

    @Before
    public void before() {
        sqlSession = sqlSessionFactory.openSession(false);
    }

    @After
    public void after() {
        sqlSession.rollback(true);
        sqlSession.close();
    }

    @Test
    public void countByExample() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);

            mapper.insert(TABLE_NAME, ToDo.init(1, 1000001L, "remark1", null));
            mapper.insert(TABLE_NAME, ToDo.init(2, 1000002L, "remark2", null));

            long count = mapper.countByExample(TABLE_NAME, new ToDoParam());
            Assert.assertEquals(count, 2);
            System.out.println(String.format("countByExample count:[%s]", count));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void deleteByExample() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);

            mapper.insert(TABLE_NAME, ToDo.init(1, 1000001L, "remark1", null));
            mapper.insert(TABLE_NAME, ToDo.init(2, 1000002L, "remark2", null));
            mapper.insert(TABLE_NAME, ToDo.init(3, 1000003L, "remark3", null));

            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark2").andToDoEqualTo(2);
            long deleteCount = mapper.deleteByExample(TABLE_NAME, param);
            Assert.assertEquals(deleteCount, 1);

            long selectCount = mapper.countByExample(TABLE_NAME, new ToDoParam());
            Assert.assertEquals(selectCount, 2);

            List<ToDo> select = mapper.selectByExample(TABLE_NAME, param);
            Assert.assertEquals(select.size(), 0);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void deleteByPrimaryKey() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);

            mapper.insert(TABLE_NAME, ToDo.init(1, 1000001L, "remark1", null));
            mapper.insert(TABLE_NAME, ToDo.init(2, 1000002L, "remark2", null));
            mapper.insert(TABLE_NAME, ToDo.init(3, 1000003L, "remark3", null));

            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark2").andToDoEqualTo(2);
            List<ToDo> select = mapper.selectByExample(TABLE_NAME, param);
            Assert.assertEquals(select.size(), 1);

            mapper.deleteByPrimaryKey(TABLE_NAME, select.get(0).getId());

            List<ToDo> selectAfterDelete = mapper.selectByExample(TABLE_NAME, param);
            Assert.assertEquals(selectAfterDelete.size(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void insert() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            ToDo todo = ToDo.init(1, 1000000L, "remark", null);

            // insert
            int count = mapper.insert(TABLE_NAME, todo);

            // select
            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(1);
            Optional<ToDo> inserted = mapper.getOneByExample(TABLE_NAME, param);

            // assert insert count
            Assert.assertTrue(count == 1);

            // assert inserted object
            Assert.assertTrue(inserted.isPresent());
            Assert.assertEquals(inserted.get().getRemark(), todo.getRemark());
            Assert.assertEquals(inserted.get().getToDo(), todo.getToDo());
            Assert.assertEquals(inserted.get().getVersion(), todo.getVersion());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void insertSelective() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            ToDo todo = ToDo.init(1, null, "remark", null);

            // insert
            int count = mapper.insertSelective(TABLE_NAME, todo);

            // select
            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(1);
            Optional<ToDo> inserted = mapper.getOneByExample(TABLE_NAME, param);

            // assert insert count
            Assert.assertTrue(count == 1);

            // assert inserted object
            Assert.assertTrue(inserted.isPresent());
            Assert.assertEquals(inserted.get().getRemark(), todo.getRemark());
            Assert.assertEquals(inserted.get().getToDo(), todo.getToDo());
            Assert.assertTrue(inserted.get().getVersion() == 0L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void selectByExampleWithBLOBs() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            ToDo todo = ToDo.init(1, null, "remark", "long_text");

            // insert
            int count = mapper.insertSelective(TABLE_NAME, todo);

            // select
            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(1);
            List<ToDo> select = mapper.selectByExampleWithBLOBs(TABLE_NAME, param);

            // assert insert count
            Assert.assertTrue(count == 1);

            // assert inserted object
            Assert.assertEquals(select.size(), 1);
            Assert.assertEquals(select.get(0).getRemark(), todo.getRemark());
            Assert.assertEquals(select.get(0).getToDo(), todo.getToDo());
            Assert.assertEquals(select.get(0).getLongText(), todo.getLongText());
            Assert.assertEquals(select.get(0).getLongText(), "long_text");
            Assert.assertTrue(select.get(0).getVersion() == 0L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void selectByExample() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            ToDo todo = ToDo.init(1, null, "remark", "long_text");

            // insert
            int count = mapper.insertSelective(TABLE_NAME, todo);

            // select
            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(1);
            List<ToDo> select = mapper.selectByExample(TABLE_NAME, param);

            // assert insert count
            Assert.assertTrue(count == 1);

            // assert inserted object
            Assert.assertEquals(select.size(), 1);
            Assert.assertEquals(select.get(0).getRemark(), todo.getRemark());
            Assert.assertEquals(select.get(0).getToDo(), todo.getToDo());
            Assert.assertTrue(StringUtils.isBlank(select.get(0).getLongText()));
            Assert.assertTrue(select.get(0).getVersion() == 0L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void selectByPrimaryKey() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            ToDo todo = ToDo.init(1, null, "remark", null);

            // insert
            int count = mapper.insertSelective(TABLE_NAME, todo);

            // select
            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(1);
            Optional<ToDo> inserted = mapper.getOneByExample(TABLE_NAME, param);

            // assert insert count
            Assert.assertTrue(count == 1);

            ToDo selectByKey = mapper.selectByPrimaryKey(TABLE_NAME, inserted.get().getId());

            // assert inserted object
            Assert.assertTrue(selectByKey != null);
            Assert.assertEquals(selectByKey.getRemark(), todo.getRemark());
            Assert.assertEquals(selectByKey.getToDo(), todo.getToDo());
            Assert.assertTrue(selectByKey.getVersion() == 0L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void updateByExampleSelective() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            ToDo todo = ToDo.init(1, 1L, "remark", "long_text");

            // insert
            int count = mapper.insertSelective(TABLE_NAME, todo);

            ToDo update = new ToDo();
            update.setToDo(2);
            update.setLongText("long_text2");

            // select
            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(1);
            Assert.assertEquals(mapper.updateByExampleSelective(TABLE_NAME, update, param), 1);

            param.clear();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(2);
            Optional<ToDo> todoAfterUpdate = mapper.getOneByExample(TABLE_NAME, param);

            Assert.assertTrue(todoAfterUpdate.isPresent());
            Assert.assertEquals(todoAfterUpdate.get().getRemark(), todo.getRemark());
            Assert.assertEquals(todoAfterUpdate.get().getToDo(), update.getToDo());
            Assert.assertEquals(todoAfterUpdate.get().getLongText() , update.getLongText());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void updateByExampleWithBLOBs() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            ToDo todo = ToDo.init(1, 1L, "remark", "long_text");

            // insert
            mapper.insertSelective(TABLE_NAME, todo);


            // select
            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(1);

            ToDo updateBefore = mapper.getOneByExample(TABLE_NAME, param).get();
            updateBefore.setToDo(2);
            updateBefore.setLongText("long_text2");


            Assert.assertEquals(mapper.updateByExampleWithBLOBs(TABLE_NAME, updateBefore, param), 1);

            param.clear();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(2);
            Optional<ToDo> todoAfterUpdate = mapper.getOneByExample(TABLE_NAME, param);

            Assert.assertTrue(todoAfterUpdate.isPresent());
            Assert.assertEquals(todoAfterUpdate.get().getRemark(), todo.getRemark());
            Assert.assertEquals(todoAfterUpdate.get().getToDo(), updateBefore.getToDo());
            Assert.assertEquals(todoAfterUpdate.get().getLongText() , updateBefore.getLongText());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void updateByExample() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            ToDo todo = ToDo.init(1, 1L, "remark", "long_text");

            // insert
            mapper.insertSelective(TABLE_NAME, todo);


            // select
            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(1);

            ToDo updateBefore = mapper.getOneByExample(TABLE_NAME, param).get();
            updateBefore.setToDo(2);
            updateBefore.setLongText("long_text2");


            Assert.assertEquals(mapper.updateByExample(TABLE_NAME, updateBefore, param), 1);

            param.clear();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(2);
            Optional<ToDo> todoAfterUpdate = mapper.getOneByExample(TABLE_NAME, param);

            Assert.assertTrue(todoAfterUpdate.isPresent());
            Assert.assertEquals(todoAfterUpdate.get().getRemark(), todo.getRemark());
            Assert.assertEquals(todoAfterUpdate.get().getToDo(), updateBefore.getToDo());
            Assert.assertEquals(todoAfterUpdate.get().getLongText() , todo.getLongText());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void updateByPrimaryKeySelective() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            ToDo todo = ToDo.init(1, 1L, "remark", "long_text");

            // insert
            mapper.insertSelective(TABLE_NAME, todo);

            // select
            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(1);

            ToDo updateBefore = mapper.getOneByExample(TABLE_NAME, param).get();
            updateBefore.setToDo(2);
            updateBefore.setLongText("long_text2");

            Assert.assertEquals(mapper.updateByPrimaryKeySelective(TABLE_NAME, updateBefore), 1);

            param.clear();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(2);
            ToDo todoAfterUpdate = mapper.selectByPrimaryKey(TABLE_NAME, updateBefore.getId());

            Assert.assertEquals(updateBefore.getRemark(), todo.getRemark());
            Assert.assertEquals(todoAfterUpdate.getRemark(), todo.getRemark());
            Assert.assertEquals(todoAfterUpdate.getToDo(), updateBefore.getToDo());
            Assert.assertEquals(todoAfterUpdate.getLongText() , updateBefore.getLongText());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void updateByPrimaryKeyWithBLOBs() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            ToDo todo = ToDo.init(1, 1L, "remark", "long_text");

            // insert
            mapper.insertSelective(TABLE_NAME, todo);

            // select
            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(1);

            ToDo updateBefore = mapper.getOneByExample(TABLE_NAME, param).get();
            updateBefore.setToDo(2);
            updateBefore.setLongText("long_text2");

            Assert.assertEquals(mapper.updateByPrimaryKeyWithBLOBs(TABLE_NAME, updateBefore), 1);

            param.clear();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(2);
            ToDo todoAfterUpdate = mapper.selectByPrimaryKey(TABLE_NAME, updateBefore.getId());

            Assert.assertEquals(updateBefore.getRemark(), todo.getRemark());
            Assert.assertEquals(todoAfterUpdate.getRemark(), todo.getRemark());
            Assert.assertEquals(todoAfterUpdate.getToDo(), updateBefore.getToDo());
            Assert.assertEquals(todoAfterUpdate.getLongText() , updateBefore.getLongText());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void updateByPrimaryKey() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            ToDo todo = ToDo.init(1, 1L, "remark", "long_text");

            // insert
            mapper.insertSelective(TABLE_NAME, todo);

            // select
            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(1);

            ToDo updateBefore = mapper.getOneByExample(TABLE_NAME, param).get();
            updateBefore.setToDo(2);
            updateBefore.setLongText("long_text2");

            Assert.assertEquals(mapper.updateByPrimaryKey(TABLE_NAME, updateBefore), 1);

            param.clear();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(2);
            ToDo todoAfterUpdate = mapper.selectByPrimaryKey(TABLE_NAME, updateBefore.getId());

            Assert.assertEquals(updateBefore.getRemark(), todo.getRemark());
            Assert.assertEquals(todoAfterUpdate.getRemark(), todo.getRemark());
            Assert.assertEquals(todoAfterUpdate.getToDo(), updateBefore.getToDo());
            Assert.assertEquals(todoAfterUpdate.getLongText(), todo.getLongText());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();

        }
    }

    @Test
    public void getOneByExample() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            ToDo todo = ToDo.init(1, null, "remark", "long_text");

            // insert
            int count = mapper.insertSelective(TABLE_NAME, todo);

            // select
            ToDoParam param = new ToDoParam();
            param.createCriteria().andRemarkEqualTo("remark").andToDoEqualTo(1);
            Optional<ToDo> select = mapper.getOneByExample(TABLE_NAME, param);

            // assert insert count
            Assert.assertTrue(count == 1);

            // assert inserted object
            Assert.assertTrue(select.isPresent());
            Assert.assertEquals(select.get().getRemark(), todo.getRemark());
            Assert.assertEquals(select.get().getToDo(), todo.getToDo());
            Assert.assertEquals(select.get().getLongText(), todo.getLongText());
            Assert.assertEquals(select.get().getLongText(), "long_text");
            Assert.assertTrue(select.get().getVersion() == 0L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void batchInsert() {
        try {
            ToDoMapper mapper = sqlSession.getMapper(ToDoMapper.class);
            List<ToDo> toDoList = new ArrayList<>();

            toDoList.add(ToDo.init(1, null, "remark1", "long_text1"));
            toDoList.add(ToDo.init(2, null, "remark2", "long_text2"));
            toDoList.add(ToDo.init(3, null, "remark3", "long_text3"));
            toDoList.add(ToDo.init(4, null, "remark4", "long_text4"));

            // insert
            int insertCount = mapper.batchInsert(TABLE_NAME, toDoList);

            // select
            long count = mapper.countByExample(TABLE_NAME, new ToDoParam());

            // assert insert count
            Assert.assertTrue(count == toDoList.size());
            Assert.assertTrue(insertCount == toDoList.size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}