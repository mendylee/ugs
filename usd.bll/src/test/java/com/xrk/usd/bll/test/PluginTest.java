package com.xrk.usd.bll.test;

import com.xrk.usd.bll.plugin.impl.LimitPointGrowingPlugin;
import com.xrk.usd.bll.plugin.proxy.GrowingService;
import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.dao.DaoBase;
import org.junit.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class PluginTest {
    @BeforeClass
    public static void SetUpClass() {
        //测试类运行前的代码，只运行一次
    }

    @AfterClass
    public static void tearDownAfterClass() {
        System.out.println("Tear down After class");
    }

    @Before
    public void SetUp() {
        //在每个测试方法运行之前运行
    }

    @After
    public void tearDown() throws Exception {
        //在每个测试方法后运行
        System.out.println("Tear down");
    }

    @Test
    public void testBll() {

//        String persistenceUnitName = "HibernatePersistenceUnit";
//        EntityManagerFactory factory = Persistence.createEntityManagerFactory(persistenceUnitName);
//        DaoBase.setFactory(factory);
//
        DalService.Init();
        LimitPointGrowingPlugin growingPlugin = new LimitPointGrowingPlugin();
        growingPlugin.setGrowingService(new GrowingService());
        Map<String,Object> params = new HashMap<>();
        params.put("experience_points","1");
        params.put("time_unit","day");
        params.put("vip_experience_points","200");

        //"forward_plan"、"answer_advisory"、"authentication"
//        growingPlugin.invoke(372941, "B_USER", "answer_advisory","每天的赠送分数",params);
    }
}
