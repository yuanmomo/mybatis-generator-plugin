package net.yuanmomo.mybatis.mbg.plugin.selectone;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.Properties;

/**
 *  Generate a new method name selectOneByExample which will
 *  return only one record according to the example param.
 */
public class MapperAddSelectOneByExamplePlugin extends PluginAdapter {

    public static String SELECT_ONE_BY_EXAMPLE_PROVIDER_METHOD_NAME = "getOneByExample";

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
        AnnotatedSelectOneByExampleWithoutBLOBsMethodGenerator annotatedSelectOneByExampleWithoutBLOBsMethodGenerator
                = new AnnotatedSelectOneByExampleWithoutBLOBsMethodGenerator();

        annotatedSelectOneByExampleWithoutBLOBsMethodGenerator.setContext(context);
        annotatedSelectOneByExampleWithoutBLOBsMethodGenerator.setIntrospectedTable(introspectedTable);
        annotatedSelectOneByExampleWithoutBLOBsMethodGenerator.setMethodName(getMethodName(this.getProperties()));

        annotatedSelectOneByExampleWithoutBLOBsMethodGenerator.addInterfaceElements(interfaze);
        return true;
    }

    /**
     * add selectOneByExample SqlProvider.
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean providerGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        ProviderSelectOneByExampleWithoutBLOBsMethodGenerator providerSelectOneByExampleWithoutBLOBsMethodGenerator
                = new ProviderSelectOneByExampleWithoutBLOBsMethodGenerator(false);

        providerSelectOneByExampleWithoutBLOBsMethodGenerator.setContext(introspectedTable.getContext());
        providerSelectOneByExampleWithoutBLOBsMethodGenerator.setIntrospectedTable(introspectedTable);
        providerSelectOneByExampleWithoutBLOBsMethodGenerator.setMethodName(getMethodName(this.getProperties()));

        providerSelectOneByExampleWithoutBLOBsMethodGenerator.addClassElements(topLevelClass);

        return super.providerGenerated(topLevelClass,introspectedTable);
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
            return SELECT_ONE_BY_EXAMPLE_PROVIDER_METHOD_NAME;
        }
        return methodNameProp.trim();
    }
}
