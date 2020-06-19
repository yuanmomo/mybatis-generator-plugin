package com.github.yuanmomo.mybatis.mbg.plugin;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * @author Hongbin Yuan
 */
public class PaginationPlugin extends PluginAdapter {
    /**
     * Add two fields start and count with default value of -1 into Criteria class.
     *
     * @param topLevelClass   topLevelClass.
     * @param introspectedTable current table configurations.
     * @return boolean result
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {
        // add field, getter, setter for limit start clause
        this.addField(topLevelClass, introspectedTable,
                new FullyQualifiedJavaType("long"), "start", "-1");
        // add field, getter, setter for limit count clause
        this.addField(topLevelClass, introspectedTable,
                new FullyQualifiedJavaType("long"), "count", "-1");
        return super.modelExampleClassGenerated(topLevelClass,
                introspectedTable);
    }

    /**
     *  Modify the selectByExample element in the mapper xml file.
     *
     * @param element selectByExample element in xml file.
     * @param introspectedTable current table configurations.
     * @return boolean result
     */
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        // add start field
        XmlElement limitStartElement = new XmlElement("if");
        limitStartElement.addAttribute(new Attribute("test", "start &gt;= 0 "));
        limitStartElement.addElement(new TextElement("limit ${start}"));
        element.addElement(limitStartElement);

        // add count field
        XmlElement limitEndElement = new XmlElement("if");
        limitEndElement.addAttribute(new Attribute("test", "count &gt;= 0 "));
        limitEndElement.addElement(new TextElement(",${count}"));
        element.addElement(limitEndElement);

        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element,
                introspectedTable);
    }

    /**
     * Modify the selectByExample in sql provider class.
     *
     * @param method  selectByExample method.
     * @param topLevelClass topLevelClass
     * @param introspectedTable current table configurations.
     * @return boolean result
     */
    @Override
    public boolean providerSelectByExampleWithoutBLOBsMethodGenerated(
            Method method,
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {

        List<String> bodyLines = method.getBodyLines();
        // delete the line of "return sql.toString();"
        bodyLines.remove(bodyLines.size() - 1);

        bodyLines.add("// add pagination for mysql with limit clause ");
        bodyLines.add("StringBuilder sqlBuilder = new StringBuilder(sql.toString());");
        bodyLines.add("if(example != null && (example.getStart() > -1 || example.getCount() > -1) ){");
        bodyLines.add("sqlBuilder.append(\" limit \");");
        bodyLines.add("if(example.getStart() > -1 && example.getCount() > -1){");
        bodyLines.add("sqlBuilder.append(example.getStart()).append(\",\").append(example.getCount());");
        bodyLines.add("}else if( example.getStart() > -1 ){");
        bodyLines.add("sqlBuilder.append(example.getStart());");
        bodyLines.add("}else if( example.getCount() > -1 ){");
        bodyLines.add("sqlBuilder.append(example.getCount());");
        bodyLines.add("}");
        bodyLines.add("}");
        bodyLines.add("return sqlBuilder.toString();");

        return super.providerSelectByExampleWithoutBLOBsMethodGenerated(method, topLevelClass, introspectedTable);
    }

    /**
     * Add a field into class both with setter and getter. <br/>
     *
     * @param topLevelClass topLevelClass
     * @param introspectedTable current table configurations.
     * @param fieldType  field type
     * @param name  field name.
     * @param initializationString  initializationString
     */
    private void addField(TopLevelClass topLevelClass,
                          IntrospectedTable introspectedTable,
                          FullyQualifiedJavaType fieldType, String name,
                          String initializationString) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        // add field
        Field field = new Field(name,fieldType);
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setInitializationString(initializationString);
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);

        // upper first char
        char c = name.charAt(0);
        String camel = Character.toUpperCase(c) + name.substring(1);

        // add setter
        Method method = new Method("set" + camel);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(fieldType, name));
        method.addBodyLine("this." + name + "=" + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);

        // add getter
        method = new Method("get" + camel);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(fieldType);
        method.addBodyLine("return " + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
