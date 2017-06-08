
/**
 * Project Name : Tools
 * File Name    : MapperException.java
 * Package Name : net.yuanmomo.tools.db.orm.mybatis.generator.plugin
 * Created on   : 2014-2-18下午3:51:44
 * Author       : Hongbin Yuan
 * Blog         : yuanmomo.net
 * Company      : 成都逗溜网科技有限公司
 */

package net.yuanmomo.mbg.plugin.selectone;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.sqlprovider.AbstractJavaProviderMethodGenerator;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities.getSelectListPhrase;
import static org.mybatis.generator.internal.util.StringUtility.escapeStringForJava;

/**
 * ClassName : MapperAddSelectOneByExamplePlugin
 * Date      : 2014-2-18 下午3:51:44
 *
 * @author   : Hongbin Yuan
 * @version
 * @since JDK 1.6
 * @see
 */
public class ProviderAddSelectOneByExamplePlugin extends PluginAdapter {
    public static final String SELECT_ONE_BY_EXAMPLE_PROVIDER_METHOD_NAME = "selectOneByExample";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean providerGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        ProviderSelectOneByExampleWithoutBLOBsMethodGenerator providerSelectOneByExampleWithoutBLOBsMethodGenerator
                = new ProviderSelectOneByExampleWithoutBLOBsMethodGenerator(false);

        providerSelectOneByExampleWithoutBLOBsMethodGenerator.setContext(introspectedTable.getContext());
        providerSelectOneByExampleWithoutBLOBsMethodGenerator.setIntrospectedTable(introspectedTable);

        providerSelectOneByExampleWithoutBLOBsMethodGenerator.addClassElements(topLevelClass);

        return super.providerGenerated(topLevelClass,introspectedTable);
    }

    class ProviderSelectOneByExampleWithoutBLOBsMethodGenerator extends
            AbstractJavaProviderMethodGenerator {

        public ProviderSelectOneByExampleWithoutBLOBsMethodGenerator(boolean useLegacyBuilder) {
            super(useLegacyBuilder);
        }

        @Override
        public void addClassElements(TopLevelClass topLevelClass) {
            Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();

            importedTypes.add(NEW_BUILDER_IMPORT);

            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(introspectedTable.getExampleType());
            importedTypes.add(fqjt);

            Method method = new Method(getMethodName());
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

            topLevelClass.addMethod(method);
        }

        public List<IntrospectedColumn> getColumns() {
            return introspectedTable.getNonBLOBColumns();
        }

        public String getMethodName() {
            return SELECT_ONE_BY_EXAMPLE_PROVIDER_METHOD_NAME;
        }
    }
}
