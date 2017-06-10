package com.github.yuanmomo.mybatis.mbg.plugin.batch;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.Properties;

/**
 * Created by Hongbin.Yuan on 2017-06-09 16:13.
 */
public class MySqlBatchInsertPlugin extends PluginAdapter {
    public static String BATCH_INSERT_SELECTIVE_PROVIDER_METHOD_NAME = "batchInsertSelective";
    /**
     *  Here should check if current database is MySql.
     * @param warnings
     * @return
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * add selectOneByExample method.
     *
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze,
                                   TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        AnnotatedBatchInsertSelectiveMethodGenerator annotatedBatchInsertSelectiveMethodGenerator
                = new AnnotatedBatchInsertSelectiveMethodGenerator();

        annotatedBatchInsertSelectiveMethodGenerator.setContext(context);
        annotatedBatchInsertSelectiveMethodGenerator.setIntrospectedTable(introspectedTable);
        annotatedBatchInsertSelectiveMethodGenerator.setMethodName(getMethodName(this.getProperties()));

        annotatedBatchInsertSelectiveMethodGenerator.addInterfaceElements(interfaze);
        return true;
    }

    /**
     *  Get configured method name.
     *
     * @param properties
     * @return
     */
    private static String getMethodName (Properties properties){
        String methodNameProp = properties.getProperty("methodName");
        if(StringUtils.isBlank(methodNameProp)){
            return BATCH_INSERT_SELECTIVE_PROVIDER_METHOD_NAME;
        }
        return methodNameProp.trim();
    }

}
