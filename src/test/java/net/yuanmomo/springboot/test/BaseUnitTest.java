package net.yuanmomo.springboot.test;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import net.yuanmomo.springboot.Application;
import net.yuanmomo.springboot.util.AjaxResponseBean;
import net.yuanmomo.springboot.util.PaginationBean;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Base test class for Spring MVC unit test.
 * <p>
 * Created by Hongbin.Yuan on 2017-04-04 05:22.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@AutoConfigureMockMvc
@Transactional
public class BaseUnitTest {

    /**
     * Field will not set into request params.
     */
    private final String[] EXCLUDE_PARAM_FIELD_ARRAY = new String[]{"serialVersionUID"};
    private final Set<String> EXCLUDE_PARAM_FIELD_SET = Sets.newHashSet(EXCLUDE_PARAM_FIELD_ARRAY);

    /**
     * Instance of MockMvc.
     */
    @Autowired protected MockMvc mvc;

    /**
     * Format expected result to JSON and compare with the actual result.
     */
    protected static Gson gson = new Gson();

    /**
     * fix Druid WebAppStat NullPointerException.
     */
    @Autowired private FilterRegistrationBean filterRegistrationBean;


    @Before
    public void before() {
        try {
            MockFilterConfig mockFilterConfig = new MockFilterConfig();
            filterRegistrationBean.getFilter().init(mockFilterConfig);
        } catch (ServletException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Get an expected result of {@link PaginationBean}
     *
     * @param pageNum
     * @param numPerPage
     * @param totalCount
     * @param totalPages
     * @return
     */
    protected PaginationBean getPaginationBean(long pageNum, long numPerPage, long totalCount, long totalPages) {
        PaginationBean paginationBean = new PaginationBean();
        paginationBean.setPageNum(pageNum);
        paginationBean.setNumPerPage(numPerPage);
        paginationBean.setTotalCount(totalCount);
        paginationBean.setTotalPages(totalPages);
        return paginationBean;
    }

    /**
     * Get single result by id(primary key).
     *
     * @param url
     * @param id
     * @return
     * @throws Exception
     */
    protected String getById(String url, Object id) throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(url)
                .param("id", String.valueOf(id))
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse();
        return response == null ? "" : response.getContentAsString();
    }

    /**
     * Convert the return value of {@link AjaxResponseBean} into type primitive long.
     *
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    protected long getLongFromReturnValue(MockHttpServletResponse response) throws UnsupportedEncodingException {
        if (response == null) {
            return 0L;
        }
        return this.getLongFromReturnValue(response.getContentAsString());
    }

    /**
     * Convert the return value of {@link AjaxResponseBean} into type primitive long.
     *
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    protected long getLongFromReturnValue(String response) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(response)) {
            return 0L;
        }
        Object value = gson.fromJson(response, AjaxResponseBean.class).getReturnValue();
        if (value instanceof Double) {
            Double doubleValue = (Double) value;
            return doubleValue.longValue();
        }
        return 0L;
    }

    /**
     * Get a reuqest Builder.
     *
     * @param mapping
     * @return
     */
    protected MockHttpServletRequestBuilder getMockRequestBuilder(String mapping) {
        return MockMvcRequestBuilders.post(mapping).accept(MediaType.APPLICATION_JSON_UTF8);
    }

    /**
     * send request and return response.
     *
     * @param requestBuilder
     * @return
     * @throws Exception
     */
    protected String perform(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        MockHttpServletResponse response = this.mvc.perform(requestBuilder).andReturn().getResponse();
        return response == null ? "" : response.getContentAsString();
    }

    /**
     * Set request param into request.
     *
     * @param requestBuilder
     * @param key
     * @param value
     */
    protected void setParam(MockHttpServletRequestBuilder requestBuilder, String key, Object value) {
        if (requestBuilder == null) {
            throw new NullPointerException("requestBuilder is null");
        }
        requestBuilder.param(key, String.valueOf(value));
    }

    /**
     * @param requestBuilder
     * @param obj
     * @param includeField   if this param is not null, then only set field in this param.
     */
    protected void setParam(MockHttpServletRequestBuilder requestBuilder, Object obj, Object... includeField) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        if (ArrayUtils.isEmpty(fields)) {
            return;
        }

        // set field
        for (Field field : fields) {
            field.setAccessible(true);
            if (EXCLUDE_PARAM_FIELD_SET.contains(field.getName())) {
                continue;
            }
            if (ArrayUtils.isNotEmpty(includeField)
                    && !ArrayUtils.contains(includeField, field.getName())) {
                continue;
            }
            this.setParam(requestBuilder, field.getName(), field.get(obj));
        }
    }

    /**
     * @param requestBuilder
     * @param obj
     * @param excludeField   if this param is not null, then the field will not set in this param.
     */
    protected void setParamExclude(MockHttpServletRequestBuilder requestBuilder, Object obj, String... excludeField) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        if (ArrayUtils.isEmpty(fields)) {
            return;
        }

        List<String> includeFieldList = new ArrayList<>();

        // set field
        for (Field field : fields) {
            if (ArrayUtils.contains(excludeField, field.getName())) {
                continue;
            }
            includeFieldList.add(field.getName());
        }

        this.setParam(requestBuilder, obj, includeFieldList.toArray());
    }


    /**
     * Assert the expected equal to response.
     *
     * @param ajaxResponseBean
     * @param response
     * @throws JSONException
     */
    protected void assertJson(AjaxResponseBean ajaxResponseBean, String response) throws JSONException {
        JSONAssert.assertEquals(gson.toJson(ajaxResponseBean), response, false);
    }

    /**
     * Assert the expected equal to response.
     *
     * @param object
     * @param response
     * @throws JSONException
     */
    protected void assertJsonWithObj(Object object, String response) throws JSONException {
        JSONAssert.assertEquals(gson.toJson(AjaxResponseBean.getReturnValueResponseBean(object)), response, false);
    }
}
