package com.xrk.usd.dal.test;

import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.dao.*;
import com.xrk.usd.dal.entity.*;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.LoggerFactory;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.List;

public class InitDBTest {

    private GrowingTypeDao typeDao = DalService.getDao(GrowingTypeDao.class);
    private GrowingRuleGroupDao groupDao = DalService.getDao(GrowingRuleGroupDao.class);
    private GrowingRulePluginDao pluginDao = DalService.getDao(GrowingRulePluginDao.class);
    private GrowingRuleListDao ruleListDao = DalService.getDao(GrowingRuleListDao.class);
    private GrowingRuleParameterDao parameterDao = DalService.getDao(GrowingRuleParameterDao.class);
    private UserPointDao pointDao = DalService.getDao(UserPointDao.class);
    private GrowingActiveGroupDao activeGroupDao = DalService.getDao(GrowingActiveGroupDao.class);

    static {
        DalService.Init();
    }

    @BeforeClass
    public static void SetUpClass() {
    }

    @AfterClass
    public static void tearDownAfterClass() {
        System.out.println("Tear down After class");
    }

    @Before
    public void SetUp() {

    }

    @After
    public void tearDown() throws Exception {
        System.out.println("Tear down");
    }

    @Test
    public void t() {
        clearDB();
//        a();
//        b();
//        c();
//        d();
//        e();
    }

    private void clearDB() {
        String persistenceUnitName = "HibernatePersistenceUnit";
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(persistenceUnitName);
        DaoBase.setFactory(factory);

        EntityManager em = factory.createEntityManager();
        EntityTransaction tran = em.getTransaction();

        try {
            tran.begin();
            em.createNativeQuery("truncate table ugs_growing_active_group CASCADE").executeUpdate();
            em.createNativeQuery("truncate table ugs_growing_rule_group CASCADE").executeUpdate();
            em.createNativeQuery("truncate table ugs_growing_rule_list CASCADE").executeUpdate();
            em.createNativeQuery("truncate table ugs_growing_rule_parameter CASCADE").executeUpdate();
            em.createNativeQuery("truncate table ugs_growing_rule_plugin CASCADE").executeUpdate();
            em.createNativeQuery("truncate table ugs_growing_type CASCADE").executeUpdate();
            em.createNativeQuery("truncate table ugs_observer CASCADE").executeUpdate();
            em.createNativeQuery("truncate table ugs_point_type CASCADE").executeUpdate();
            em.createNativeQuery("truncate table ugs_process_log CASCADE").executeUpdate();
            em.createNativeQuery("truncate table ugs_user_grade_config CASCADE").executeUpdate();
            em.createNativeQuery("truncate table ugs_user_grade_config_list CASCADE").executeUpdate();
            em.createNativeQuery("truncate table ugs_user_info CASCADE").executeUpdate();
            em.createNativeQuery("truncate table ugs_user_point CASCADE").executeUpdate();
            em.createNativeQuery("truncate table ugs_user_point_history CASCADE").executeUpdate();
            tran.commit();
        }catch (Exception e){
            tran.rollback();
            org.slf4j.LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }finally {
            em.close();
        }

//        //规则参数
//        List<UgsGrowingRuleParameter> parameterAll = parameterDao.findAll();
//        parameterDao.removeList(parameterAll);
//        //规则
//        List<UgsGrowingRuleList> items = ruleListDao.findAll();
//        ruleListDao.removeList(items);
//        //积分
//        List<UgsUserPoint> points = pointDao.findAll();
//        pointDao.removeList(points);
//        //插件
//        List<UgsGrowingRulePlugin> pluginAll = pluginDao.findAll();
//        pluginDao.removeList(pluginAll);
//        //分组
//        groupDao.removeList(groupDao.findAll());
//        //类型
//        List<UgsGrowingType> typeAll = typeDao.findAll();
//        typeDao.removeList(typeAll);
    }

    /**
     * 类型
     */
    private void a() {
        UgsGrowingType type = new UgsGrowingType();
        type.setTypeCode("B_USER");
        type.setStatus((short) 1);
        type.setTypeName("B端");
        typeDao.persist(type);
    }

    /**
     * 分组
     */
    private void b() {
        List<UgsGrowingType> types = typeDao.findAll();
        UgsGrowingRuleGroup group = new UgsGrowingRuleGroup();
        group.setUgsGrowingType(types.get(0));
        group.setRuleGroupName("普通分组");
        group.setStatus(UgsGrowingRuleGroup.STATUS_NORMAL);
        groupDao.persist(group);

        UgsGrowingActiveGroup activeGroup = new UgsGrowingActiveGroup();
        activeGroup.setTypeCode("B_USER");
        activeGroup.setUgsGrowingRuleGroup(group);
        activeGroupDao.persist(activeGroup);
    }

    /**
     * 插件
     */
    private void c() {
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
    private void d() {

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

        persist1("city", "地区", "10", group, fixPlugin);
        persist1("company", "公司", "10", group, fixPlugin);
        persist1("identification", "身份证", "30", group, fixPlugin);
        persist1("practicing_certificate", "执业证", "30", group, fixPlugin);
        persist1("avatar", "头像", "10", group, fixPlugin);
        persist1("professional_photo", "半身照", "30", group, fixPlugin);
        persist1("self_introduction", "个人介绍", "10", group, fixPlugin);

        persist2("authentication", "诚信通年限", "150", "year", "", group, limitPlugin);
        persist2("forward_reading", "微阅读转发", "1", "day", "2", group, limitPlugin);
        persist2("forward_plan", "V计划", "1", "day", "3", group, limitPlugin);
        persist2("answer_advisory", "问吧问答", "", "day", "2", group, limitPlugin);
    }

    private void e() {
        UgsGrowingType growingType = new UgsGrowingType();
        growingType.setTypeCode("B_USER");
        UgsPointType pointType = new UgsPointType();
        pointType.setPointTypeCode("experience_points");
        pointType.setPointTypeName("经验值");
        pointType.setUgsGrowingType(growingType);
        pointDao.persist(pointType);
    }

    private void persist1(String code, String name, String value, UgsGrowingRuleGroup group, UgsGrowingRulePlugin plugin) {
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
        parameter.setParamValue(value);
        parameterDao.persist(parameter);
    }

    private void persist2(String code, String name, String value, String unit, String vipValue, UgsGrowingRuleGroup group, UgsGrowingRulePlugin plugin) {
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
        if (!value.equals("")) {
            id.setRuleListId(item.getRuleListId());
            id.setParamName("experience_points");
            parameter.setId(id);
            parameter.setParamValue(value);
            parameterDao.persist(parameter);

        }

        if (!unit.equals("")) {
            parameter = new UgsGrowingRuleParameter();
            id = new UgsGrowingRuleParameterId();
            id.setRuleListId(item.getRuleListId());
            id.setParamName("time_unit");
            parameter.setId(id);
            parameter.setParamValue(unit);
            parameterDao.persist(parameter);
        }

        if (!vipValue.equals("")) {
            parameter = new UgsGrowingRuleParameter();
            id = new UgsGrowingRuleParameterId();
            id.setRuleListId(item.getRuleListId());
            id.setParamName("vip_experience_points");
            parameter.setId(id);
            parameter.setParamValue(vipValue);
            parameterDao.persist(parameter);
        }

    }

}
