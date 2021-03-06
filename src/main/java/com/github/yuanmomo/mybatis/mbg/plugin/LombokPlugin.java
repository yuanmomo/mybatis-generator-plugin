package com.github.yuanmomo.mybatis.mbg.plugin;

import java.util.List;
import java.util.Map;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.yuanmomo.mybatis.mbg.util.PropertiesUtil;

/**
 *
 */

public class LombokPlugin extends PluginAdapter {

    private static Logger logger = LoggerFactory.getLogger(LombokPlugin.class);

    public static final String DATA_FAG = "lombok.Data";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              Plugin.ModelClassType modelClassType) {
        return !PropertiesUtil.containsValueIgnoreCase(this.getProperties(), DATA_FAG);
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              Plugin.ModelClassType modelClassType) {
        return !PropertiesUtil.containsValueIgnoreCase(this.getProperties(), DATA_FAG);
    }

    private void addLombokAnnotation(TopLevelClass topLevelClass){
        if (this.getProperties() == null || this.getProperties().isEmpty() ){
            return;
        }
        for (Map.Entry<Object, Object> entry : this.getProperties().entrySet()) {
            FullyQualifiedJavaType fullyQualifiedJavaType = new FullyQualifiedJavaType(String.valueOf(entry.getValue()));
            topLevelClass.addImportedType(fullyQualifiedJavaType);
            topLevelClass.addAnnotation(String.format("@%s", fullyQualifiedJavaType.getShortName()));
        }
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        addLombokAnnotation(topLevelClass);
        return true;
    }


    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addLombokAnnotation(topLevelClass);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addLombokAnnotation(topLevelClass);
        return true;
    }

}
