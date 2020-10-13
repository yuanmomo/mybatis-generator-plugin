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
import org.mybatis.generator.internal.util.JavaBeansUtil;

/**
 * @author Hongbin Yuan
 */
public class PaginationPlugin extends PluginAdapter {

    /**
     * Add two fields start and count with default value of -1 into Criteria class.
     *
     * @param topLevelClass     topLevelClass.
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
        return true;
    }

    /**
     * Modify the selectByExample element in the mapper xml file.
     *
     * @param element           selectByExample element in xml file.
     * @param introspectedTable current table configurations.
     * @return boolean result
     */
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        this.addPaginationInXml(element);
        return true;
    }
    /**
     * Modify the selectByExampleWithBlobs element in the mapper xml file.
     *
     * @param element           selectByExampleWithBlobs element in xml file.
     * @param introspectedTable current table configurations.
     * @return boolean result
     */
    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        this.addPaginationInXml(element);
        return true;
    }
    /**
     * Append pagination to method in xml.
     *
     * @param element append pagination code to method.
     */
    private void addPaginationInXml(XmlElement element) {
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
    }

    /**
     * Modify the selectByExample in sql provider class.
     *
     * @param method            selectByExample method.
     * @param topLevelClass     topLevelClass
     * @param introspectedTable current table configurations.
     * @return boolean result
     */
    @Override
    public boolean providerSelectByExampleWithoutBLOBsMethodGenerated(
            Method method,
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        appendPaginationInProvider(method);
        return true;
    }

    /**
     * Modify the selectByExampleWithBlobs in sql provider class.
     *
     * @param method            selectByExampleWithBlobs method.
     * @param topLevelClass     topLevelClass
     * @param introspectedTable current table configurations.
     * @return boolean result
     */
    @Override
    public boolean providerSelectByExampleWithBLOBsMethodGenerated(
            Method method,
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        appendPaginationInProvider(method);
        return true;
    }

    /**
     * Append pagination to method in provider.
     *
     * @param method append pagination code to method.
     */
    private void appendPaginationInProvider(Method method) {

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
    }

    /**
     * Add a field into class both with setter and getter. <br/>
     *
     * @param topLevelClass        topLevelClass
     * @param introspectedTable    current table configurations.
     * @param fieldType            field type
     * @param property             field name.
     * @param initializationString initializationString
     */
    private void addField(TopLevelClass topLevelClass,
                          IntrospectedTable introspectedTable,
                          FullyQualifiedJavaType fieldType, String property,
                          String initializationString) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        // add field
        Field field = new Field(property, fieldType);
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setInitializationString(initializationString);
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);

        // add setter
        Method method = new Method(JavaBeansUtil.getSetterMethodName(property));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(fieldType, property));
        method.addBodyLine(String.format("this.%s = %s;", property, property));
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);

        // add getter
        method = new Method(JavaBeansUtil.getGetterMethodName(property, fieldType));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(fieldType);
        method.addBodyLine(String.format("return %s;", property));
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
