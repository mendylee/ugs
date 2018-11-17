package com.xrk.usd.bll.test;

import com.xrk.usd.bll.component.impl.UserPointComponent;
import com.xrk.usd.common.exception.BusinessException;
import com.xrk.usd.dal.DalService;
import org.junit.*;

public class BusinessTest {
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
    public void t(){
        DalService.Init();
        UserPointComponent pointComponent = new UserPointComponent();
        try {
//            pointComponent.findUserPoint(372941,"B_USER");
//            pointComponent.findUserPointHistory(372941,"B_USER",50,1,"2015-09-17","","company");
            pointComponent.countUserPointHistory(372941, "B_USER","2015-09-17","","company");
//            pointComponent.findSysGrade("B_USER");
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }
}
