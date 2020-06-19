package com.github.yuanmomo.mybatis.mbg.plugin.getone;

/**
 * Created by Hongbin.Yuan on 2017-06-09 00:33.
 */

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.SelectByExampleWithoutBLOBsMethodGenerator;

import com.github.yuanmomo.mybatis.mbg.plugin.dynamic.DynamicTableNamePlugin;

public class AnnotatedGetOneByExampleWithoutBLOBsMethodGenerator extends
        SelectByExampleWithoutBLOBsMethodGenerator {
    private String methodName;

    public AnnotatedGetOneByExampleWithoutBLOBsMethodGenerator() {
        super();
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getExampleType());
        importedTypes.add(type);

        Method method = new Method(methodName);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(true);

        interfaze.addImportedType(new FullyQualifiedJavaType(Optional.class.getName())); //$NON-NLS-1$
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(Optional.class.getSimpleName());
        returnType.addTypeArgument(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));


        method.setReturnType(returnType);
        importedTypes.add(returnType);


        method.addParameter(new Parameter(type, "example")); //$NON-NLS-1$

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        addMapperAnnotations(interfaze, method);

    }

    @Override
    public void addMapperAnnotations(Interface interfaze, Method method) {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(introspectedTable.getMyBatis3SqlProviderType());
        interfaze.addImportedType(new FullyQualifiedJavaType(SelectProvider.class.getName())); //$NON-NLS-1$
        interfaze.addImportedType(new FullyQualifiedJavaType(JdbcType.class.getName())); //$NON-NLS-1$

        if (introspectedTable.isConstructorBased()) {
            interfaze.addImportedType(new FullyQualifiedJavaType(Arg.class.getName())); //$NON-NLS-1$
            interfaze.addImportedType(new FullyQualifiedJavaType(ConstructorArgs.class.getName())); //$NON-NLS-1$
        } else {
            interfaze.addImportedType(new FullyQualifiedJavaType(Result.class.getName())); //$NON-NLS-1$
            interfaze.addImportedType(new FullyQualifiedJavaType(Results.class.getName())); //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();
        sb.append("@SelectProvider(type="); //$NON-NLS-1$
        sb.append(fqjt.getShortName());
        sb.append(".class, method=\""); //$NON-NLS-1$
        sb.append(methodName);
        sb.append("\")"); //$NON-NLS-1$

        method.addAnnotation(sb.toString());

        if (introspectedTable.isConstructorBased()) {
            method.addAnnotation("@ConstructorArgs({"); //$NON-NLS-1$
        } else {
            method.addAnnotation("@Results({"); //$NON-NLS-1$
        }

        Iterator<IntrospectedColumn> iterPk = introspectedTable.getPrimaryKeyColumns().iterator();
        Iterator<IntrospectedColumn> iterNonPk = introspectedTable.getNonPrimaryKeyColumns().iterator();
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

        //
        if (DynamicTableNamePlugin.DYNAMIC_TABLE_NAME_PLUGIN_ACTIVE){
            DynamicTableNamePlugin.mapperAddTableNameParameter(method,interfaze,introspectedTable);
        }

        interfaze.addMethod(method);
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
