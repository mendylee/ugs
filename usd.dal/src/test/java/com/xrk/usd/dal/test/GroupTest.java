package com.xrk.usd.dal.test;

import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.dao.GrowingActiveGroupDao;
import com.xrk.usd.dal.entity.*;
import org.junit.*;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class GroupTest {
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
    public void testGetGrowingMap() {
        GrowingActiveGroupDao growingActiveGroupDao = new GrowingActiveGroupDao();
        Map<UgsGrowingType, Set<UgsGrowingRuleList>> result = growingActiveGroupDao.getGrowingMap();

        result.forEach((x, y) -> System.out.println(String.format("growing: %s, count: %d", x.getTypeCode(), y.size())));
    }
}
