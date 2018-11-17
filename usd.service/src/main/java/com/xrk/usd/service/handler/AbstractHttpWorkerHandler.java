package com.xrk.usd.service.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.xrk.hws.http.HttpServer;
import com.xrk.hws.http.HttpWorkerHandler;
import com.xrk.hws.http.context.HttpContext;
import com.xrk.hws.http.monitor.MonitorClient;
import com.xrk.hws.http.monitor.MonitorContext;
import com.xrk.usd.bll.common.BUSINESS_CODE;
import com.xrk.usd.common.annotation.HttpMethod;
import com.xrk.usd.common.annotation.HttpRouterInfo;
import com.xrk.usd.common.collections.SortEntity;
import com.xrk.usd.common.collections.ThreadSafeSortList;
import com.xrk.usd.common.entity.ErrorResponseEntity;
import com.xrk.usd.common.exception.AbstractRedirectException;
import com.xrk.usd.common.exception.BadRequestException;
import com.xrk.usd.common.exception.BusinessException;
import com.xrk.usd.common.exception.HTTP_CODE;
import com.xrk.usd.common.exception.InternalServerException;
import com.xrk.usd.common.exception.MethodNotAllowedException;
import com.xrk.usd.common.exception.PreconditionFailedException;
import com.xrk.usd.common.tools.ClassHelper;

import javassist.NotFoundException;

