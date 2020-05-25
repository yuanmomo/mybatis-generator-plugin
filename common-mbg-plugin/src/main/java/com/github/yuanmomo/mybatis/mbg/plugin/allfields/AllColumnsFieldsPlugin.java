package com.github.yuanmomo.mybatis.mbg.plugin.allfields;

import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;


/**
 * Created on 2018-12-10 15:59.
 */

public class AllColumnsFieldsPlugin extends PluginAdapter {

    public AllColumnsFieldsPlugin() {
    }


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    @Override
    public boolean providerGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Field field = new Field("ALL_COLUMN_FIELDS",FullyQualifiedJavaType.getStringInstance());
        field.setVisibility(JavaVisibility.PUBLIC);
        field.setStatic(true);
        field.setFinal(true);

        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        if(allColumns == null || allColumns.size() == 0){
            return true;
        }
        StringBuilder value = new StringBuilder("\"");
        for (IntrospectedColumn column : allColumns) {
            if(value.length() > 1){
                value.append(",");
            }
            value.append(column.getActualColumnName());
        }

        value.append("\"");
        field.setInitializationString(value.toString());

        CommentGenerator commentGenerator = context.getCommentGenerator();

        commentGenerator.addFieldComment(field,introspectedTable);

        topLevelClass.addField(field);
        topLevelClass.addImportedType(field.getType());

        return true;
    }
}
