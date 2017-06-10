package com.github.yuanmomo.test.generator;


import com.github.yuanmomo.mybatis.mbg.MyBatisGeneratorTool;
import org.junit.Test;

public class GeneratorMainTest {
	@Test
	public void testMBGGenerator() {
		MyBatisGeneratorTool.generate("src/test/resources/generatorConfig.xml");
	}
}
