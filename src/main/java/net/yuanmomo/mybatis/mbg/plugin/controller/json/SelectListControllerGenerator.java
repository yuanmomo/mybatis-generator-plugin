package net.yuanmomo.mybatis.mbg.plugin.controller.json;

import net.yuanmomo.springboot.util.AjaxResponseBean;
import net.yuanmomo.springboot.util.PaginationBean;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;

import java.util.ArrayList;
import java.util.List;


public class SelectListControllerGenerator {
	public static List<Method> generator(FullyQualifiedJavaType beanType, String beanName,
                                         String businessFieldName, String criteriaType){
		List<Method> methodList = new ArrayList<Method>();
		
		Method method = new Method();
        method.addAnnotation("@RequestMapping(value = \"select" + beanName + "List.do\")");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType(AjaxResponseBean.class.getName()));
        method.setName("select" + beanName + "List");
        Parameter param1 = new Parameter(new FullyQualifiedJavaType(Short.class.getName()), "conditionType");
        param1.addAnnotation("@RequestParam(value = \"conditionType\",required = false) ");
        Parameter param2 = new Parameter(FullyQualifiedJavaType.getStringInstance(), "conditionValue");
        param2.addAnnotation("@RequestParam(value =\"conditionValue\",required = false) ");
        Parameter param3 = new Parameter(new FullyQualifiedJavaType(PaginationBean.class.getName()), "paginationBean");
        param3.addAnnotation("@ModelAttribute ");
        method.addParameter(param1); 
        method.addParameter(param2); 
        method.addParameter(param3); 
        // 方法body
        method.addBodyLine("try {");
        method.addBodyLine("long currentPage = paginationBean.getPageNum();");
        method.addBodyLine("long pageSize = paginationBean.getNumPerPage(); ");
        method.addBodyLine("");
        method.addBodyLine("if(pageSize < 1){");
        method.addBodyLine("return AjaxResponseBean.Const.PARAMETER_INVALID_ERROR_RESPONSE_BEAN; ");
        method.addBodyLine("}");
        method.addBodyLine("if(currentPage<1){");
        method.addBodyLine("return AjaxResponseBean.Const.PARAMETER_INVALID_ERROR_RESPONSE_BEAN; ");
        method.addBodyLine("}");
        method.addBodyLine("// 构造查询参数");
        method.addBodyLine(""+criteriaType + " param =new " + criteriaType + "();");
        method.addBodyLine("//"+criteriaType + ".Criteria criteria = param.createCriteria();");
        method.addBodyLine("");
        method.addBodyLine("// 根据参数设置查询条件");
        method.addBodyLine("");
        method.addBodyLine("// 取得当前查询的总记录结果");
        method.addBodyLine("long total = this." + businessFieldName + ".count" + beanName + "List(param);");
        method.addBodyLine("if(total == 0){");
        method.addBodyLine("// 没有记录数");
        method.addBodyLine("return AjaxResponseBean.getNoDataReturnValueResponseBean();");
        method.addBodyLine("}");
        method.addBodyLine("paginationBean.setTotalCount(total);");
        method.addBodyLine("// 判断当前请求的页码有没有超过总页数");
        method.addBodyLine("long totalPages = PaginationUtil.getPages(total, pageSize);");
        method.addBodyLine("paginationBean.setTotalPages(totalPages);");
        method.addBodyLine("");
        method.addBodyLine("if(currentPage > totalPages){");
        method.addBodyLine("// 当前页超过总页数,取最大数");
        method.addBodyLine("currentPage = totalPages;");
        method.addBodyLine("paginationBean.setPageNum(currentPage);");
        method.addBodyLine("}");
        method.addBodyLine("");
        method.addBodyLine("// 设置排序");
        method.addBodyLine("// param.setOrderByClause(\" id asc \");");
        method.addBodyLine("");
        method.addBodyLine("long start = (currentPage - 1) * pageSize;");
        method.addBodyLine("param.setStart(start);");
        method.addBodyLine("param.setCount(pageSize);");
        method.addBodyLine("");
        method.addBodyLine("List<" + beanName + "> " + StringUtils.uncapitalize(beanName) +"List = this." + businessFieldName + ".select" + beanName + "List(param);");
        method.addBodyLine("");
        method.addBodyLine("paginationBean.setResult(" + StringUtils.uncapitalize(beanName) +"List);  // 返回数据结果");
        method.addBodyLine("return AjaxResponseBean.getReturnValueResponseBean(paginationBean);");
        method.addBodyLine("} catch (Exception e) {");
        method.addBodyLine("logger.error(\"查询异常\" + e.getMessage());");
        method.addBodyLine("return AjaxResponseBean.getErrorResponseBean(\"查询异常\" + e.getMessage());");
        method.addBodyLine("}"); 
		
		methodList.add(method);
        
        return methodList;
	}
}
