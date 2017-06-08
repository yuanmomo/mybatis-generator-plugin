package net.yuanmomo.generator;

import net.yuanmomo.mbg.MyBatisGeneratorTool;

public class GeneratorMain {
	public static void main(String args[]) {
		MyBatisGeneratorTool.generate("src/test/resources/generatorConfig.xml");
	}
}
