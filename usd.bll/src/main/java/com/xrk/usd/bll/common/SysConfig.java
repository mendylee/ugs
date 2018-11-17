package com.xrk.usd.bll.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SysConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(SysConfig.class);
    private static Properties properties = null;

    public static void Init(String filename) {
        InputStream in = null;
        try {
            in = new FileInputStream(filename);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            LOGGER.error(e1.getMessage(), e1);
            return;
        }

        properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException e) {
            LOGGER.error("Failed to load app.properties, msg: %s", e.getMessage());
            return;
        }
    }

    public static String getCacheClass() {
        return properties.getProperty("cache", "com.xrk.usd.bll.cache.memory.MemoryCache");
    }

    public static int getUserPointQueueThreadNum() {
        String val = properties.getProperty("point_update_queue_thread", "5");
        return Integer.parseInt(val);
    }

    public static long getUserPointQueuePeriod() {
        String val = properties.getProperty("point_queue_period", "5");
        return Integer.parseInt(val);
    }

    public static String getPersistenceUnitName() {
        return properties.getProperty("persistence_unit_name", "HibernatePersistenceUnit");
    }

    public static int getLRUCacheSize() {
        return Integer.parseInt(properties.getProperty("lruSize"));
    }

    public static int getHttpPort() {
        String val = properties.getProperty("http_port", "8181");
        return Integer.parseInt(val);
    }
    
    public static int getHttpReadTimeout() {
        String val = properties.getProperty("http_read_timeout", "180");
        return Integer.parseInt(val);
    }
    
    public static int getHttpWriteTimeout() {
        String val = properties.getProperty("http_write_timeout", "60");
        return Integer.parseInt(val);
    }

    public static int getPointHistoryMaxCacheSize(){
        String val = properties.getProperty("point_history_max_cache_size", "1000");
        return Integer.parseInt(val);
    }

    public static String getMemberUrl(){
        return properties.getProperty("member_url", "");
    }

    public static long getMemberCacheTime(){
        String val = properties.getProperty("member_cache_time", "12");
        return Integer.parseInt(val);
    }

	public static int getUserLRUCacheSize() {		
		return Integer.parseInt(properties.getProperty("lruUserSize"));
	}
	
	public static int getPointLRUCacheSize() {		
		return Integer.parseInt(properties.getProperty("lruUserPointSize"));
	}
}