/**
 * 对业务层处理器的抽像类，主要完成
 * 1.处理方法的加载、注册
 * 2.自定义请求参数的获取
 * 3.控制方法的二次封装，简化前端控制层编写
 * 4.统一处理业务层的异常，返回给客户端
 * AbstractHttpWorkerHandler: AbstractHttpWorkerHandler.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月13日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class AbstractHttpWorkerHandler extends HttpWorkerHandler
{	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpWorkerHandler.class);
	//自定义HTTP常量头
	private final String CLIENT_VERSION = "XRK-CLIENT-VERSION";
	private final String APPID = "XRK-APPID";
	private final String ACCESS_TOKEN = "XRK-ACCESS-TOKEN";
	
	private Map<String, HttpMethod> hsAnnotation = new HashMap<String, HttpMethod>();
	private Map<String, List<String>> hsMethodParams = new HashMap<String, List<String>>(); 
	
	private Gson gson = new Gson();
	
	public AbstractHttpWorkerHandler()
	{		
	}
	
	private void InitMethod()
	{
		Method[] mt = this.getClass().getMethods();
		List<SortEntity<Method>> lsMethod = new ThreadSafeSortList<SortEntity<Method>>();
		for(Method m : mt)
		{
			//如果注解了HttpMethod类,则默认为映射方法
			if(m.isAnnotationPresent(HttpMethod.class))
			{
				HttpMethod hm = m.getAnnotation(HttpMethod.class);
				lsMethod.add(new SortEntity<Method>(hm.priority(), m));
			}
		}
		
		for(SortEntity<Method> method : lsMethod)
		{
			Method m = method.getValue();
			HttpMethod hm = m.getAnnotation(HttpMethod.class);
			//请求函数绑定HTTP方法
			this.addFunction(hm.method().name(), hm.uri(), m);
			LOGGER.info("Add Route Function: method={}, uri={}, status code={}, priority={}, class={}, function={}", 
					hm.method().name(), hm.uri(), hm.code(), hm.priority(), this.getClass().getName(), m.getName());
			//根据映射的方法名缓存注解信息
			hsAnnotation.put(m.getName(), hm);
			String methodKey = String.format("%s_%s", m.getName(), m.getParameterCount());
			//hsMethodParams.put(methodKey, getMethodParamNames(m));
			try {
	            hsMethodParams.put(methodKey, ClassHelper.getParameterNamesByAsm(m));
            }
            catch (NotFoundException e) {
	            LOGGER.error("Get parameter failed!"+e.getMessage(), e);
            }
		}		
	}
	
	public void register(HttpServer server)
    {
		//将当前类注册到路由表中
		HttpRouterInfo router = this.getClass().getAnnotation(HttpRouterInfo.class);
		String strRouter = String.format("^/%s$|^/%s[?/]{1}.*", router.router(), router.router());
		String strMethod = router.method();
		if (server.registerRequestHandler(strMethod, strRouter, this) != 0)
		{
			LOGGER.error("Register handler failed! class={}, method={}, route={}", this.getClass().getName(), strMethod, strRouter);
		}
		else
		{
			LOGGER.info("Register handler success! class={}, method={}, route={}", this.getClass().getName(), strMethod, strRouter);
		}
		
		InitMethod();
    }  
		
	private static ThreadLocal<DateFormat> dtFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };
    
    private static ThreadLocal<DateFormat> dtTimeFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
	/**
	 * 
	 * 转换参数  
	 *    
	 * @param paramType
	 * @param val
	 * @param ctx
	 * @return
	 * @throws ParseException 
	 */
	private Object parseVal(String paramType, String val, HttpContext ctx) throws ParseException
	{
		Object objVal = null;
		Double d = null;
		switch(paramType)
		{
			case "short":
			case "java.lang.Short":
				d = Double.parseDouble(val);
				objVal = d.shortValue();
				break;
			case "int":
			case "java.lang.Integer":
				d = Double.parseDouble(val);				
				objVal = d.intValue();
				break;
			case "long":
			case "java.lang.Long":
				d = Double.parseDouble(val);
				objVal = d.longValue();
				break;
			case "float":
			case "java.lang.Float":
				objVal = Float.parseFloat(val);
				break;
			case "double":
			case "java.lang.Double":
				objVal = Double.parseDouble(val);
				break;
			case "boolean":
			case "java.lang.Boolean":
				objVal = Boolean.parseBoolean(val);
				break;
			case "java.util.Date":
				if(val.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"))
				{
					objVal = dtTimeFormat.get().parse(val);
				}
				else
				{
					objVal = dtFormat.get().parse(val);
				}				
				break;
			case "com.xrk.usd.service.handler.CustomParameter":
				objVal = getCustomParameter(ctx);
				break;
			case "com.xrk.hws.http.context.HttpContext":
				objVal = ctx;
				break;
			default:
				objVal = val;
				break;
		}
		return objVal;
	}
	
	@Override 
	protected void callFunction(Method func, HttpContext ctx) throws InvocationTargetException,IllegalAccessException
    {
		//用以监控的参数
		MonitorContext mctx = null;
		
		StopWatch sw = new StopWatch();
		String clientIP = ctx.request.headers().get("X-Forwarded-For");
		try {
			if(func == null)
			{
				LOGGER.warn("invalid request! method={}, uri={}", ctx.request.getMethod(), ctx.request.getUri());
				MethodNotAllowedException ex = new MethodNotAllowedException(BUSINESS_CODE.HTTP_METHOD_INVALID, "HTTP请求方法不正确");
				InvocationTargetException exp = new InvocationTargetException(ex);
				throw exp;
			}
			sw.start();
			
			//初始化质量日志监控context，并启动监控
			mctx = MonitorClient.getAccessContext();
			MonitorClient.start(mctx);
			
			 if(clientIP == null || clientIP.isEmpty()){
             	InetSocketAddress insocket = (InetSocketAddress) ctx.ctx.channel().remoteAddress();
             	clientIP =  insocket.getAddress().getHostAddress();
             }
			 
			LOGGER.debug("incoming request! process function={}, method={}, uri={}, remote Addr={}, userAgent={}", 
					func.getName(), ctx.request.getMethod(), ctx.request.getUri(), 
					clientIP, ctx.request.headers().get("User-Agent"));
			//分析方法的参数			
			String methodKey = String.format("%s_%s", func.getName(), func.getParameterCount());
			List<String> params = hsMethodParams.get(methodKey);
			List<Object> lsParam = new ArrayList<Object>();
			if(params != null)
			{
				//从请求中获取参数
				int i =0;
				for(Class<?> param : func.getParameterTypes())
				{
					String paramName = params.get(i++);
					
					List<String> lsVal = ctx.getUriAttribute(paramName);
					String val = lsVal != null ? (lsVal.size() > 0 ? lsVal.get(0) : null) : null;
					if(val == null)
					{
						val = ctx.getPostAttrValue(paramName);
					}
					
					try
					{
						Object objVal = parseVal(param.getName(), val, ctx);
						LOGGER.debug("get params: paramName={}, paramType={}, inputVal={}, outVal={}", paramName, param.getName(), val, objVal);						
						lsParam.add(objVal);
					}
					catch(NumberFormatException | ParseException ex)
					{
						//参数转换异常
						sw.stop();
						LOGGER.warn("request parameter invalid! method={}, uri={}, parameter info:name={}, val={}, type={}, uiacRunTime={}",
								ctx.request.getMethod(), ctx.request.getUri(),paramName, val, param.getName(), sw.toString());
						
						BadRequestException exFail = new BadRequestException(BUSINESS_CODE.PARAMER_INVAILD, 
								String.format("参数转换异常:请求参数={},送入参数={},参数类型={}", paramName, val, param.getName()));
						InvocationTargetException exp = new InvocationTargetException(exFail);
						throw exp;
					}
				}
			}

			//调用方法
			Object rtn = func.invoke(this, lsParam.toArray());
			
			//调用成功时的返回值
			int httpCode = HTTP_CODE.OK;
	        //处理返回事件 
			switch(hsAnnotation.get(func.getName()).code())
			{
				case CREATED:
					httpCode = HTTP_CODE.CREATED;
					break;
				case ACCEPTED:
					httpCode = HTTP_CODE.ACCEPTED;
					break;
				case NO_CONTENT:
					httpCode = HTTP_CODE.NO_CONTENT;
					break;
				default:
					httpCode = HTTP_CODE.OK;
					break;
			}
			
			MonitorClient.stop(mctx, ctx, gson.toJson(rtn), String.format("%s##%s", func.getDeclaringClass().getName(), func.getName()), String.valueOf(httpCode));
			this.renderJSON(ctx, rtn, httpCode);
        }
        catch (InvocationTargetException e) {
        	//处理自定义异常
        	Throwable exception = e.getTargetException();
        	
        	BusinessException ex = null;
        	if(exception instanceof AbstractRedirectException)
        	{
        		//重定向异常处理
        		ex = (AbstractRedirectException)exception;
        	}
        	else if(exception instanceof BusinessException)
        	{
        		//其它业务异常
        		ex = (BusinessException)exception;
        	}
        	else
        	{
        		LOGGER.error("call method error!", e);
        		//否则当成服务器内部错误抛出
        		ex = new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR, 
        				String.format("ex={}, traget ex={}", e.getMessage(), exception.getMessage()));
        	}
        	
        	ErrorResponseEntity errObj = new ErrorResponseEntity(ex.getErrCode(), ex.getMessage());
        	MonitorClient.stop(mctx, ctx, gson.toJson(errObj), String.format("%s##%s", func.getDeclaringClass().getName(), func.getName()), String.valueOf(ex.getHttpCode()));
			this.renderJSON(ctx, errObj, ex.getHttpCode());
        }
		
		sw.stop();
		LOGGER.info("end request! httpRunTime={}, process function={}, method={}, uri={}, remote Addr={}, userAgent={} ",
				sw.toString(), func.getName(), ctx.request.getMethod(), ctx.request.getUri(), 
				clientIP, ctx.request.headers().get("User-Agent"));
    }
	
	/**
	 * 
	 * 获取当前请求中的自定义头参数  
	 *    
	 * @param ctx
	 * @return
	 */
	protected CustomParameter getCustomParameter(HttpContext ctx)
	{
		CustomParameter head = new CustomParameter();
		head.setClientVersion(ctx.request.headers().get(CLIENT_VERSION));
		head.setAppId(ctx.request.headers().get(APPID));
		head.setAccessToken(ctx.request.headers().get(ACCESS_TOKEN));
		head.setUri(ctx.request.getUri());
		//获取URI中匹配的分组信息
		head.setUriGroup(getMatcheGroup(ctx));
		return head;
	}
	
	protected long parseLong(String val) throws BusinessException
	{
		long uid = 0;
		try
		{
		 uid = Long.parseLong(val);
		}
		catch(NumberFormatException ex)
		{
			LOGGER.error(ex.getMessage(), ex);
			throw  new PreconditionFailedException(BUSINESS_CODE.PARAMER_INVAILD, String.format("转换参数格式不正确!val={}", val));
		}
		return uid;
	}
}
