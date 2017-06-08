
/**
 * Project Name : Tools
 * File Name    : MapperException.java
 * Package Name : net.yuanmomo.tools.db.orm.mybatis.generator.plugin
 * Created on   : 2014-2-18下午3:51:44
 * Author       : Hongbin Yuan
 * Blog         : yuanmomo.net
 * Company      : 成都逗溜网科技有限公司
 */

package net.yuanmomo.mybatis.mbg.plugin.selectone;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.SelectByExampleWithoutBLOBsMethodGenerator;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;

/**
 * ClassName : MapperAddSelectOneByExamplePlugin
 * Date      : 2014-2-18 下午3:51:44
 *
 * @author   : Hongbin Yuan
 * @version
 * @since JDK 1.6
 * @see
 */
public class MapperAddSelectOneByExamplePlugin extends PluginAdapter {

    /**
     * validate:. <br/>
     *
     * @author Hongbin Yuan
     * @param warnings
     * @return
     * @see org.mybatis.generator.api.Plugin#validate(List)
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * clientGenerated:. <br/>
     *
     * @author Hongbin Yuan
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     * @see PluginAdapter#clientGenerated(Interface, TopLevelClass, IntrospectedTable)
     */
    @Override
    public boolean clientGenerated(Interface interfaze,
                                   TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // add a select one method
        AnnotatedSelectOneByExampleWithoutBLOBsMethodGenerator annotatedSelectOneByExampleWithoutBLOBsMethodGenerator
                = new AnnotatedSelectOneByExampleWithoutBLOBsMethodGenerator();

        annotatedSelectOneByExampleWithoutBLOBsMethodGenerator.setContext(context);
        annotatedSelectOneByExampleWithoutBLOBsMethodGenerator.setIntrospectedTable(introspectedTable);

        annotatedSelectOneByExampleWithoutBLOBsMethodGenerator.addInterfaceElements(interfaze);
        return true;
    }

    static class AnnotatedSelectOneByExampleWithoutBLOBsMethodGenerator extends
            SelectByExampleWithoutBLOBsMethodGenerator {

        public AnnotatedSelectOneByExampleWithoutBLOBsMethodGenerator() {
            super();
        }

        @Override
        public void addInterfaceElements(Interface interfaze) {
            Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
            FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                    introspectedTable.getExampleType());
            importedTypes.add(type);

            Method method = new Method();
            method.setVisibility(JavaVisibility.PUBLIC);

            FullyQualifiedJavaType returnType = introspectedTable.getRules()
                    .calculateAllFieldsClass();
            method.setReturnType(returnType);
            importedTypes.add(returnType);


            method.setName("selectOneByExample");
            method.addParameter(new Parameter(type, "example")); //$NON-NLS-1$

            context.getCommentGenerator().addGeneralMethodComment(method,
                    introspectedTable);

            addMapperAnnotations(interfaze, method);

        }


        @Override
        public void addMapperAnnotations(Interface interfaze, Method method) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(introspectedTable.getMyBatis3SqlProviderType());
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.SelectProvider")); //$NON-NLS-1$
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.type.JdbcType")); //$NON-NLS-1$

            if (introspectedTable.isConstructorBased()) {
                interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Arg")); //$NON-NLS-1$
                interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.ConstructorArgs")); //$NON-NLS-1$
            } else {
                interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Result")); //$NON-NLS-1$
                interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Results")); //$NON-NLS-1$
            }

            StringBuilder sb = new StringBuilder();
            sb.append("@SelectProvider(type="); //$NON-NLS-1$
            sb.append(fqjt.getShortName());
            sb.append(".class, method=\""); //$NON-NLS-1$
            sb.append(ProviderAddSelectOneByExamplePlugin.SELECT_ONE_BY_EXAMPLE_PROVIDER_METHOD_NAME);
            sb.append("\")"); //$NON-NLS-1$

            method.addAnnotation(sb.toString());

            if (introspectedTable.isConstructorBased()) {
                method.addAnnotation("@ConstructorArgs({"); //$NON-NLS-1$
            } else {
                method.addAnnotation("@Results({"); //$NON-NLS-1$
            }

            Iterator<IntrospectedColumn> iterPk = introspectedTable.getPrimaryKeyColumns().iterator();
            Iterator<IntrospectedColumn> iterNonPk = introspectedTable.getBaseColumns().iterator();
            while (iterPk.hasNext()) {
                IntrospectedColumn introspectedColumn = iterPk.next();
                sb.setLength(0);
                javaIndent(sb, 1);
                sb.append(getResultAnnotation(interfaze, introspectedColumn, true,
                        introspectedTable.isConstructorBased()));

                if (iterPk.hasNext() || iterNonPk.hasNext()) {
                    sb.append(',');
                }

                method.addAnnotation(sb.toString());
            }

            while (iterNonPk.hasNext()) {
                IntrospectedColumn introspectedColumn = iterNonPk.next();
                sb.setLength(0);
                javaIndent(sb, 1);
                sb.append(getResultAnnotation(interfaze, introspectedColumn, false,
                        introspectedTable.isConstructorBased()));

                if (iterNonPk.hasNext()) {
                    sb.append(',');
                }

                method.addAnnotation(sb.toString());
            }

            method.addAnnotation("})"); //$NON-NLS-1$
            interfaze.addMethod(method);
        }
    }



}
