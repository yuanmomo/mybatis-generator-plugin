package net.yuanmomo.mybatis.mbg.plugin;
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

import java.util.List;

/**
 * @author Hongbin Yuan
 * @date 2011-11-30 下午08:36:11
 */
public class PaginationPlugin extends PluginAdapter {
    /**
     * Add two fields start and count with default value of -1 into Criteria class.
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
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
     * @param element
     * @param introspectedTable
     * @return
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
     * @param method
     * @param topLevelClass
     * @param introspectedTable
     * @return
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
     * @param topLevelClass
     * @param introspectedTable
     * @param fieldType
     * @param name
     * @param initializationString
     */
    private void addField(TopLevelClass topLevelClass,
                          IntrospectedTable introspectedTable,
                          FullyQualifiedJavaType fieldType, String name,
                          String initializationString) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        // add field
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(fieldType);
        field.setName(name);
        field.setInitializationString(initializationString);
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);

        // upper first char
        char c = name.charAt(0);
        String camel = Character.toUpperCase(c) + name.substring(1);

        // add setter
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("set" + camel);
        method.addParameter(new Parameter(fieldType, name));
        method.addBodyLine("this." + name + "=" + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);

        // add getter
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(fieldType);
        method.setName("get" + camel);
        method.addBodyLine("return " + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
