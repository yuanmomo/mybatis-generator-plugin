
/**
 * Project Name : Tools
 * File Name    : DAOPlugin.java
 * Package Name : net.yuanmomo.dwz.tools.db.orm.mybatis.generator.plugin.business
 * Created on   : 2014-2-17下午8:23:27
 * Author       : Hongbin Yuan
 * Blog         : yuanmomo.net
 * Company      : 成都逗溜网科技有限公司  
 */

package net.yuanmomo.mybatis.mbg.plugin;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * ClassName : DAOPlugin 
 * Date      : 2014-2-17 下午8:23:27
 *
 * @author   : Hongbin Yuan
 * @version  
 * @since      JDK 1.6
 * @see 	 
 */
public class DAOPlugin extends PluginAdapter {
	private static String typeName = "DAO";
	
	/**
	 * validate:. <br/>
	 *
	 * @author Hongbin Yuan
	 * @param warnings
	 * @return
	 * @see org.mybatis.generator.api.Plugin#validate(List)
	 */
	public boolean validate(List<String> warnings) {
		return true;
	}

	/**
	 * contextGenerateAdditionalJavaFiles:. <br/>
	 *
	 * @author Hongbin Yuan
	 * @param introspectedTable
	 * @return
	 * @see PluginAdapter#contextGenerateAdditionalJavaFiles(IntrospectedTable)
	 */
	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(
			IntrospectedTable introspectedTable) {
		CommentGenerator commentGenerator = context.getCommentGenerator();

		// 设置文件类型,DAO文件,java 接口文件
		String javaMapperName = introspectedTable.getMyBatis3JavaMapperType();
		String typeNameProp = this.getProperties().getProperty("typeName");
		if(typeNameProp == null || "".equals(typeNameProp.trim())){
			typeNameProp = typeName;
		}
		javaMapperName = javaMapperName.replaceAll("Mapper$",typeNameProp);
		
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(javaMapperName);
		Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);

        String rootInterface = introspectedTable
            .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }
		
        GeneratedJavaFile gjf = new GeneratedJavaFile(interfaze,
			context.getJavaClientGeneratorConfiguration().getTargetProject(),
            context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
            context.getJavaFormatter());
        List<GeneratedJavaFile> gifList = new ArrayList<GeneratedJavaFile>();
        
        gifList.add(gjf);
        
		return gifList;
	}
}
