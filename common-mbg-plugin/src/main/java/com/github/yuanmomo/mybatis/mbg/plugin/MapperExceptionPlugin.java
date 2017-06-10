package com.github.yuanmomo.mybatis.mbg.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class MapperExceptionPlugin extends PluginAdapter {
	
	private void addException(Method method, IntrospectedTable introspectedTable){
		String exceptionClassStr = this.getProperties().getProperty("exceptionClass");
		try {
			method.addException(new FullyQualifiedJavaType(exceptionClassStr));
		} catch (Exception e) {
			method.addException(new FullyQualifiedJavaType(RuntimeException.class.getName()));
		}
	};

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean clientGenerated(Interface interfaze,
                                   TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		List<Method> methodList = interfaze.getMethods();
		if(methodList !=null && methodList.size()>0){
			for(Method m : methodList){
				// add 'throws xxxException' to every method in mapper interface.
				addException(m,introspectedTable);
			}
		}
		return true;
	}
}
