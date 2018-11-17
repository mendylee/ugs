package com.xrk.usd.bll.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xrk.usd.bll.vo.PointTypeVo;
import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.dao.PointTypeDao;
import com.xrk.usd.dal.entity.UgsPointType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PointTypeBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(PointTypeBusiness.class);
	private final Map<String, List<PointTypeVo>> cache = new HashMap<>();//growingTypeCode=>PointTypeVo list
	private final Map<Integer, PointTypeVo> listCache = new HashMap<>(); //PointTypeVo.pointTypeId=>PointTypeVo
    private final List<UgsPointType> pointTypesCache = new ArrayList<>();
	private static PointTypeBusiness instance = null;
	private static Object syncObj = new Object();

	public static PointTypeBusiness getInstance()
	{
		if (instance == null) {
			synchronized (syncObj) {
				if (instance == null) {
					instance = new PointTypeBusiness();
				}
			}
		}
		return instance;
	}

	private PointTypeDao dao = null;

	private PointTypeBusiness() {
		dao = DalService.getDao(PointTypeDao.class);
		init();
	}

	public void init()
	{
		if (cache.isEmpty()) {
			synchronized (syncObj) {
				if (cache.isEmpty()) {
					List<UgsPointType> all = dao.findAll();
                    pointTypesCache.addAll(all);
					for (UgsPointType type : all) {
						
						List<PointTypeVo> cacheByType = cache.get(type.getUgsGrowingType().getTypeCode());
						if (null == cacheByType) {
							cacheByType = new ArrayList<>();
							cache.put(type.getUgsGrowingType().getTypeCode().toUpperCase(), cacheByType);
                            LOGGER.info("cache add,key:{},size:{}",type.getUgsGrowingType().getTypeCode().toUpperCase(),cacheByType.size());
                            for(PointTypeVo ptv:cacheByType){
                                LOGGER.info("array item,item:{},pointTypeId:{},pointTypeCode:{},pointTypeName:{},growingTypeCode:{},growingTypeName:{}",ptv.toString(),ptv.getPointTypeId(),ptv.getPointTypeCode(),ptv.getPointTypeName(),ptv.getGrowingTypeCode(),ptv.getGrowingTypeName());
                            }
						}else{
                            for(PointTypeVo ptv:cacheByType){
                                LOGGER.info("array item,item:{},pointTypeId:{},pointTypeCode:{},pointTypeName:{},growingTypeCode:{},growingTypeName:{}",ptv.toString(),ptv.getPointTypeId(),ptv.getPointTypeCode(),ptv.getPointTypeName(),ptv.getGrowingTypeCode(),ptv.getGrowingTypeName());
                            }
                        }
						PointTypeVo vo = new PointTypeVo(type.getPointTypeId(), type.getPointTypeCode(), type.getPointTypeName(),
								type.getUgsGrowingType().getTypeCode(), type.getUgsGrowingType().getTypeName());
						cacheByType.add(vo);
						listCache.put(vo.getPointTypeId(), vo);
                        LOGGER.info("cache add,key(pointTypeId):{},value:{},pointTypeCode:{},pointTypeName:{},growingTypeCode:{},growingTypeName:{}",vo.getPointTypeId(),vo.toString(),vo.getPointTypeCode(),vo.getPointTypeName(),vo.getGrowingTypeCode(),vo.getGrowingTypeName());
					}
				}
			}
		}
	}

	public List<PointTypeVo> findBy(String growingTypeCode)
	{
        return cache.get(growingTypeCode.trim().toUpperCase());
	}

	public PointTypeVo findById(int pointTypeId)
	{
        return listCache.get(pointTypeId);
	}

    public UgsPointType findByPointTypeCode(String pointTypeCode){       
        for(UgsPointType pointType:pointTypesCache){
            if(pointType.getPointTypeCode().equalsIgnoreCase(pointTypeCode)){
                return pointType;
            }
        }
        return null;
    }

}
