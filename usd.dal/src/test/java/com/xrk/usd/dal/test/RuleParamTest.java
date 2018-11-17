package com.xrk.usd.dal.test;

import org.junit.*;

public class RuleParamTest {
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
    public void t() {


    }
}
