package com.github.yuanmomo.mybatis.mbg.plugin.getone;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import com.github.yuanmomo.mybatis.mbg.plugin.SimplePlugin;

/**
 *  Generate a new method name getOneByExample which will
 *  return only one record according to the example param.
 */
public class MapperAddGetOneByExamplePlugin extends PluginAdapter {

    public static String GET_ONE_BY_EXAMPLE_PROVIDER_METHOD_NAME = "getOneByExample";

    @Override
    public boolean validate(List<String> warnings) {
        return ! SimplePlugin.SIMPLE_ACTIVE;
    }

    /**
     * add getOneByExample method.
     *
     * @param interfaze   mapper interface.
     * @param introspectedTable current table configurations.
     * @return boolean result
     */
    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        AnnotatedGetOneByExampleWithoutBLOBsMethodGenerator annotatedGetOneByExampleWithoutBLOBsMethodGenerator
                = new AnnotatedGetOneByExampleWithoutBLOBsMethodGenerator();

        annotatedGetOneByExampleWithoutBLOBsMethodGenerator.setContext(context);
        annotatedGetOneByExampleWithoutBLOBsMethodGenerator.setIntrospectedTable(introspectedTable);
        annotatedGetOneByExampleWithoutBLOBsMethodGenerator.setMethodName(getMethodName(this.getProperties()));

        annotatedGetOneByExampleWithoutBLOBsMethodGenerator.addInterfaceElements(interfaze);
        return true;
    }

    /**
     * add getONeByExample SqlProvider.
     *
     * @param topLevelClass  topLevelClass
     * @param introspectedTable current table configurations.
     * @return boolean result
     */
    @Override
    public boolean providerGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        ProviderGetOneByExampleWithoutBLOBsMethodGenerator providerGetOneByExampleWithoutBLOBsMethodGenerator
                = new ProviderGetOneByExampleWithoutBLOBsMethodGenerator(false);

        providerGetOneByExampleWithoutBLOBsMethodGenerator.setContext(introspectedTable.getContext());
        providerGetOneByExampleWithoutBLOBsMethodGenerator.setIntrospectedTable(introspectedTable);
        providerGetOneByExampleWithoutBLOBsMethodGenerator.setMethodName(getMethodName(this.getProperties()));

        providerGetOneByExampleWithoutBLOBsMethodGenerator.addClassElements(topLevelClass);

        return super.providerGenerated(topLevelClass,introspectedTable);
    }


    /**
     *  Get configured method name.
     *
     * @param properties configuration in xml file.
     * @return method name;
     */
    private static String getMethodName (Properties properties){
        String methodNameProp = properties.getProperty("methodName");
        if(StringUtils.isBlank(methodNameProp)){
            return GET_ONE_BY_EXAMPLE_PROVIDER_METHOD_NAME;
        }
        return methodNameProp.trim();
    }
}
