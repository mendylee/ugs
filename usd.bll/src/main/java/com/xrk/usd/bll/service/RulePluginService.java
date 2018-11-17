package com.xrk.usd.bll.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xrk.usd.bll.plugin.IGrowingPlugin;
import com.xrk.usd.bll.plugin.proxy.GrowingService;
import com.xrk.usd.bll.plugin.proxy.IGrowingService;
import com.xrk.usd.bll.vo.GrowingRuleVo;
import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.dao.GrowingActiveGroupDao;
import com.xrk.usd.dal.entity.UgsGrowingRuleList;
import com.xrk.usd.dal.entity.UgsGrowingRuleParameter;
import com.xrk.usd.dal.entity.UgsGrowingRulePlugin;
import com.xrk.usd.dal.entity.UgsGrowingType;

public class RulePluginService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RulePluginService.class);
	
	private static Map<String, GrowingRuleVo> mapRulePlugin = null;
	private static Map<Integer, IGrowingPlugin> mapPlugin = null;
	private static boolean bInit = false;
	private static IGrowingService proxy_service;
	
	public static boolean Init(){
		if(bInit){
			return bInit;
		}
		
		if(mapRulePlugin == null){
			mapRulePlugin = new HashMap<String, GrowingRuleVo>();
		}
		else{
			mapRulePlugin.clear();
		}
		
		if(mapPlugin == null){
			mapPlugin    = new HashMap<Integer, IGrowingPlugin>();
		}
		
		GrowingActiveGroupDao dao = DalService.getDao(GrowingActiveGroupDao.class);
		proxy_service = new GrowingService();
		
		Map<UgsGrowingType, Set<UgsGrowingRuleList>> mapRules = dao.getGrowingMap();
		for(Map.Entry<UgsGrowingType,Set<UgsGrowingRuleList>> val : mapRules.entrySet()){
			UgsGrowingType type = val.getKey();
			for(UgsGrowingRuleList rule : val.getValue()){
				String ruleCode = rule.getRuleCode();
				String key = formatKey(type.getTypeCode(), ruleCode);
				 
				UgsGrowingRulePlugin plugin = rule.getUgsGrowingRulePlugin();
				IGrowingPlugin pluginObj = mapPlugin.get(plugin.getPluginId());
				if(pluginObj == null){
					String className = plugin.getPluginClass();
					try {
		                Class<?> classes = Class.forName(className);
		                pluginObj = (IGrowingPlugin) classes.newInstance();
		                pluginObj.setGrowingService(proxy_service);
	                    pluginObj.init();
	                    mapPlugin.put(plugin.getPluginId(), pluginObj);
	                    LOGGER.info("load growing rule plugin: growingTypeCode={}, pluginId={}, pluginName={}, pluginClass={}",
	                    		type.getTypeCode(), plugin.getPluginId(), plugin.getPluginName(), plugin.getPluginClass());
	                }
	                catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
		                LOGGER.error("load growing rule plugin error: className={}, error={}", className, e.getMessage());
		                LOGGER.warn("ignore growing rule loading! growingTypeCode={}, ruleId={}, ruleCode={}, ruleName={}",
		                		type.getTypeCode(), rule.getRuleListId(), rule.getRuleCode(), rule.getRuleName());
		                continue;
	                }
				}	
				
				GrowingRuleVo pluginWarp = new GrowingRuleVo(pluginObj, type.getTypeCode(), ruleCode, rule.getRuleName());
				//add rule object
				for(UgsGrowingRuleParameter param : rule.getUgsGrowingRuleParameters()){
					pluginWarp.addParams(param.getId().getParamName(), param.getParamValue());
				}
				
				mapRulePlugin.put(key, pluginWarp);
				LOGGER.info("loading growing rule! growingTypeCode={}, ruleId={}, ruleCode={}, ruleName={}, pluginId={}, pluginClass={}",
                		type.getTypeCode(), rule.getRuleListId(), rule.getRuleCode(), rule.getRuleName(), plugin.getPluginId(), plugin.getPluginClass());
			}
		}
		LOGGER.info("RulePluginService init finished!");
		
		bInit = true;
		return bInit;
	}
	
	public static IGrowingService getGrowingService(){
		return proxy_service;
	}
	
	/**
	 * 
	 * 根据成长体系代码和规则代码生成唯一Key  
	 *    
	 * @param growingTypeCode
	 * @param ruleCode
	 * @return
	 */
	public static String formatKey(String growingTypeCode, String ruleCode){
		return String.format("%s_%s", growingTypeCode.trim(), ruleCode.trim()).toUpperCase();
	}
	
	/**
	 * 
	 * 检测指定的成长体系代码和成长规则代码是否符有效  
	 *    
	 * @param growingTypeCode
	 * @param ruleCode
	 * @return
	 */
	public static boolean contains(String growingTypeCode, String ruleCode){
		if(!bInit){
			return false;
		}
		
		String key = formatKey(growingTypeCode, ruleCode);
		return mapRulePlugin.containsKey(key);
	}
	
	/**
	 * 
	 * 根据成长体系代码和规则代码获取处理插件实例  
	 *    
	 * @param growingTypeCode
	 * @param ruleCode
	 * @return
	 */
	public static GrowingRuleVo getGrowingRule(String growingTypeCode, String ruleCode){
		if(!bInit){
			return null;
		}
		
		String key = formatKey(growingTypeCode, ruleCode);
		return mapRulePlugin.get(key);
	}
	
}
