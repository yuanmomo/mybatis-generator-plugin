package net.yuanmomo.test.generator;


import net.yuanmomo.mybatis.mbg.table.MySqlTableXmlPrinter;
import org.junit.Test;

import java.util.List;

public class XMLGeneratorTest {
    @Test
	public void testMySqlXmlGenerator(){
		MySqlTableXmlPrinter mySqlTableXmlPrinter = new MySqlTableXmlPrinter("root","root");
		List<String> output = mySqlTableXmlPrinter.print("test","demo","todo");
		for(String str : output){
			System.out.println(str);
		}
	}
}
