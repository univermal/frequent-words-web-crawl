package com.purini.fw;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Loads and stores application properties.
 * Singleton object, so that it can be accessed from anywhere.
 * Has util methods to give out int, string or string list properties.
 * Also provides constants for property keys for easy reference.
 */
public class PropertyStore {

    private static final Logger logger = LoggerFactory.getLogger(PropertyStore.class);
    private static final String propsFile = "/application.properties";
    private static final PropertyStore instance = new PropertyStore();
    private final Properties properties;

    public static final String DOCUMENT_PROCESSOR_CORE_POOL_SIZE = "documentProcessorCorePoolSize";
    public static final String DOCUMENT_PROCESSOR_MAX_POOL_SIZE = "documentProcessorMaxPoolSize";
    public static final String STOP_WORDS = "stopWords";

    public PropertyStore() {
        try (InputStream inputStream = getClass().getResourceAsStream(propsFile)){
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error("while loading properties", e);
            throw new RuntimeException(e);
        }
    }

    public static PropertyStore getInstance() {
        return instance;
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }

    public int getIntProperty(String key){
        return Integer.parseInt(properties.getProperty(key));
    }

    public List<String> getPropertyList(String key) {
        String value = properties.getProperty(key);
        if (StringUtils.isNotEmpty(value)) {
            return Arrays.asList(value.split(","));
        } else {
            logger.warn("Empty value for property {}", key);
            return Collections.emptyList();
        }

    }
}
