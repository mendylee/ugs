package com.xrk.usd.dal.test;

import com.xrk.usd.dal.dao.GrowingTypeDao;
import com.xrk.usd.dal.dao.PointTypeDao;
import com.xrk.usd.dal.entity.UgsGrowingType;
import com.xrk.usd.dal.entity.UgsPointType;
import org.junit.*;

import java.util.List;

public class TypeTest {
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
    public void t(){
        PointTypeDao pointTypeDao = new PointTypeDao();

        UgsGrowingType growingType = new UgsGrowingType();
        growingType.setTypeCode("B_USER");
        List<UgsPointType> pointTypes = pointTypeDao.findBy(growingType);
        for(UgsPointType type:pointTypes){
            System.out.println(type.getUgsGrowingType().getTypeName());
        }
        pointTypeDao.findAll();

    }
}
