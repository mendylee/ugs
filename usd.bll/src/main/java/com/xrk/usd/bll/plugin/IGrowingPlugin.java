package com.xrk.usd.bll.plugin;

import java.util.List;
import java.util.Map;

import com.xrk.usd.bll.plugin.proxy.IGrowingService;
import com.xrk.usd.common.exception.BadRequestException;

public interface IGrowingPlugin {
    /**
     * 获取插件名称
     *
     * @return
     */
    String getName();

    /**
     * 获取插件描述信息
     *
     * @return
     */
    String getDescription();

    /**
     * 获取插件版本
     *
     * @return
     */
    String getVersion();

    /**
     * 获取成长服务接口对象
     *
     * @return
     */
    IGrowingService getGrowingService();

    /**
     * 设置成长服务接口对象实例
     *
     * @param service
     */
    void setGrowingService(IGrowingService service);

    /**
     * 获取插件参数定义列表
     *
     * @return
     */
    List<PluginParameterDefine> getParameterDefine();

    /**
     * 初始化插件操作
     */
    void init();

    /**
     * 销毁插件时执行的操作
     */
    void destory();

    /**
     * 执行插件方法
     *
     * @param params
     * @return
     */
    Object invoke(long uid, String growingTypeCode, String ruleCode, String description, Map<String, Object> params) throws BadRequestException;
}
