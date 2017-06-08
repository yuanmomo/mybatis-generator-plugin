package net.yuanmomo.mybatis.generator;

import net.yuanmomo.mybatis.mbg.MyBatisGeneratorTool;

public class GeneratorMain {
	public static void main(String args[]) {
		MyBatisGeneratorTool.generate("src/test/resources/generatorConfig.xml");
	}
}
