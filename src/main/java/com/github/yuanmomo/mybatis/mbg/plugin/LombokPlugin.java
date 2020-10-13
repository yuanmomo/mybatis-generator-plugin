package com.github.yuanmomo.mybatis.mbg.plugin;

import java.util.HashMap;
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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */

public class LombokPlugin extends PluginAdapter {

    private static Logger logger = LoggerFactory.getLogger(LombokPlugin.class);

    public static final String DATA_FAG = "data";
    public static final String HASH_EQUALS_FLAG_FLAG = "hashEquals";
    public static final String TO_STRING_FLAT = "toString";
    public static final String NO_ARG_CONSTRUCTOR_FLAG = "noArgConstructor";

    public static final Map<String, Class<?>> CONFIG_MAP = new HashMap<>();
    static {
        CONFIG_MAP.put(DATA_FAG, Data.class);
        CONFIG_MAP.put(HASH_EQUALS_FLAG_FLAG, EqualsAndHashCode.class);
        CONFIG_MAP.put(TO_STRING_FLAT, ToString.class);
        CONFIG_MAP.put(NO_ARG_CONSTRUCTOR_FLAG, NoArgsConstructor.class);
    }


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              Plugin.ModelClassType modelClassType) {
        return !PropertiesUtil.getBooleanProp(this.getProperties(), DATA_FAG);
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              Plugin.ModelClassType modelClassType) {
        return !PropertiesUtil.getBooleanProp(this.getProperties(), DATA_FAG);
    }

    private void addLombokAnnotation(TopLevelClass topLevelClass){
        for (String configKey : CONFIG_MAP.keySet()) {
            boolean flag = PropertiesUtil.getBooleanProp(this.getProperties(), configKey);
            if (flag) {
                topLevelClass.addImportedType(new FullyQualifiedJavaType(CONFIG_MAP.get(configKey).getName()));
                topLevelClass.addAnnotation(String.format("@%s", CONFIG_MAP.get(configKey).getSimpleName()));
            }
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
