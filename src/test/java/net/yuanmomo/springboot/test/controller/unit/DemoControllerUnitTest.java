package net.yuanmomo.springboot.test.controller.unit;

/**
 * Created by Hongbin.Yuan on 2017-04-03 21:34.
 */


import net.yuanmomo.springboot.bean.Demo;
import net.yuanmomo.springboot.test.BaseUnitTest;
import net.yuanmomo.springboot.util.PaginationBean;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static net.yuanmomo.springboot.util.AjaxResponseBean.Const.ERROR_RESPONSE_BEAN;
import static net.yuanmomo.springboot.util.AjaxResponseBean.Const.PARAMETER_INVALID_ERROR_RESPONSE_BEAN;
import static net.yuanmomo.springboot.util.AjaxResponseBean.Const.SUCCESS_RESPONSE_BEAN;

public class DemoControllerUnitTest extends BaseUnitTest {

    /**
     * Mapping configured on controller.
     */
    protected String BASE_MAPPING = "/backend/demo/";

    /**
     * Mapping configured on method in controller.
     */
    String insert = BASE_MAPPING + "insert.do";
    String getDemoByKey = BASE_MAPPING + "getDemoByKey.do";
    String selectDemoList = BASE_MAPPING + "selectDemoList.do";
    String updateSaveDemo = BASE_MAPPING + "updateSaveDemo.do";
    String batchUpdateSaveDemo = BASE_MAPPING + "batchUpdateSaveDemo.do";

    @Test
    public void testInsert() throws Exception {
        /********************************************* 1. insert ************************************************************/
        MockHttpServletRequestBuilder requestBuilder = this.getMockRequestBuilder(insert);

        Demo toInsertDemo = this.getDemo(9999999L, 8888881, 7777771L);

        // set parameters
        this.setParamExclude(requestBuilder, toInsertDemo, "id");

        String response = this.perform(requestBuilder);

        // check return value
        this.assertJsonWithObj(null, response);

        /********************************************* 2. get and check ************************************************************/
        toInsertDemo.setId(getLongFromReturnValue(response));

        // check return value
        this.assertJsonWithObj(toInsertDemo, this.getById(getDemoByKey, toInsertDemo.getId()));
    }

    @Test
    @Sql("classpath:sql/demo.sql")
    public void testGetDemoByKey() throws Exception {
        // get expected value
        Demo expectedDemo = getDemo(9999999L, 888888, 777777L);

        // check response
        this.assertJsonWithObj(expectedDemo, this.getById(getDemoByKey, expectedDemo.getId()));
    }

    @Test
    @Sql("classpath:sql/demo.sql")
    public void testSelectDemoList() throws Exception {
        /********************************************* 1. pass invalid parameter ************************************************************/
        MockHttpServletRequestBuilder requestBuilder = this.getMockRequestBuilder(selectDemoList);

        // check response
        this.assertJson(PARAMETER_INVALID_ERROR_RESPONSE_BEAN, this.perform(requestBuilder));

        /********************************************* 2. pass valid parameter ************************************************************/
        PaginationBean<Demo> expectedPaginationBean = getPaginationBean(1, 20, 2, 1);
        List<Demo> expectedDemoList = new ArrayList<>();
        expectedDemoList.add(getDemo(9999999L, 888888, 777777L));
        expectedDemoList.add(getDemo(99999999L, 8888888, 7777777L));
        expectedPaginationBean.setResult(expectedDemoList);

        // set valid parameter
        this.setParam(requestBuilder, expectedPaginationBean, "pageNum", "numPerPage");

        // check return value
        this.assertJsonWithObj(null, this.perform(requestBuilder));
    }


    @Test
    @Sql("classpath:sql/demo.sql")
    public void testUpdateSaveDemo() throws Exception {
        /********************************************* 1. pass invalid parameter ************************************************************/
        MockHttpServletRequestBuilder requestBuilder = this.getMockRequestBuilder(updateSaveDemo);

        this.assertJson(PARAMETER_INVALID_ERROR_RESPONSE_BEAN, perform(requestBuilder));
        /********************************************* 2. pass valid parameter to update ************************************************************/
        Demo updatedDemo = getDemo(9999999L, 8888881, 7777771L);

        // set valid parameter
        this.setParam(requestBuilder, updatedDemo);

        // check return value
        this.assertJson(SUCCESS_RESPONSE_BEAN, this.perform(requestBuilder));
        /********************************************* 3. select and check ************************************************************/
        // check return value
        this.assertJsonWithObj(updatedDemo, this.getById(getDemoByKey, updatedDemo.getId()));
    }


    @Test
    @Sql("classpath:sql/demo.sql")
    public void testBatchUpdateSaveDemo() throws Exception {
        /********************************************* 1. pass invalid parameter ************************************************************/
        MockHttpServletRequestBuilder requestBuilder = this.getMockRequestBuilder(batchUpdateSaveDemo);

        // check response
        this.assertJson(ERROR_RESPONSE_BEAN, this.perform(requestBuilder));

        /********************************************* 1. pass valid parameter to batch update ************************************************************/
        PaginationBean<Demo> expectedPaginationBean = getPaginationBean(1, 20, 2, 1);
        List<Demo> expectedDemoList = new ArrayList<>();
        expectedDemoList.add(getDemo(9999999L, 8888882, 7777772L));
        expectedDemoList.add(getDemo(99999999L, 88888882, 77777772L));
        expectedPaginationBean.setResult(expectedDemoList);

        // set valid parameter
        for (int i = 0; i < expectedDemoList.size(); i++) {
            requestBuilder.param(String.format("demoList[%d].id", i), String.valueOf(expectedDemoList.get(i).getId()));
            requestBuilder.param(String.format("demoList[%d].number", i), String.valueOf(expectedDemoList.get(i).getNumber()));
            requestBuilder.param(String.format("demoList[%d].version", i), String.valueOf(expectedDemoList.get(i).getVersion()));
        }

        // check return value
        this.assertJson(SUCCESS_RESPONSE_BEAN, this.perform(requestBuilder));
        /**********************************************************************************************************
         ***   3. get and check
         **********************************************************************************************************/
        for (Demo demo : expectedDemoList) {
            this.assertJsonWithObj(demo, this.getById(getDemoByKey, demo.getId()));
        }
    }


    /**
     * get instance.
     *
     * @param id
     * @param number
     * @param version
     * @return
     */
    protected Demo getDemo(Long id, Integer number, Long version) {
        Demo demo = new Demo();
        demo.setId(id);
        demo.setNumber(number);
        demo.setVersion(version);
        return demo;
    }
}

