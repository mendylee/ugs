package com.xrk.usd.bll.business;

import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.dao.GrowingTypeDao;
import com.xrk.usd.dal.entity.UgsGrowingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrowingTypeBusiness{

    private static final Logger LOGGER = LoggerFactory.getLogger(GrowingTypeBusiness.class);
	private static GrowingTypeBusiness _instance = null;
	private static Object syncObj = new Object();
	public static GrowingTypeBusiness getInstance(){
		if(_instance == null){
			synchronized (syncObj) {
	            if(_instance == null){
	            	_instance = new GrowingTypeBusiness();	            	
	            }
            }
		}
		return _instance;
	}
    
	private Map<String, UgsGrowingType> cache = null;
    private GrowingTypeDao dao = null;    
    
    private GrowingTypeBusiness(){
    	cache = new HashMap<>();
    	dao = DalService.getDao(GrowingTypeDao.class);
    	this.init();
    }
    
    private void init(){
    	List<UgsGrowingType> list = dao.findAll();
        for(UgsGrowingType type : list){
            cache.put(type.getTypeCode().trim().toUpperCase(), type);
            LOGGER.info("cache load,key:{},value:{},typeCode:{},typeName:{}",type.getTypeCode().trim().toUpperCase(),type.toString(),type.getTypeCode(),type.getTypeName());
        }
    }
    
    public List<UgsGrowingType> findAll() {
    	List<UgsGrowingType> lsRtn = new ArrayList<>();
    	for(UgsGrowingType vo : cache.values()){
    		lsRtn.add(vo);
    	}
    	return lsRtn;
    }

    public UgsGrowingType findBy(String typeCode){
    	String key = typeCode.trim().toUpperCase();
        UgsGrowingType growingType = cache.get(key);
        return growingType;
    }
}
