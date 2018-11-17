package com.xrk.usd.dal;

import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xrk.usd.common.tools.ClassHelper;
import com.xrk.usd.dal.dao.DaoBase;

/**
 * 数据访问服务
 * DalService: DalService.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年9月10日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class DalService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DalService.class);
	private static EntityManagerFactory factory = null;
	private static Map<String, DaoBase<?>> mapDao = null;
	private static boolean bInit = false;
	private static String _persistenceUnitName = "HibernatePersistenceUnit";
	public static String getPersistenceName(){
		return _persistenceUnitName;
	}
	
	/**
	 * 
	 * 仅供单元测试或内部测试使用，业务代码不可使用，随时废除此函数  
	 *    
	 * @return
	 */
	@Deprecated
	public static boolean Init(){
		return Init("HibernatePersistenceUnit");
	}
	
	
	public static void onError(Exception e){
		if(e instanceof JDBCConnectionException ||
        		(e.getCause() != null && e.getCause() instanceof JDBCConnectionException)){
        	resetFactory();
        }
	}
	
	private static AtomicLong lastRest = new AtomicLong(0);
	private static Object syncObj = new Object();
	private static int delay_time = 1000 * 60;//1分钟内不重置Factory
	public static void resetFactory(){
		synchronized (syncObj) {
			long last = lastRest.get();
			long curr = new Date().getTime();
			if(curr < last){
				return;
			}
			
			destory();
			
			try{
				factory = Persistence.createEntityManagerFactory(getPersistenceName());
				DaoBase.setFactory(factory);
			}
			catch(Exception ex){
				LOGGER.error(ex.getMessage(), ex);
			}
			
			lastRest.set(curr+delay_time);
		}
	}
	
	public static boolean Init(String persistenceUnitName){
		if(bInit){
			return bInit;
		}
		
		_persistenceUnitName = persistenceUnitName;
		resetFactory();
		
		mapDao = new HashMap<String, DaoBase<?>>();
		
		// 自动加载指定包下的所有处理器
		Class<?> base = DaoBase.class;
		Set<Class<?>> set = ClassHelper.getClasses("com.xrk.usd.dal.dao");
		for (Class<?> classes : set) {
			//忽略基本类型
			if(classes.isInterface() 
					|| classes.isAnnotation()
					|| classes.isAnonymousClass()
					|| classes.isEnum()
					|| Modifier.isAbstract(classes.getModifiers()) 
					|| !base.isAssignableFrom(classes)
					|| classes.isPrimitive()){
				continue;
			}
			
			String superClassName =classes.getSuperclass().getName();
			if(superClassName.equals(base.getName()))
			{
				try {
					DaoBase<?> dao = (DaoBase<?>) classes.newInstance();
					mapDao.put(classes.getName(), dao);
					LOGGER.info("load db dao class:{}", classes.getName());
				}
				catch (InstantiationException e) {
					LOGGER.error(e.getMessage(), e);
				}
				catch (IllegalAccessException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		bInit = true;
		
		return bInit;
	}
	
	public static void destory(){
		if(factory != null){
			try{
				factory.close();
			}
			catch(Exception ex){
				LOGGER.error(ex.getMessage(), ex);
			}
		}
	}
	
	/**
	 * 
	 * 根据DAO的类获取DAO的实体对象  
	 *    
	 * @param classType
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public static <T> T getDao(Class<T> classType){		
		return (T)mapDao.get(classType.getName());
	}
}
