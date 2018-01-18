package com.treefinance.saas.monitor.common.utils;

import com.treefinance.saas.monitor.common.domain.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by haojiahong on 2018/1/17.
 */
public class MonitorUtils {

    private static final Logger logger = LoggerFactory.getLogger(MonitorUtils.class);

    public static final String SAAS_ENV;

    static {
        String saasEnv = "";
        try {
            saasEnv = System.getProperty("saas.env");
        } catch (Exception e) {
            logger.error("load system properties saas.env failed ", e);
        }

        SAAS_ENV = saasEnv;
    }


    /**
     * 所处环境是否是预发布环境
     *
     * @return true:是预发布;false:不是预发布环境
     */
    public static Boolean isPreProductContext() {
        String saasEnv = SAAS_ENV;
        logger.info("当前所处环境saas.env={}", saasEnv);
        if (StringUtils.isNotBlank(saasEnv)
                && StringUtils.equalsIgnoreCase(saasEnv, Constants.SAAS_ENV_PRE_PRODUCT)) {
            return true;
        }
        return false;
    }
}
