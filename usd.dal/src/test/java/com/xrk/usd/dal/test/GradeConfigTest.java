package com.xrk.usd.dal.test;

import com.xrk.usd.dal.dao.UserGradeConfigDao;
import com.xrk.usd.dal.entity.UgsGrowingType;
import com.xrk.usd.dal.entity.UgsPointType;
import com.xrk.usd.dal.entity.UgsUserGradeConfig;
import org.junit.*;

import java.util.List;

public class GradeConfigTest {
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
        System.out.println("Tear down");
    }

    @Test
    public void t(){
        UserGradeConfigDao configDao = new UserGradeConfigDao();
        List<UgsUserGradeConfig> configs = configDao.findAll();
        for(UgsUserGradeConfig config:configs){
            UgsPointType pointType = config.getUgsPointType();
            UgsGrowingType growingType = config.getUgsGrowingType();
        }
    }
}
