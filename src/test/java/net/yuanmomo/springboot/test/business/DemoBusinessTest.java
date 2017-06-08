package net.yuanmomo.springboot.test.business;

import net.yuanmomo.springboot.bean.Demo;
import net.yuanmomo.springboot.bean.DemoParam;
import net.yuanmomo.springboot.mybatis.mapper.DemoMapper;
import net.yuanmomo.springboot.test.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

/**
 * Created by Hongbin.Yuan on 2017-06-08 16:47.
 */

public class DemoBusinessTest extends BaseUnitTest {

    @Autowired DemoMapper demoMapper;

    @Test
    @Sql("classpath:sql/demo-business-test.sql")
    public void testSelectOneByExample() throws Exception {
        DemoParam demoParam = new DemoParam();
        demoParam.createCriteria().andNumberEqualTo(8888888);
        demoParam.setOrderByClause(" id desc ");

        Demo demo = this.demoMapper.selectOneByExample(demoParam);
        Assert.assertTrue(demo.getVersion() == 1111111);

        demoParam.setOrderByClause(" id asc ");

        demo = this.demoMapper.selectOneByExample(demoParam);
        Assert.assertTrue(demo.getVersion() == 2222222);
    }

    @Test
    @Sql("classpath:sql/demo-business-test.sql")
    public void testPagination() throws Exception {
        DemoParam demoParam = new DemoParam();
        demoParam.setOrderByClause(" id desc ");

        demoParam.setStart(1);
        List<Demo> demoList = this.demoMapper.selectByExample(demoParam);
        Assert.assertTrue(demoList.size() == 1);
        Assert.assertTrue(demoList.get(0).getVersion() == 2222222);

        demoParam.setCount(1);
        demoParam.setStart(-1);
        demoList = this.demoMapper.selectByExample(demoParam);
        Assert.assertTrue(demoList.size() == 1);
        Assert.assertTrue(demoList.get(0).getVersion() == 2222222);

        demoParam.setStart(3);
        demoParam.setCount(2);
        demoList = this.demoMapper.selectByExample(demoParam);
        Assert.assertTrue(demoList.size() == 2);
        Assert.assertTrue(demoList.get(0).getVersion() == 5555555);
        Assert.assertTrue(demoList.get(1).getVersion() == 9999999);
    }

}
