package com.github.yuanmomo.mybatis.mbg;

import com.github.yuanmomo.mybatis.mbg.util.JavaFilesMergeUtil;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyBatisGeneratorTool {
	private static Logger logger = LoggerFactory.getLogger(MyBatisGeneratorTool.class);
	
	/**
	 *  call MyBatis Generator method.
	 *
	 * @param generatorConfigPath  the path of generatorConfig.xml file. e.g: src/test/resources/generatorConfig.xml
	 * @param warnings warnings
	 */
	public static void generate(String generatorConfigPath,List<String> warnings) {
		if(generatorConfigPath == null || "".equals(generatorConfigPath.trim())){
			logger.error("GeneratorConfig.xml location null.");
			return;
		}
		
		logger.info("Current path is " + new File(".").getAbsolutePath());
		File configFile = new File(generatorConfigPath);
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = null;
		try {
			config = cp.parseConfiguration(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLParserException e) {
			e.printStackTrace();
		}
		// whether overwrite specific Java files (like java models)
		boolean overwrite = false;
		boolean isMergeSupported= true;
		JavaFilesMergeUtil callback = new JavaFilesMergeUtil(overwrite,isMergeSupported);
		MyBatisGenerator myBatisGenerator = null;
		try {
			myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		try {
			myBatisGenerator.generate(null);
		} catch (SQLException e) {
			logger.error("SQLException",e);
		} catch (IOException e) {
			logger.error("IOException",e);
		} catch (InterruptedException e) {
			logger.error("InterruptedException",e);
		}
	}

	/**
	 *
	 * @param generatorConfigPath the config file path
	 */
	public static void generate(String generatorConfigPath) {
		List<String> warnings = new ArrayList<String>();
		generate(generatorConfigPath,warnings);
    }

}
