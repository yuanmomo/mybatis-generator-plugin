package net.yuanmomo.mybatis.mbg.plugin.controller.json;

import net.yuanmomo.mybatis.mbg.PluginUtil;
import net.yuanmomo.mybatis.mbg.plugin.business.BusinessPluginUtil;
import net.yuanmomo.springboot.util.AjaxResponseBean;
import net.yuanmomo.springboot.util.NumberUtil;
import net.yuanmomo.springboot.util.PaginationBean;
import net.yuanmomo.springboot.util.PaginationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.PropertyRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControllerPluginUtil{

	public static void main(String[] args) {
		System.out.println(AjaxResponseBean.class.getName());
	}
	/**
	 *  controller需要导入的包列表
	 */
	public static Set<Class<?>> CONTROLLER_IMPORT_CLASS_SET = new HashSet<>();
	static{
		CONTROLLER_IMPORT_CLASS_SET.add(List.class);
		CONTROLLER_IMPORT_CLASS_SET.add(AjaxResponseBean.class);
		CONTROLLER_IMPORT_CLASS_SET.add(CollectionUtils.class);
		CONTROLLER_IMPORT_CLASS_SET.add(PaginationUtil.class);
		CONTROLLER_IMPORT_CLASS_SET.add(PaginationBean.class);
		CONTROLLER_IMPORT_CLASS_SET.add(Logger.class);
		CONTROLLER_IMPORT_CLASS_SET.add(NumberUtil.class);
		CONTROLLER_IMPORT_CLASS_SET.add(LoggerFactory.class);
		CONTROLLER_IMPORT_CLASS_SET.add(Autowired.class);
		CONTROLLER_IMPORT_CLASS_SET.add(List.class);
		CONTROLLER_IMPORT_CLASS_SET.add(RestController.class);
		CONTROLLER_IMPORT_CLASS_SET.add(RequestMapping.class);
		CONTROLLER_IMPORT_CLASS_SET.add(ModelAttribute.class);
		CONTROLLER_IMPORT_CLASS_SET.add(RequestParam.class);
	}
	
	/**
	 * 	生成java business 文件
	 * 
	 * @return
	 */
	public static List<GeneratedJavaFile> getControllerJavaFile(
			Context context,
			IntrospectedTable introspectedTable,
			String controllerPackage,
			String javaControllerName,
			String businessPackage,
			String javaBusinessName,
			String exceptionClass){
		// 当前bean的完整名称 (包名 + 类名: xxxx.xxx.xx.xx.Test)
		String beanClass = introspectedTable.getBaseRecordType();
		// Bean的名称(Test)
		String beanName = introspectedTable.getTableConfiguration().getDomainObjectName();
		// bean Field Name
        String beanFieldName = StringUtils.uncapitalize(beanName);
		FullyQualifiedJavaType beanType = new FullyQualifiedJavaType(beanClass);
		// BeanExample的名称(TestCriteria)
		String criteriaName = introspectedTable.getExampleType();
		FullyQualifiedJavaType criteriaType = new FullyQualifiedJavaType(criteriaName);
		
		// business类的包名和类名
		businessPackage = PluginUtil.getBusinessPackage(businessPackage, beanClass);
		javaBusinessName = PluginUtil.getBusinessName(javaBusinessName, beanName);
		
		// controller类的包名和类名
		controllerPackage = PluginUtil.getControllerPackage(controllerPackage, beanClass);
		javaControllerName = PluginUtil.getControllerName(javaControllerName, beanName);
		
		// 生成business 文件
		List<GeneratedJavaFile> businessFileList = BusinessPluginUtil.getBusinessJavaFile(context, introspectedTable,
				businessPackage, javaBusinessName);
		// business type
		FullyQualifiedJavaType businessType = new FullyQualifiedJavaType(businessPackage + "." + javaBusinessName);
		// business Field Name
        String businessFieldName = StringUtils.uncapitalize(javaBusinessName);
		
		// 注释生成器
		CommentGenerator commentGenerator = context.getCommentGenerator();
		
		FullyQualifiedJavaType controllerType = new FullyQualifiedJavaType(controllerPackage + "." + javaControllerName);
		TopLevelClass targetControllerClass = new TopLevelClass(controllerType);
        targetControllerClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(targetControllerClass);
        
        targetControllerClass.addAnnotation(String.format("@%s", RestController.class.getSimpleName()));
		targetControllerClass.addAnnotation(String.format("@%s(\"%s%s\")",RequestMapping.class.getSimpleName(),"/backend/",beanFieldName));
        for(Class<?> clazz : CONTROLLER_IMPORT_CLASS_SET){
        	targetControllerClass.addImportedType(clazz.getName());
        }
        targetControllerClass.addImportedType(beanClass);
        
        // 导入bean类
        targetControllerClass.addImportedType(beanClass);
        // 导入criteria类
        targetControllerClass.addImportedType(criteriaName);
        // 添加business包
        targetControllerClass.addImportedType(businessType);
        // 自定义Exception的导入、获取类名
        String exceptionClassName = null;
        if(exceptionClass != null){
        	// 导入自定义Exception类
        	targetControllerClass.addImportedType(exceptionClass);
        	// 获取Exception类名
        	int index = exceptionClass.lastIndexOf(".");
        	if(index != -1)
        		exceptionClassName = exceptionClass.substring(index+1);
        }
        
        // add logger field
        Field loggerField = PluginUtil.getLoggerField(javaControllerName);
        if(loggerField != null){
        	commentGenerator.addFieldComment(loggerField, introspectedTable);
        	targetControllerClass.addField(loggerField);
        }
        // add business field
        Field field = PluginUtil.getField(String.format("@%s",Autowired.class.getSimpleName()), businessType, businessFieldName);
        if(field != null){
        	commentGenerator.addFieldComment(field, introspectedTable);
        	targetControllerClass.addField(field);
        }
        
        List<Method> methodList = new ArrayList<Method>();

        // add insert
        List<Method> insertMethodList = InsertControllerGenerator.generator(beanType, beanName, businessFieldName);
        // 添加自定义异常
        addCustomException(insertMethodList,exceptionClassName);
        if(!CollectionUtils.isEmpty(insertMethodList)){
        	methodList.addAll(insertMethodList);
        }
        
        // add getXXXByKey
        // 判断是否包含主键
        GeneratedKey generatedKey = introspectedTable.getTableConfiguration().getGeneratedKey();
        if(generatedKey != null){
	        String keyColumn = generatedKey.getColumn();
	        FullyQualifiedJavaType keyType = introspectedTable.getColumn(keyColumn).getFullyQualifiedJavaType();
	        if(generatedKey != null){
	        	List<Method> getByKeyMethodList = GetByKeyControllerGenerator.generator(beanType, beanName, businessFieldName,
	        			keyType,keyColumn);
	        	// 添加自定义异常
	        	addCustomException(getByKeyMethodList,exceptionClassName);
	        	if(!CollectionUtils.isEmpty(getByKeyMethodList)){
	            	methodList.addAll(getByKeyMethodList);
	            }
	        }
        }
        // add selectBeanList
        List<Method> selectListMethodList = SelectListControllerGenerator.generator(beanType, beanName, businessFieldName,
        		criteriaType.getShortName());
        // 添加自定义异常
    	addCustomException(selectListMethodList,exceptionClassName);
        if(!CollectionUtils.isEmpty(selectListMethodList)){
        	methodList.addAll(selectListMethodList);
        }
        
        // add updateSaveBean
        List<Method> updateSaveBeanMethodList = UpdateControllerGenerator.generator(beanType, beanName, businessFieldName);
        // 添加自定义异常
    	addCustomException(updateSaveBeanMethodList,exceptionClassName);
        if(!CollectionUtils.isEmpty(updateSaveBeanMethodList)){
        	methodList.addAll(updateSaveBeanMethodList);
        }
        
        InnerClass innerClass = InnerClassControllerGenerator.generator(controllerType, beanName);
        targetControllerClass.addInnerClass(innerClass);
        
        
        // add batchUpdateSaveBean
        List<Method> batchUpdateSaveBeanMethodList = BatchUpdateControllerGenerator.generator(innerClass.getType(), beanName, businessFieldName);
        // 添加自定义异常
    	addCustomException(batchUpdateSaveBeanMethodList,exceptionClassName);
        if(!CollectionUtils.isEmpty(batchUpdateSaveBeanMethodList)){
        	methodList.addAll(batchUpdateSaveBeanMethodList);
        }
        
        for(Method m : methodList){
    		commentGenerator.addGeneralMethodComment(m, introspectedTable);
    		targetControllerClass.addMethod(m);
    	}
        
		GeneratedJavaFile gjf = new GeneratedJavaFile(targetControllerClass,
				context.getJavaClientGeneratorConfiguration().getTargetProject(),
	            context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
	            context.getJavaFormatter());
		
		List<GeneratedJavaFile> list = new ArrayList<GeneratedJavaFile>();
		list.add(gjf);
		list.addAll(businessFileList);
		return list;
	}

	private static void addCustomException(List<Method> methodList,
			String exceptionClassName) {
		if(CollectionUtils.isEmpty(methodList) || StringUtils.isEmpty(exceptionClassName))
			return;
		Method method = methodList.get(0);
		if(method == null)
			return;
		int index = 0 ;
		String compare = "} catch (Exception e) {";
		List<String> lines = method.getBodyLines();
		for(String line:lines){
			if(compare.equals(line))
				break;
			index++;
		}
		method.addBodyLine(index, "return AjaxResponseBean.getErrorResponseBean(\"异常\" + e1.getKey());");
		method.addBodyLine(index, "} catch ("+exceptionClassName+" e1) {");
	}
}
