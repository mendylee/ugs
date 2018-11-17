package com.xrk.usd.dal;

import com.xrk.usd.dal.dao.*;
import com.xrk.usd.dal.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataInitializer {
    private static UgsGrowingType B_User;
    
    public static void main(String[] args) {
        DalService.Init("HibernatePersistenceUnit");
        a();
        B_User = DalService.getDao(GrowingTypeDao.class).findById("B_USER");
        b();
        c();
        d();
        UgsPointType pointType = initUgsPointType();
        UgsUserGradeConfig userGradeConfig = initUgsUserGradeConfig(pointType);
        initUgsUserGradeConfigList(userGradeConfig);
        DalService.destory();
    }

    private static UgsPointType initUgsPointType() {
        UgsPointType pointType = new UgsPointType();
        pointType.setUgsGrowingType(B_User);
        pointType.setPointTypeCode("experience_points");
        pointType.setPointTypeName("经验值");

        PointTypeDao pointTypeDao = DalService.getDao(PointTypeDao.class);
        pointTypeDao.persist(pointType);
        return pointType;
    }

    private static UgsUserGradeConfig initUgsUserGradeConfig(UgsPointType pointType) {
        UgsUserGradeConfig userGradeConfig = new UgsUserGradeConfig();
        userGradeConfig.setUgsGrowingType(pointType.getUgsGrowingType());
        userGradeConfig.setGradeName("B端用户等级");
        userGradeConfig.setUgsPointType(pointType);

        UserGradeConfigDao userGradeConfigDao = DalService.getDao(UserGradeConfigDao.class);
        userGradeConfigDao.persist(userGradeConfig);
        return userGradeConfig;
    }

    private static List<UgsUserGradeConfigList> initUgsUserGradeConfigList(UgsUserGradeConfig userGradeConfig) {
        List<UgsUserGradeConfigList> userGradeConfigLists = new ArrayList<>();
        UserGradeConfigListDao userGradeConfigListDao = DalService.getDao(UserGradeConfigListDao.class);
        short level = 0;

        for (int point : new int[]{100, 200, 300, 500, 800, 1100, 1400, 1800, 2300, 2800}) {
            UgsUserGradeConfigListId id = new UgsUserGradeConfigListId();
            id.setGradeId(userGradeConfig.getGradeId());
            id.setLevel(++level);

            UgsUserGradeConfigList userGradeConfigList = new UgsUserGradeConfigList();
            userGradeConfigList.setId(id);
            userGradeConfigList.setPoint(point);
            userGradeConfigListDao.persist(userGradeConfigList);
        }

        return userGradeConfigLists;
    }

    /**
     * 类型
     */
    private static void a() {
        GrowingTypeDao typeDao = DalService.getDao(GrowingTypeDao.class);
        UgsGrowingType type = new UgsGrowingType();
        type.setTypeCode("B_USER");
        type.setStatus((short) 1);
        type.setTypeName("B端");
        typeDao.persist(type);
    }

    /**
     * 分组
     */
    private static void b() {
        GrowingTypeDao typeDao = DalService.getDao(GrowingTypeDao.class);
        GrowingRuleGroupDao groupDao = DalService.getDao(GrowingRuleGroupDao.class);
        GrowingActiveGroupDao activeGroupDao = DalService.getDao(GrowingActiveGroupDao.class);

        List<UgsGrowingType> types = typeDao.findAll();
        UgsGrowingRuleGroup group = new UgsGrowingRuleGroup();
        group.setUgsGrowingType(types.get(0));
        group.setRuleGroupName("普通分组");
        group.setStatus(UgsGrowingRuleGroup.STATUS_NORMAL);
        groupDao.persist(group);

        UgsGrowingActiveGroup activeGroup = new UgsGrowingActiveGroup();
        activeGroup.setTypeCode("B_USER");
        activeGroup.setUgsGrowingType(B_User);
        activeGroup.setUgsGrowingRuleGroup(group);
        activeGroupDao.persist(activeGroup);
    }

    /**
     * 插件
     */
    private static void c() {
        GrowingRulePluginDao pluginDao = DalService.getDao(GrowingRulePluginDao.class);

        UgsGrowingRulePlugin plugin = new UgsGrowingRulePlugin();
        plugin.setAddDate(new Date());
        plugin.setDescription("每个用户只能赠送一次");
        plugin.setPluginClass("com.xrk.usd.bll.plugin.impl.FixPointGrowingPlugin");
        plugin.setPluginName("一次性经验值赠送插件");
        plugin.setVersion("1.0.0");
        pluginDao.persist(plugin);
        plugin = new UgsGrowingRulePlugin();
        plugin.setAddDate(new Date());
        plugin.setDescription("按时间范围多次赠送经验值的插件");
        plugin.setPluginClass("com.xrk.usd.bll.plugin.impl.LimitPointGrowingPlugin");
        plugin.setPluginName("多次经验值赠送插件");
        plugin.setVersion("1.0.0");
        pluginDao.persist(plugin);
    }

    /**
     * 规则
     */
    private static void d() {
        GrowingRuleGroupDao groupDao = DalService.getDao(GrowingRuleGroupDao.class);
        GrowingRulePluginDao pluginDao = DalService.getDao(GrowingRulePluginDao.class);

        List<UgsGrowingRuleGroup> groups = groupDao.findAll();
        UgsGrowingRuleGroup group = groups.get(0);
        List<UgsGrowingRulePlugin> plugins = pluginDao.findAll();
        UgsGrowingRulePlugin fixPlugin = plugins.get(0);
        UgsGrowingRulePlugin limitPlugin = plugins.get(1);
        for (UgsGrowingRulePlugin plugin : plugins) {
            if (plugin.getPluginName().equals("一次性经验值赠送插件")) {
                fixPlugin = plugin;
            } else {
                limitPlugin = plugin;
            }
        }

        persist1("city", "地区", 10, group, fixPlugin);
        persist1("company", "公司", 10, group, fixPlugin);
        persist1("identification", "身份证", 30, group, fixPlugin);
        persist1("practicing_certificate", "执业证", 30, group, fixPlugin);
        persist1("avatar", "头像", 10, group, fixPlugin);
        persist1("professional_photo", "半身照", 30, group, fixPlugin);
        persist1("self_introduction", "个人介绍", 10, group, fixPlugin);

        persist2("authentication", "诚信通年限", 0, "year", 150, group, limitPlugin);
        persist2("forward_reading", "微阅读转发", 1, "day", 2, group, limitPlugin);
        persist2("forward_plan", "V计划", 1, "day", 2, group, limitPlugin);
        persist2("answer_advisory", "问吧问答", 0, "day", 2, group, limitPlugin);
    }

//    private void e() {
//        UserPointDao pointDao = DalService.getDao(UserPointDao.class);
//
//        UgsGrowingType growingType = new UgsGrowingType();
//        growingType.setTypeCode("B_USER");
//        UgsPointType pointType = new UgsPointType();
//        pointType.setPointTypeCode("experience_points");
//        pointType.setPointTypeName("经验值");
//        pointType.setUgsGrowingType(growingType);
//        pointDao.persist(pointType);
//    }

    private static void persist1(String code, String name, int value, UgsGrowingRuleGroup group, UgsGrowingRulePlugin plugin) {
        GrowingRuleParameterDao parameterDao = DalService.getDao(GrowingRuleParameterDao.class);
        GrowingRuleListDao ruleListDao = DalService.getDao(GrowingRuleListDao.class);

        UgsGrowingRuleList item = new UgsGrowingRuleList();
        item.setAddDate(new Date());
        item.setRuleCode(code);
        item.setRuleName(name);
        item.setUserId(0);
        item.setUgsGrowingRuleGroup(group);
        item.setUgsGrowingRulePlugin(plugin);
        ruleListDao.persist(item);

        UgsGrowingRuleParameter parameter = new UgsGrowingRuleParameter();
        UgsGrowingRuleParameterId id = new UgsGrowingRuleParameterId();
        id.setRuleListId(item.getRuleListId());
        id.setParamName("experience_points");
        parameter.setId(id);
        parameter.setParamValue(String.valueOf(value));
        parameterDao.persist(parameter);
    }

    private static void persist2(String code, String name, int value, String unit, int vipValue, UgsGrowingRuleGroup group, UgsGrowingRulePlugin plugin) {
        GrowingRuleParameterDao parameterDao = DalService.getDao(GrowingRuleParameterDao.class);
        GrowingRuleListDao ruleListDao = DalService.getDao(GrowingRuleListDao.class);

        UgsGrowingRuleList item = new UgsGrowingRuleList();
        item.setAddDate(new Date());
        item.setRuleCode(code);
        item.setRuleName(name);
        item.setUserId(0);
        item.setUgsGrowingRuleGroup(group);
        item.setUgsGrowingRulePlugin(plugin);
        ruleListDao.persist(item);


        UgsGrowingRuleParameter parameter = new UgsGrowingRuleParameter();
        UgsGrowingRuleParameterId id = new UgsGrowingRuleParameterId();
        id.setRuleListId(item.getRuleListId());
        id.setParamName("experience_points");
        parameter.setId(id);
        parameter.setParamValue(String.valueOf(value));
        parameterDao.persist(parameter);


        parameter = new UgsGrowingRuleParameter();
        id = new UgsGrowingRuleParameterId();
        id.setRuleListId(item.getRuleListId());
        id.setParamName("time_unit");
        parameter.setId(id);
        parameter.setParamValue(unit);
        parameterDao.persist(parameter);

        parameter = new UgsGrowingRuleParameter();
        id = new UgsGrowingRuleParameterId();
        id.setRuleListId(item.getRuleListId());
        id.setParamName("vip_experience_points");
        parameter.setId(id);
        parameter.setParamValue(String.valueOf(vipValue));
        parameterDao.persist(parameter);

    }
}
