package com.xrk.usd.bll.test;

import com.xrk.usd.bll.common.SysConfig;
import com.xrk.usd.bll.component.impl.UserPointComponent;
import com.xrk.usd.dal.DalService;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserTest {
    @BeforeClass
    public static void beforeClass() {
        SysConfig.Init(UserTest.class.getClassLoader().getResource("app.properties").getFile());
        DalService.Init("HibernatePersistenceUnit");
    }

    @AfterClass
    public static void afterClass() {
        DalService.destory();
    }

    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    @Test
    public void test() throws Exception {
        UserPointComponent pointComponent = new UserPointComponent();
        List<Long> uIds = new ArrayList<>();
        uIds.add(Long.valueOf(372941));
        uIds.add(Long.valueOf(74689));
        Map<Long, List<Map<String, Object>>> map = pointComponent.queryUserGrade("B_USER", uIds);
        Assert.assertEquals(map.size(), 2);
    }
}
