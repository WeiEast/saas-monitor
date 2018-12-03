package com.treefinance.saas.monitor.facade.service;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chengtong
 * @date 18/5/31 10:58
 */

public class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    protected MonitorResult result;

    @After
    public void logResult(){
        logger.info(JSON.toJSONString(result));
    }


}
