package com.github.yuanmomo.mybatis.mbg.plugin.MultiplyTable;

import static com.github.yuanmomo.mybatis.mbg.plugin.MultiplyTable.MethodTypeEnum.EXAMPLE;
import static com.github.yuanmomo.mybatis.mbg.plugin.MultiplyTable.MethodTypeEnum.NORMAL;
import static org.mybatis.generator.internal.util.StringUtility.escapeStringForJava;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.Param;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * Table's name may like table_1, table_2, ...
 * <p>
 * Make the table index as an parameter passed as a parameter.
 */

@Slf4j
public class MultiplyTablePlugin extends PluginAdapter {

    private static Logger logger = LoggerFactory.getLogger(MultiplyTablePlugin.class);

    public static final String NEW_TABLE_NAME_PARAMETER = "tableName";

    public static boolean MULTIPLY_TABLE_ACTIVE = false;

    /**
     * active this plugin
     *
     * @param warnings
     * @return
     */
    @Override
    public boolean validate(List<String> warnings) {
        MULTIPLY_TABLE_ACTIVE = true;
        return MULTIPLY_TABLE_ACTIVE;
    }

    /**
     * Update method parameter to Map<String,Object> in provider.
     *
     * @param typeEnum          Normal method or example method.
     * @param method
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    public static boolean providerChangeMethodParameterToMap(
            MethodTypeEnum typeEnum,
            Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // check params
        if (method == null || method.getBodyLines() == null) {
            return true;
        }

        // current table name
        String tableName = escapeStringForJava(introspectedTable.getFullyQualifiedTableNameAtRuntime());

        // Provider class import java.util.Map
        topLevelClass.addImportedType(FullyQualifiedJavaType.getNewMapInstance());

        // replace parameter type with Map<String,Object>, named parameter
        List<Parameter> parameterList = method.getParameters();
        parameterList.clear();
        parameterList.add(new Parameter(new FullyQualifiedJavaType("java.util.Map<java.lang.String, java.lang.Object>"), "parameter"));

        // add get record or example method line
        List<String> newBodyLines = new ArrayList<>();
        switch (typeEnum) {
            case NORMAL:
                FullyQualifiedJavaType record = introspectedTable.getRules().calculateAllFieldsClass();
                newBodyLines.add(String.format("%s record = (%s) parameter.get(\"record\");", record.getShortName(), record.getShortName()));
                break;
            case EXAMPLE:
                FullyQualifiedJavaType example = new FullyQualifiedJavaType(introspectedTable.getExampleType());
                topLevelClass.addImportedType(example);
                newBodyLines.add(String.format("%s example = (%s) parameter.get(\"example\");", example.getShortName(), example.getShortName()));
                break;
            default:
                return true;
        }

        List<String> bodyLines = method.getBodyLines();
        for (String bodyLine : bodyLines) {
            if (StringUtils.startsWith(bodyLine, "ToDoParam example")) {
                continue;
            }

            // replace table name to ${tableName}
            String mark = String.format("(\"%s\")", tableName);
            if (StringUtils.contains(bodyLine, mark)) {
                String replacement = String.format("(\"%s\")", String.format("\\$\\{%s\\}", NEW_TABLE_NAME_PARAMETER));
                bodyLine = bodyLine.replaceAll(mark, replacement);
            }

            switch (typeEnum) {
                case NORMAL:
                    // add record.
                    if (StringUtils.isNotBlank(bodyLine)
                            && bodyLine.trim().startsWith("sql.VALUES(")) {
                        bodyLine = bodyLine.replaceAll("#\\{", "#\\{record.");
                    }

                    if (StringUtils.isNotBlank(bodyLine)
                            && bodyLine.trim().startsWith("sql.SET(")
                            && !bodyLine.trim().contains("#{record.")) {
                        bodyLine = bodyLine.replaceAll("#\\{", "#\\{record.");
                    }

                    if (StringUtils.isNotBlank(bodyLine)
                            && bodyLine.trim().startsWith("sql.WHERE(")
                            && !bodyLine.trim().contains("#{record.")) {
                        bodyLine = bodyLine.replaceAll("#\\{", "#\\{record.");
                    }


                    break;
                case EXAMPLE:
                    // replace method body line of 'applyWhere(sql, example, false)' to 'applyWhere(sql, example, true)'
                    if (StringUtils.isNotBlank(bodyLine)
                            && bodyLine.trim().startsWith("applyWhere")) {
                        bodyLine = "applyWhere(sql, example, true);";
                    }
                    break;
                default:
                    break;
            }

            newBodyLines.add(bodyLine);
        }
        replaceMethodBody(method, newBodyLines);

        return true;
    }

    /**
     * Add a 'tableName' parameter to method in mapper interface.
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     * @return
     */
    public static boolean mapperAddTableNameParameter(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        if (method == null || method.getParameters() == null) {
            return true;
        }

        List<Parameter> parameters = method.getParameters();

        for (Parameter parameter : parameters) {
            // old parameter
            Parameter originalParam = parameter;
            if (originalParam.getAnnotations() != null && originalParam.getAnnotations().size() > 0) {
                continue;
            }

            FullyQualifiedJavaType fullyQualifiedJavaType = new FullyQualifiedJavaType(Param.class.getName());
            interfaze.addImportedType(fullyQualifiedJavaType);
            originalParam.addAnnotation(String.format("@Param(\"%s\")", originalParam.getName()));
        }

        // tableName parameter
        Parameter tableNameParameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), NEW_TABLE_NAME_PARAMETER);
        tableNameParameter.addAnnotation(String.format("@Param(\"%s\")", NEW_TABLE_NAME_PARAMETER));
        parameters.add(0, tableNameParameter);
        return true;
    }

    /**
     * Replace a method body with given string list.
     *
     * @param method
     * @param bodyLineList
     */
    public static void replaceMethodBody(Method method, List<String> bodyLineList) {
        if (method == null || method.getBodyLines() == null) {
            return;
        }

        method.getBodyLines().clear();
        method.getBodyLines().addAll(bodyLineList);
    }

    /**
     * Replace string in method's annotation in mapper interface.
     *
     * @param method
     * @param matchers
     */
    public static void mapperReplaceAnnotationLine(Method method, Pair<String, String>... matchers) {
        if (method == null || method.getAnnotations() == null
                || matchers == null || matchers.length == 0) {
            return;
        }

        List<String> annotations = method.getAnnotations();
        List<String> newAnnotations = new ArrayList<>();

        String annotationLine = null;
        for (String annotation : annotations) {
            annotationLine = annotation;
            for (Pair<String, String> matcher : matchers) {
                annotationLine = StringUtils.replaceAll(annotationLine, matcher.getKey(), matcher.getValue());
            }
            newAnnotations.add(String.format("%s\n ",annotationLine));
        }

        method.getAnnotations().clear();
        method.getAnnotations().addAll(newAnnotations);
    }

    @Override
    public boolean providerCountByExampleMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        providerChangeMethodParameterToMap(EXAMPLE, method, topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean clientCountByExampleMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return mapperAddTableNameParameter(method, interfaze, introspectedTable);
    }

    @Override
    public boolean providerDeleteByExampleMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        providerChangeMethodParameterToMap(EXAMPLE, method, topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return mapperAddTableNameParameter(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // update method parameter
        mapperAddTableNameParameter(method, interfaze, introspectedTable);

        // current table name
        String tableName = escapeStringForJava(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        mapperReplaceAnnotationLine(method, Pair.of(String.format("from %s", tableName), "from \\$\\{tableName\\}"));
        return true;
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // update method parameter
        mapperAddTableNameParameter(method, interfaze, introspectedTable);

        // current table name
        String tableName = escapeStringForJava(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        mapperReplaceAnnotationLine(method,
                Pair.of(String.format("into %s", tableName), "into \\$\\{tableName\\}"),
                Pair.of("#\\{", "#\\{record."));
        return true;

    }

    @Override
    public boolean providerInsertSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        providerChangeMethodParameterToMap(NORMAL, method, topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // update method parameter
        mapperAddTableNameParameter(method, interfaze, introspectedTable);
        return true;
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        mapperAddTableNameParameter(method, interfaze, introspectedTable);
        return true;
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        mapperAddTableNameParameter(method, interfaze, introspectedTable);
        return true;
    }

    @Override
    public boolean providerSelectByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        providerChangeMethodParameterToMap(EXAMPLE, method, topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean providerSelectByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        providerChangeMethodParameterToMap(EXAMPLE, method, topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // update method parameter
        mapperAddTableNameParameter(method, interfaze, introspectedTable);

        // current table name
        String tableName = escapeStringForJava(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        mapperReplaceAnnotationLine(method, Pair.of(String.format("from %s", tableName), "from \\$\\{tableName\\}"));
        return true;
    }

    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // update method parameter
        mapperAddTableNameParameter(method, interfaze, introspectedTable);
        return true;
    }

    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // update method parameter
        mapperAddTableNameParameter(method, interfaze, introspectedTable);
        return true;
    }

    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // update method parameter
        mapperAddTableNameParameter(method, interfaze, introspectedTable);
        return true;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // update method parameter
        mapperAddTableNameParameter(method, interfaze, introspectedTable);
        return true;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // update method parameter
        mapperAddTableNameParameter(method, interfaze, introspectedTable);

        String tableName = escapeStringForJava(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        mapperReplaceAnnotationLine(method,
                Pair.of(String.format("update %s", tableName), "update \\$\\{tableName\\}"),
                Pair.of("#\\{", "#\\{record."));
        return true;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // update method parameter
        mapperAddTableNameParameter(method, interfaze, introspectedTable);

        String tableName = escapeStringForJava(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        mapperReplaceAnnotationLine(method,
                Pair.of(String.format("update %s", tableName), "update \\$\\{tableName\\}"),
                Pair.of("#\\{", "#\\{record."));
        return true;
    }

    @Override
    public boolean providerUpdateByExampleSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        providerChangeMethodParameterToMap(EXAMPLE, method, topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean providerUpdateByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        providerChangeMethodParameterToMap(EXAMPLE, method, topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean providerUpdateByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        providerChangeMethodParameterToMap(EXAMPLE, method, topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean providerUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        providerChangeMethodParameterToMap(NORMAL, method, topLevelClass, introspectedTable);
        return true;
    }
}