package com.xrk.usd.bll.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xrk.usd.bll.vo.GrowingTypeVo;
import com.xrk.usd.common.exception.BusinessException;
import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.dao.UserGradeConfigListDao;
import com.xrk.usd.dal.entity.UgsGrowingType;
import com.xrk.usd.dal.entity.UgsUserGradeConfig;
import com.xrk.usd.dal.entity.UgsUserGradeConfigList;

public class GradeBusiness{

    private static final Logger LOGGER = LoggerFactory.getLogger(GradeBusiness.class);
    private static final Map<String,List<UgsUserGradeConfigList>> cache = new HashMap<>();//typeCode=>List<UgsUserGradeConfigList>
    private UserGradeConfigListDao listDao = null;

    private static GradeBusiness instance = null;
	private static Object syncObj = new Object();

	public static GradeBusiness getInstance()
	{
		if (instance == null) {
			synchronized (syncObj) {
				if (instance == null) {
					instance = new GradeBusiness();
				}
			}
		}
		return instance;
	}
    
    private GradeBusiness(){
    	listDao = DalService.getDao(UserGradeConfigListDao.class);
    	init();
    }

    private String formatKey(String key){
    	return key.trim().toUpperCase();
    }
    
    private void init()
    {
	    List<UgsGrowingType> ls = GrowingTypeBusiness.getInstance().findAll();
	    for(UgsGrowingType type : ls){
	    	String key = formatKey(type.getTypeCode());
	    	List<UgsUserGradeConfigList> ret = this.findBy(type.getTypeCode());
			cache.put(key, ret);
			LOGGER.info("cache add, key:{}, size:{}", key, ret.size());
	    }
    }

	public List<UgsUserGradeConfigList> findConfigListBy(String typeCode)throws BusinessException{
        return cache.get(formatKey(typeCode));
    }

    private List<UgsUserGradeConfigList> findBy(String typeCode){
        return listDao.findBy(typeCode);
    }

    public List<UgsUserGradeConfigList> findConfigListBy(String typeCode,long gradeId)throws BusinessException{
        List<UgsUserGradeConfigList> ret = new ArrayList<>();

        List<UgsUserGradeConfigList> list = findConfigListBy(typeCode);
        for(UgsUserGradeConfigList configList:list){
            if(configList.getUgsUserGradeConfig().getGradeId()==gradeId){
                ret.add(configList);
            }
        }
//        list.forEach(configList->{
//            if(configList.getUgsUserGradeConfig().getGradeId()==gradeId){
//                ret.add(configList);
//            }
//        });
        return ret;
    }

    public List<UgsUserGradeConfig> findConfigs(String typeCode) throws BusinessException {
        List<UgsUserGradeConfig> ret = new ArrayList<>();
        Map<Integer,UgsUserGradeConfig> configMap = new HashMap<>();
        List<UgsUserGradeConfigList> lists = this.findConfigListBy(typeCode);
        for(UgsUserGradeConfigList configList:lists){
            configMap.put(configList.getUgsUserGradeConfig().getGradeId(),configList.getUgsUserGradeConfig());
        }
        ret.addAll(configMap.values());
        return ret;
    }

    public List<UgsUserGradeConfig> findConfigsBy(String growingType, long pointTypeId) throws BusinessException {
        List<UgsUserGradeConfig> ret = new ArrayList<>();
        List<UgsUserGradeConfig> all = this.findConfigs(growingType);
        for(UgsUserGradeConfig config:all){
            if(config.getUgsPointType().getPointTypeId()==pointTypeId){
                ret.add(config);
            }
        }
        return ret;
    }

}
