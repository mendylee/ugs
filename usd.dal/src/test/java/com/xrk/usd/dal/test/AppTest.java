package com.xrk.usd.dal.test;

import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.dao.GrowingTypeDao;
import com.xrk.usd.dal.dao.UgsObserverDao;
import com.xrk.usd.dal.dao.UserGradeConfigListDao;
import com.xrk.usd.dal.dao.UserPointDao;
import com.xrk.usd.dal.entity.UgsGrowingType;
import com.xrk.usd.dal.entity.UgsUserGradeConfigList;
import com.xrk.usd.dal.entity.UgsUserPoint;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @BeforeClass
    public static void SetUpClass() {
        DalService.Init("HibernatePersistenceUnit");
    }

    @AfterClass
    public static void tearDownAfterClass() {
        DalService.destory();
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
    public void test() {
        UserPointDao dao = DalService.getDao(UserPointDao.class);
        List<Long> uIds = new ArrayList<>();
        uIds.add(Long.valueOf(372941));
        uIds.add(Long.valueOf(1755440));
        List<UgsUserPoint> list = dao.findBy(uIds, "B_USER");
        Assert.assertNotNull(list);
        System.out.println(list.size());

        for(UgsUserPoint userPoint: list)
        {
            System.out.println(userPoint.getUgsPointType().getPointTypeId());
        }
    }

    @Test
    public void testDal() {
        //测试方法
        System.out.println("Test testDal");
//        EntityManagerFactory factory = Persistence.createEntityManagerFactory("HibernatePersistenceUnit");
        GrowingTypeDao dao = new GrowingTypeDao();

        UgsGrowingType entity = new UgsGrowingType("C_TYPE", "C端用户", (short) 1);
        Assert.assertTrue(dao.persist(entity));

        entity.setTypeCode("B_TYPE");
        entity.setTypeName("B端用户体系");
        Assert.assertTrue(dao.persist(entity));

        entity.setTypeName("再次修改后的B端用户体系哦！");
        entity.setStatus((short) 0);
        Assert.assertEquals(true, dao.merge(entity));

        List<UgsGrowingType> list = dao.findAll();
        Assert.assertEquals(2, list.size());

        entity = dao.findById("C_TYPE");
        Assert.assertNotNull(entity);

        UgsGrowingType type = new UgsGrowingType();
        type.setTypeCode("C_TYPE");
//        Assert.assertEquals(true, dao.remove(type));
        Assert.assertTrue(dao.remove(type));

        entity = dao.findById("C_TYPE");
        Assert.assertNull(entity);

//        Assert.assertEquals(1, entity);
    }
}
