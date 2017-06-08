package net.yuanmomo.springboot.test.controller.it;

import net.yuanmomo.springboot.bean.Demo;
import net.yuanmomo.springboot.util.AjaxResponseBean;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.Assert.assertEquals;

/**
 * Created by Hongbin.Yuan on 2017-04-03 21:34.
 */
@Sql("classpath:sql/demo.sql")
public class DemoControllerITTest extends BaseITTest {

    private static final String BASE_MAPPING = "/backend/demo/";

    @Test
    public void testInsert() throws Exception {
        String mapping = "insert.do?number=1000&version=1000";
        Demo demo = new Demo();
        demo.setNumber(1);
        demo.setVersion(2L);
        ResponseEntity<AjaxResponseBean> response = template.postForEntity(base.toString() + BASE_MAPPING + mapping, null, AjaxResponseBean.class);
        assertEquals(200,response.getBody().getStatusCode());
    }
}

