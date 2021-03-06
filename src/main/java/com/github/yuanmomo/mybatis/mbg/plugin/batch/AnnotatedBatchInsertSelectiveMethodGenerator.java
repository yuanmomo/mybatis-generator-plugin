package com.github.yuanmomo.mybatis.mbg.plugin.batch;

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;
import static org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities.getEscapedColumnName;
import static org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities.getParameterClause;
import static org.mybatis.generator.internal.util.StringUtility.escapeStringForJava;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.InsertSelectiveMethodGenerator;

import com.github.yuanmomo.mybatis.mbg.plugin.dynamic.DynamicTableNamePlugin;

/**
 * Created by Hongbin.Yuan on 2017-06-09 16:31.
 */

public class AnnotatedBatchInsertSelectiveMethodGenerator extends
        InsertSelectiveMethodGenerator {

    private String methodName;

    public AnnotatedBatchInsertSelectiveMethodGenerator() {
        super();
    }

    /**
     *
     * @param interfaze the mapper interface.
     */
    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();

        Method method = new Method(methodName);

        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(true);

        // set parameter List<T> xxx
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType(introspectedTable
                    .getBaseRecordType());
        importedTypes.add(listType);

        FullyQualifiedJavaType parameterType = FullyQualifiedJavaType.getNewListInstance();
        parameterType.addTypeArgument(listType);
        importedTypes.add(parameterType);
        method.addParameter(new Parameter(parameterType, "list")); //$NON-NLS-1$


        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        interfaze.addImportedType(new FullyQualifiedJavaType(InsertProvider.class.getName())); //$NON-NLS-1$
        interfaze.addImportedType(new FullyQualifiedJavaType(Options.class.getName())); //$NON-NLS-1$
        interfaze.addImportedType(new FullyQualifiedJavaType(Insert.class.getName())); //$NON-NLS-1$

        addMapperAnnotations( method);

        if (DynamicTableNamePlugin.DYNAMIC_TABLE_NAME_PLUGIN_ACTIVE){
            DynamicTableNamePlugin.mapperAddTableNameParameter(method, interfaze, introspectedTable);

            String tableName = escapeStringForJava(introspectedTable.getFullyQualifiedTableNameAtRuntime());
            DynamicTableNamePlugin.mapperReplaceAnnotationLine(introspectedTable,method,
                    Pair.of(String.format("into %s", tableName), "into \\$\\{tableName\\}"),
                    Pair.of("keyProperty=\"id\"", "keyProperty=\"list.id\"")
            );
        }

        interfaze.addMethod(method);
    }

    @Override
    public void addMapperAnnotations(Method method) {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(introspectedTable.getMyBatis3SqlProviderType());

        // get java property name and column name
        List<IntrospectedColumn> introspectedColumns = introspectedTable
                .getPrimaryKeyColumns();
        // primary key count == 1
        if (introspectedColumns != null && introspectedColumns.size() == 1) {
            String columnPrimaryKey = escapeStringForJava(
                    getEscapedColumnName(introspectedColumns.get(0)));
            String classPrimaryField = introspectedColumns.get(0).getJavaProperty();
            String optionAnnotation = String.format("@Options(useGeneratedKeys = true,keyProperty=\"%s\",keyColumn = \"%s\")", classPrimaryField, columnPrimaryKey);
            method.addAnnotation(optionAnnotation);
        }


        method.addAnnotation("@Insert({"); //$NON-NLS-1$
        method.addAnnotation("\"<script>\","); //$NON-NLS-1$

        StringBuilder insertClause = new StringBuilder();
        StringBuilder valuesClause = new StringBuilder();

        javaIndent(insertClause, 1);
        javaIndent(valuesClause, 1);

        insertClause.append("\"insert into "); //$NON-NLS-1$
        insertClause.append(escapeStringForJava(introspectedTable
                .getFullyQualifiedTableNameAtRuntime()));
        insertClause.append(" ("); //$NON-NLS-1$

        valuesClause.append("\"values"); //$NON-NLS-1$

        valuesClause.append("<foreach collection=\\\"list\\\" item=\\\"detail\\\" index=\\\"index\\\" separator=\\\",\\\">("); //$NON-NLS-1$

        List<String> valuesClauses = new ArrayList<String>();
        Iterator<IntrospectedColumn> iter = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns())
                .iterator();
        boolean hasFields = false;
        while (iter.hasNext()) {
            IntrospectedColumn introspectedColumn = iter.next();
            insertClause.append(escapeStringForJava(getEscapedColumnName(introspectedColumn)));
            valuesClause.append(getParameterClause(introspectedColumn,"detail."));
            hasFields = true;
            if (iter.hasNext()) {
                insertClause.append(", "); //$NON-NLS-1$
                valuesClause.append(", "); //$NON-NLS-1$
            }

            if (valuesClause.length() > 60) {
                if (!iter.hasNext()) {
                    insertClause.append(')');
                    valuesClause.append(")</foreach></script>");
                }
                insertClause.append("\","); //$NON-NLS-1$
                valuesClause.append('\"');
                if (iter.hasNext()) {
                    valuesClause.append(',');
                }

                method.addAnnotation(insertClause.toString());
                insertClause.setLength(0);
                javaIndent(insertClause, 1);
                insertClause.append('\"');

                valuesClauses.add(valuesClause.toString());
                valuesClause.setLength(0);
                javaIndent(valuesClause, 1);
                valuesClause.append('\"');
                hasFields = false;
            }
        }

        if (hasFields) {
            insertClause.append(")\","); //$NON-NLS-1$
            method.addAnnotation(insertClause.toString());

            valuesClause.append(")"); //$NON-NLS-1$
            valuesClause.append("</foreach></script>\",");
            valuesClauses.add(valuesClause.toString());
        }

        for (String clause : valuesClauses) {
            method.addAnnotation(clause);
        }


        method.addAnnotation("})");

    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
