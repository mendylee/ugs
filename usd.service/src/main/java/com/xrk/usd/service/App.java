package com.xrk.usd.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.Set;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xrk.hws.http.HttpServer;
import com.xrk.hws.http.monitor.MonitorClient;
import com.xrk.usd.bll.cache.CacheService;
import com.xrk.usd.bll.common.SysConfig;
import com.xrk.usd.bll.service.UserPointQueueService;
import com.xrk.usd.common.annotation.HttpRouterInfo;
import com.xrk.usd.common.tools.ClassHelper;
import com.xrk.usd.dal.DalService;
import com.xrk.usd.service.handler.AbstractHttpWorkerHandler;

public class App
{
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	public static void main(String[] args)
	{
		String appBasePath = "";
		try {
			CodeSource codeSource = App.class.getProtectionDomain().getCodeSource();
	        appBasePath = URLDecoder.decode(codeSource.getLocation().toURI().getPath(), "UTF-8");
    		File jarFile = new File(appBasePath);
        	appBasePath = jarFile.getParentFile().getPath();
        }
        catch (URISyntaxException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
		String configPath = String.format("%s/config/", appBasePath);
		System.out.println("configPath="+configPath);
		String log4jPath = String.format("%slog4j.xml", configPath);
		DOMConfigurator.configureAndWatch(log4jPath, 60000);
		//加载系统配置项
		String filename = configPath + "/app.properties";
		SysConfig.Init(filename);
		
		//加载DAL
		DalService.Init(SysConfig.getPersistenceUnitName());
		
		//应用程序初始化操作，如：配置加载、环境初始化等 
		CacheService.Init();
		
		//用户积分队列服务初始化
		UserPointQueueService.Init();
					
		//加载质量日志监控组件
		try
		{
			MonitorClient.init(configPath);
		}
		catch (Exception e)
		{
			LOGGER.error("fail to init MonitorClient", e);
		}
				
		HttpServer server = new HttpServer(SysConfig.getHttpReadTimeout(), SysConfig.getHttpWriteTimeout());
		// 线程数默认为处理器数目
		int processNum = Runtime.getRuntime().availableProcessors();
		server.init(processNum, processNum * 2, processNum * 2, null);
		server.addListen(new InetSocketAddress(SysConfig.getHttpPort()));

		// 自动加载指定包下的所有处理器
		Set<Class<?>> set = ClassHelper.getClasses("com.xrk.usd.service.handler");
		for (Class<?> classes : set) {
			if (classes.isAnnotationPresent(HttpRouterInfo.class)) {
				try {
					AbstractHttpWorkerHandler handler = (AbstractHttpWorkerHandler) classes
					        .newInstance();
					handler.register(server);
				}
				catch (InstantiationException e) {
					LOGGER.error(e.getMessage(), e);
				}
				catch (IllegalAccessException e) {
					LOGGER.error(e.getMessage(), e);
				}
			} 
		}		
		LOGGER.info("start http server, port:{}", SysConfig.getHttpPort());
		server.run();
	}
}
