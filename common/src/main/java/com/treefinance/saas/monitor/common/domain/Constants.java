package com.treefinance.saas.monitor.common.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.treefinance.b2b.saas.conf.PropertiesConfiguration;


public final class Constants {

    private static final Logger logger = LoggerFactory.getLogger(Constants.class);

    public static final String PREFIX_KEY = "saas-gateway:";
    public static final int REDIS_KEY_TIMEOUT = PropertiesConfiguration
        .getInstance().getInt("platform.redisKey.timeout", 600);

    public static final String SAAS_ENV;
    public static final String SAAS_ENV_VALUE;
    public static final String SAAS_ENV_PRE_PRODUCT = "preproduct";

    static {
        String saasEnv = "";
        String saasEnvValue = "1";
        try {
            saasEnv = System.getProperty("saas.env");
            if (saasEnv != null && !"".equals(saasEnv) && "preproduct".equals(saasEnv)) {
                saasEnvValue = "2";
            }
        } catch (Exception var3) {
            logger.error("load system properties saas.env failed ", var3);
        }

        SAAS_ENV = saasEnv;
        SAAS_ENV_VALUE = saasEnvValue;
        logger.debug("system properties saas.env=" + saasEnv + " saasEnvValue=" + saasEnvValue);
    }

    private Constants() {
    }
}