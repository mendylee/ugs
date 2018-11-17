package com.xrk.usd.bll.plugin.impl;

import com.xrk.usd.bll.business.MemberBusiness;
import com.xrk.usd.bll.common.BUSINESS_CODE;
import com.xrk.usd.bll.common.CalendarUtil;
import com.xrk.usd.bll.plugin.DefaultGrowingPlugin;
import com.xrk.usd.bll.plugin.PluginParameterDefine;
import com.xrk.usd.bll.vo.MemberVo;
import com.xrk.usd.common.exception.BadRequestException;
import com.xrk.usd.common.exception.InternalServerException;
import com.xrk.usd.dal.entity.UgsUserPointHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LimitPointGrowingPlugin extends DefaultGrowingPlugin
{

	private Logger logger = LoggerFactory.getLogger(LimitPointGrowingPlugin.class);
	private ConcurrentHashMap<String, Object> mutex = new ConcurrentHashMap<>();

	public LimitPointGrowingPlugin() {
	}

	@Override
	public void init()
	{
		PluginParameterDefine param = new PluginParameterDefine();
		param.setParamCode("experience_points");
		param.setParamName("经验值");
		param.setDescription("多次赠送经验值插件");
		param.setParamType(0);
		param.setControlType(0);
		this.addParams(param);
	}

	@Override
	public void destory()
	{

	}

	@Override
	public Object invoke(long uid, String growingTypeCode, String ruleCode, String description,
	                     Map<String, Object> params) throws BadRequestException
	{
		String mutexKey = String.format("%s:%s:%s", uid, growingTypeCode, ruleCode).toUpperCase();
		Object lastMutex = this.mutex.putIfAbsent(mutexKey, new Object());
		if (lastMutex != null) {
			// 已经有一个并发请求在执行加分操作
			this.logger.warn(String.format("Duplicate operation of key: %s", mutexKey));
			return false;
		}

		boolean valid = false;
		int point = 0;

		try {
			UgsUserPointHistory history = this.service.getLastestHistory(uid, growingTypeCode, ruleCode);
			if (null == history) {
				valid = true;
			}

			MemberVo member = MemberBusiness.getMemberFromHttp(uid);

			if (!member.isMember()) {
				logger.warn(
				        "uid is not member, skip update! uid={}, growingTypeCode={}, ruleCode={}, description={}",
				        uid, growingTypeCode, ruleCode, description);
				//throw new BadRequestException(BUSINESS_CODE.UID_INVAILD, "用户ID无效");
				return false;
			}
			boolean vip = member.isVIP();
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				if (key.equalsIgnoreCase("time_unit") && null != history) {
					String v = params.get(key).toString();
					switch (v) {
						case "day":
                            Calendar begin = CalendarUtil.getBeginByDay();
                            if (history.getAddDate().before(begin.getTime())) {
                                valid = true;
                            }
							break;
						case "year":
                            Calendar expireDate = Calendar.getInstance();
                            expireDate.setTime(history.getAddDate());
                            expireDate.add(Calendar.YEAR,1);
                            expireDate.set(Calendar.HOUR_OF_DAY, 0);
                            expireDate.set(Calendar.MINUTE,0);
                            expireDate.set(Calendar.SECOND, 0);
                            expireDate.set(Calendar.MILLISECOND, 0);
                            Date now = new Date();
							if(now.after(expireDate.getTime())){
                                valid = true;
                            }
							break;
					}
				}
				else if (key.equalsIgnoreCase("experience_points") && !vip) {
					point = Integer.parseInt(params.get(key).toString());
				}
				else if (key.equalsIgnoreCase("vip_experience_points") && vip) {
					point = Integer.parseInt(params.get(key).toString());
				}
			}

            if(point==0){
                logger.info(
                        "user point is 0! skip it! uid={}, isvip={}, growingTypeCode={}, ruleCode={}, description={}",
                        uid, vip, growingTypeCode, ruleCode, description);
                return false;
            }

			if (valid) {
				this.service.updateUserPoint(uid, growingTypeCode, "experience_points", ruleCode,
				        point, description);
			}
			else {
				logger.info(
				        "user point was update! skip it! uid={}, isvip={}, growingTypeCode={}, ruleCode={}, description={}",
				        uid, vip, growingTypeCode, ruleCode, description);
			}
			return true;
		}
		catch (InternalServerException e) {
			logger.error(e.getMessage());
			throw new BadRequestException(BUSINESS_CODE.PARAMER_INVAILD, e.getMessage());
		}
		finally {
			this.mutex.remove(mutexKey);
		}
	}

}
