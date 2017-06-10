package com.github.yuanmomo.test.generator;


import com.github.yuanmomo.mybatis.mbg.table.MySqlTableXmlPrinter;
import org.junit.Test;

import java.util.List;

public class MySqlXMLGeneratorTest {
    @Test
	public void generate(){
		/**
		 * default host : 127.0.0.1
		 * defult port: 3306
		 */
		MySqlTableXmlPrinter mySqlTableXmlPrinter1 = new MySqlTableXmlPrinter("root","root");

//		MySqlTableXmlPrinter mySqlTableXmlPrinter2 = new MySqlTableXmlPrinter("host","port","username","password");

		// three databases named : test, demo, todo
		List<String> output = mySqlTableXmlPrinter1.print("test","demo","todo");
		for(String str : output){
			System.out.println(str);
		}
	}
}
