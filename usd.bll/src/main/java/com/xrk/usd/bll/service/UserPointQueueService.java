package com.xrk.usd.bll.service;

import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mashape.unirest.http.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xrk.usd.bll.common.SysConfig;
import com.xrk.usd.bll.vo.GrowingRuleVo;
import com.xrk.usd.bll.vo.UserPointInvokeVo;


public class UserPointQueueService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(UserPointQueueService.class);
	private static final Logger logSkip = LoggerFactory.getLogger("userPointQueueFailed");

	//每个操作最多执行5次
	protected static final int MAX_RETRY_NUM = 5;
	
	private static boolean bInit = false;
	
	private static ScheduledExecutorService scheduledExecutorService = null;
	private static Queue<UserPointInvokeVo> queue = null;
	
	public static boolean Init(){
		if(bInit){
			return bInit;
		}

        Unirest.setConcurrency(500,100);

        //初始化规则插件服务
		RulePluginService.Init();
				
		//初始化任务队列
		queue = new LinkedBlockingQueue<UserPointInvokeVo>();
		
		//生成调度处理任务线程池		
		int poolNum = SysConfig.getUserPointQueueThreadNum();
		scheduledExecutorService = Executors.newScheduledThreadPool(poolNum);
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {	
			@Override
			public void run()
			{
				UserPointInvokeVo pointVo = get();
				while(pointVo != null){
					if(pointVo.getInvokeNum() > MAX_RETRY_NUM){
						LOGGER.error("RETRY MAX NUM:{}, SKIP UPDATE TASK: uid={}, growingTypeCode={}, ruleCode={}, description={}",
								MAX_RETRY_NUM, pointVo.getUid(), pointVo.getGrowingTypeCode(), pointVo.getRuleCode(), pointVo.getDescription());
						logSkip.info("RETRY FAILED: uid={}, growingTypeCode={}, ruleCode={}, description={}",
								pointVo.getUid(), pointVo.getGrowingTypeCode(), pointVo.getRuleCode(), pointVo.getDescription());						
					}
					else {
                        GrowingRuleVo ruleVo = RulePluginService.getGrowingRule(pointVo.getGrowingTypeCode(), pointVo.getRuleCode());
                        if (ruleVo != null) {
                            try {
                                Object rtn = ruleVo.getPlugin().invoke(pointVo.getUid(), pointVo.getGrowingTypeCode(), pointVo.getRuleCode()
                                        , pointVo.getDescription(), ruleVo.getParams());
                                LOGGER.debug("Invoke user point update finish!rtn={},  uid={}, growingTypeCode={}, ruleCode={}, description={}", 
                                		rtn, pointVo.getUid(), pointVo.getGrowingTypeCode(), pointVo.getRuleCode(), pointVo.getDescription());                                
                            } catch (Throwable ex) {
                                LOGGER.error("Invoke user point update failed! growingTypeCode={}, ruleCode={}, uid={}, description={}, errmsg={}",
                                        ruleVo.getGrowingTypeCode(), ruleVo.getRuleCode(), pointVo.getUid(), pointVo.getDescription(), ex.getMessage());
                                LOGGER.error(ex.getMessage(), ex);
                                pointVo.addInvokeNum();
                                queue(pointVo);
                            }
                        }
                        else{
                        	LOGGER.error("GET GROWING PLUGIN FAILED, SKIP UPDATE TASK! uid={}, growingTypeCode={}, ruleCode={}, description={}",
								pointVo.getUid(), pointVo.getGrowingTypeCode(), pointVo.getRuleCode(), pointVo.getDescription());
                        	logSkip.info("GET GROWING PLUGIN FAILED: uid={}, growingTypeCode={}, ruleCode={}, description={}",
    								pointVo.getUid(), pointVo.getGrowingTypeCode(), pointVo.getRuleCode(), pointVo.getDescription());
                        }
                    }
					
					pointVo = get();
				}				
			}
		},  1, SysConfig.getUserPointQueuePeriod(), TimeUnit.SECONDS);
		
		LOGGER.info("UserPointQueueService init finished!");
		
		bInit = true;
		return bInit;
	}
	
	/**
	 * 
	 * 将需要处理的用户积分任务送入队列
	 *    
	 * @param userPointVo
	 * @return
	 */
	public static boolean queue(UserPointInvokeVo userPointVo){
		LOGGER.info("recv update user point request, add to queue! uid={}, growingTypeCode={}, ruleCode={}, description={}, retryNum={}",
				userPointVo.getUid(), userPointVo.getGrowingTypeCode(), userPointVo.getRuleCode(), userPointVo.getDescription(), userPointVo.getInvokeNum());
		
		return queue.offer(userPointVo);
	}
	
	/**
	 * 
	 * 获取一个待处理的用户积分任务  
	 *    
	 * @return
	 */
	private static UserPointInvokeVo get(){
		return queue.poll();
	}
}
