package com.xrk.usd.bll.test;

import com.xrk.usd.bll.business.HistoryBusiness;
import com.xrk.usd.common.exception.BusinessException;
import org.junit.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HistoryTest {
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
    public void testHistoryCache() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        long uid = 372941;
        String growingTypeCode = "B_USER";
        int pageSize = 10;
        int pageNum = 1;
        String queryDateBegin = null;
        String queryDateEnd = null;
        String ruleCodes = "";

        queryDateEnd = sdf.format(new Date());
        System.out.println("测试今天:"+queryDateEnd);
        try {
            HistoryBusiness.getInstance().findBy(uid, growingTypeCode, pageSize, pageNum, queryDateBegin, queryDateEnd, ruleCodes);
        } catch (BusinessException e) {
            e.printStackTrace();
        }

        Calendar cld = Calendar.getInstance();
        cld.add(Calendar.DAY_OF_MONTH,1);
        queryDateEnd = sdf.format(cld.getTime());
        System.out.println("测试明天:"+queryDateEnd);
        try {
            HistoryBusiness.getInstance().findBy(uid, growingTypeCode, pageSize, pageNum, queryDateBegin, queryDateEnd, ruleCodes);
        } catch (BusinessException e) {
            e.printStackTrace();
        }

        cld = Calendar.getInstance();
        cld.add(Calendar.DAY_OF_MONTH, -1);
        queryDateEnd = sdf.format(cld.getTime());
        System.out.println("测试昨天:"+queryDateEnd);
        try {
            HistoryBusiness.getInstance().findBy(uid, growingTypeCode, pageSize, pageNum, queryDateBegin, queryDateEnd, ruleCodes);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }
}
