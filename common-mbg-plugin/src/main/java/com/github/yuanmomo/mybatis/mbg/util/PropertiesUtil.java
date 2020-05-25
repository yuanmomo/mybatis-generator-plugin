package com.github.yuanmomo.mybatis.mbg.util;

import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */

public class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    /**
     *
     * @param properties
     * @param name
     * @return
     */
    public static boolean getBooleanProp(Properties properties, String name) {
        return getBooleanProp(properties,name,false);
    }

    /**
     *
     * @param properties
     * @param name
     * @param defaultValue
     * @return
     */
    public static boolean getBooleanProp(Properties properties, String name, boolean defaultValue) {
        try {
            if (properties == null){
                return defaultValue;
            }
            if (StringUtils.isBlank(properties.getProperty(name))) {
                return defaultValue;
            }
            return BooleanUtils.toBoolean(properties.getProperty(name));
        } catch (Exception e) {
            logger.error("Parse property:[{}] to boolean error", name, e);
        }
        return defaultValue;
    }


}