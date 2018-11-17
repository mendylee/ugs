package com.xrk.usd.bll.plugin.proxy;

import com.xrk.usd.bll.business.GrowingTypeBusiness;
import com.xrk.usd.bll.business.HistoryBusiness;
import com.xrk.usd.bll.business.PointTypeBusiness;
import com.xrk.usd.bll.business.UserBusiness;
import com.xrk.usd.bll.service.RulePluginService;
import com.xrk.usd.bll.vo.GrowingRuleVo;
import com.xrk.usd.bll.vo.UserPointHistoryVo;
import com.xrk.usd.common.entity.SimplePageResponseEntity;
import com.xrk.usd.common.exception.BusinessException;
import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.dao.UserPointDao;
import com.xrk.usd.dal.dao.UserPointHistoryDao;
import com.xrk.usd.dal.entity.UgsGrowingType;
import com.xrk.usd.dal.entity.UgsPointType;
import com.xrk.usd.dal.entity.UgsUserInfo;
import com.xrk.usd.dal.entity.UgsUserPointHistory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GrowingService implements IGrowingService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GrowingService.class);

	private Set<IUserChangeNotice> hsObserver = new HashSet<>();
	private ExecutorService noticeThreadService = null;

	public GrowingService() {
		hsObserver = new HashSet<>();
		noticeThreadService = Executors.newSingleThreadExecutor();

		historyDao = DalService.getDao(UserPointHistoryDao.class);
		pointDao = DalService.getDao(UserPointDao.class);
	}

	/**
	 * 
	 * 注册一个观察者
	 * 
	 * @param observer
	 * @return
	 */
	@Override
	public boolean registerObserver(IUserChangeNotice observer)
	{
		LOGGER.info("register observer:" + observer.getClass().getTypeName());
		return hsObserver.add(observer);
	}

	/**
	 * 
	 * 移除一个观察者
	 * 
	 * @param observer
	 * @return
	 */
	@Override
	public boolean unregisterObserver(IUserChangeNotice observer)
	{
		LOGGER.info("unregister observer:" + observer.getClass().getTypeName());
		return hsObserver.remove(observer);
	}

	private UserPointHistoryDao historyDao = null;
	private UserPointDao pointDao = null;

	@Override
	public boolean updateUserPoint(long uid, String growingTypeCode, String pointTypeCode,
	                               String ruleCode, int point, String description)
	{
		growingTypeCode = growingTypeCode.trim().toUpperCase();
        UgsPointType pointType = PointTypeBusiness.getInstance().findByPointTypeCode(pointTypeCode);

		//UgsGrowingRuleList rule = ruleDao.findByCode(ruleCode);
		GrowingRuleVo rule = RulePluginService.getGrowingRule(growingTypeCode, ruleCode);

		//UgsGrowingType growingType = typeDao.findById(growingTypeCode);
        UgsGrowingType growingType = GrowingTypeBusiness.getInstance().findBy(growingTypeCode);
		UgsUserInfo user = UserBusiness.getOrCreateUserInfo(growingType, uid);

		UgsUserPointHistory history = new UgsUserPointHistory();
		history.setUgsUserInfo(user);
		history.setTypeCode(growingType.getTypeCode());
		history.setTypeName(growingType.getTypeName());
		history.setAddDate(new Date());
		history.setDescription(StringUtils.isEmpty(description) ? growingType.getTypeName()
                : description);
		history.setPoint(point);
		history.setRuleCode(ruleCode);
		history.setRuleName(rule.getRuleName());
		boolean bRtn = pointDao.increment(pointType, user, history);

        LOGGER.info("update user point,uid:{},growingTypeCode:{},pointTypeCode:{},ruleCode:{},point:{},increment:{}",uid,growingTypeCode,pointTypeCode,ruleCode,point,bRtn);
		if (bRtn) {
			noticeThreadService.execute(new UserChangeNoticeTask(uid, growingTypeCode,
			                                                     pointTypeCode, ruleCode, point,
			                                                     hsObserver));
		}

		return bRtn;
	}
	
	@Override
	public UgsUserPointHistory getLastestHistory(long uid, String growingTypeCode, String ruleCode)
	{		
		UgsUserInfo user = UserBusiness.getUserInfo(growingTypeCode, uid);
		if (null == user) {
			return null;
		}
		
		UgsUserPointHistory history = null;
		try {
            SimplePageResponseEntity<UserPointHistoryVo> ls = HistoryBusiness.getInstance().findBy(uid, growingTypeCode, 10, 1, null, null, ruleCode);
	        if(ls != null && ls.getResult().size() > 0){
	        	UserPointHistoryVo vo = ls.getResult().get(0);
	        	history = new UgsUserPointHistory();
	        	history.setAddDate(new Date(vo.getAddDate()));
	        	history.setDescription(vo.getDescription());
	        	history.setPoint(vo.getPoint());
	        	history.setRuleCode(vo.getRuleCode());
	        	history.setRuleName(vo.getRuleName());
	        	history.setUgsUserInfo(user);
	        }
        }
        catch (BusinessException e) {
	        LOGGER.error(e.getMessage(), e);
        }
		//return historyDao.get(user.getUidSeqId(), ruleCode);
		return history;
	}

}
