package com.github.yuanmomo.mybatis.mbg.plugin.getone;

import static org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities.getSelectListPhrase;
import static org.mybatis.generator.internal.util.StringUtility.escapeStringForJava;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.sqlprovider.AbstractJavaProviderMethodGenerator;

import com.github.yuanmomo.mybatis.mbg.plugin.dynamic.MethodTypeEnum;
import com.github.yuanmomo.mybatis.mbg.plugin.dynamic.DynamicTableNamePlugin;

public class ProviderGetOneByExampleWithoutBLOBsMethodGenerator extends AbstractJavaProviderMethodGenerator {

    private String methodName;

    public ProviderGetOneByExampleWithoutBLOBsMethodGenerator(boolean useLegacyBuilder) {
        super(useLegacyBuilder);
    }

    @Override
    public void addClassElements(TopLevelClass topLevelClass) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();

        importedTypes.add(NEW_BUILDER_IMPORT);

        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        importedTypes.add(fqjt);


        Method method = new Method(methodName);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.addParameter(new Parameter(fqjt, "example")); //$NON-NLS-1$

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        method.addBodyLine("SQL sql = new SQL();"); //$NON-NLS-1$

        boolean distinctCheck = true;
        for (IntrospectedColumn introspectedColumn : getColumns()) {
            if (distinctCheck) {
                method.addBodyLine("if (example != null && example.isDistinct()) {"); //$NON-NLS-1$
                method.addBodyLine(String.format("%sSELECT_DISTINCT(\"%s\");", //$NON-NLS-1$
                        builderPrefix,
                        escapeStringForJava(getSelectListPhrase(introspectedColumn))));
                method.addBodyLine("} else {"); //$NON-NLS-1$
                method.addBodyLine(String.format("%sSELECT(\"%s\");", //$NON-NLS-1$
                        builderPrefix,
                        escapeStringForJava(getSelectListPhrase(introspectedColumn))));
                method.addBodyLine("}"); //$NON-NLS-1$
            } else {
                method.addBodyLine(String.format("%sSELECT(\"%s\");", //$NON-NLS-1$
                        builderPrefix,
                        escapeStringForJava(getSelectListPhrase(introspectedColumn))));
            }

            distinctCheck = false;
        }

        method.addBodyLine(String.format("%sFROM(\"%s\");", //$NON-NLS-1$
                builderPrefix,
                escapeStringForJava(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime())));
        method.addBodyLine("applyWhere(sql, example, false);"); //$NON-NLS-1$

        method.addBodyLine(""); //$NON-NLS-1$
        method.addBodyLine("if (example != null && example.getOrderByClause() != null) {"); //$NON-NLS-1$
        method.addBodyLine(String.format("%sORDER_BY(example.getOrderByClause());", builderPrefix)); //$NON-NLS-1$
        method.addBodyLine("}"); //$NON-NLS-1$

        method.addBodyLine(""); //$NON-NLS-1$
        method.addBodyLine("StringBuilder sqlBuilder = new StringBuilder(sql.toString());");
        method.addBodyLine("sqlBuilder.append(\" limit 1 \");");
        method.addBodyLine("return sqlBuilder.toString();"); //$NON-NLS-1$

        //
        if (DynamicTableNamePlugin.DYNAMIC_TABLE_NAME_PLUGIN_ACTIVE){
            DynamicTableNamePlugin.providerChangeMethodParameterToMap(MethodTypeEnum.EXAMPLE,method,topLevelClass,introspectedTable);
        }

        topLevelClass.addMethod(method);
    }

    public List<IntrospectedColumn> getColumns() {
        return introspectedTable.getAllColumns();
    }


    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
