package com.treefinance.saas.monitor.util;

import com.treefinance.saas.monitor.common.domain.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by haojiahong on 2018/1/17.
 */
public final class SystemUtils {

    private static final Logger logger = LoggerFactory.getLogger(SystemUtils.class);

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
    public static boolean isPreProductContext() {
        String saasEnv = SAAS_ENV;
        logger.info("当前所处环境saas.env={}", saasEnv);
        return Constants.SAAS_ENV_PRE_PRODUCT.equals(saasEnv);
    }

    private SystemUtils() {
    }
}
