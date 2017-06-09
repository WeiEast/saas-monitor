package com.treefinance.saas.monitor.app;

import com.github.diamond.client.extend.annotation.AfterUpdate;
import com.github.diamond.client.extend.annotation.BeforeUpdate;
import com.github.diamond.client.extend.annotation.DAttribute;
import com.github.diamond.client.extend.annotation.DResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by luoyihua on 2017/5/4.
 */
@Component("applicationConfig")
@Scope
@DResource
public class ApplicationConfig {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    @DAttribute(key = "default.merchant.color.config")
    private String defaultMerchantColorConfig;

    @BeforeUpdate
    public void before(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " start...");
    }

    @AfterUpdate
    public void after(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " end...");
    }

    public String getDefaultMerchantColorConfig() {
        return defaultMerchantColorConfig;
    }

    public void setDefaultMerchantColorConfig(String defaultMerchantColorConfig) {
        this.defaultMerchantColorConfig = defaultMerchantColorConfig;
    }
}
