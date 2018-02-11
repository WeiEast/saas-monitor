package com.treefinance.saas.monitor.common.domain;

import com.datatrees.common.conf.PropertiesConfiguration;

public interface Constants {

    String PREFIX_KEY = "saas-gateway:";

    String SAAS_ENV_PRE_PRODUCT = "preproduct";


    int REDIS_KEY_TIMEOUT = PropertiesConfiguration.getInstance().getInt("platform.redisKey.timeout", 600);

    String START_LOGIN = "START_LOGIN";
    String REFRESH_LOGIN_QR_CODE = "REFRESH_LOGIN_QR_CODE";
    String REFRESH_LOGIN_CODE = "REFRESH_LOGIN_CODE";
    String REFRESH_LOGIN_RANDOMPASSWORD = "REFRESH_LOGIN_RANDOMPASSWORD";
    String RETURN_PICTURE_CODE = "RETURN_PICTURE_CODE";
}