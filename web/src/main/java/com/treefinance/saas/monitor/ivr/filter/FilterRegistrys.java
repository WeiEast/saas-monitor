package com.treefinance.saas.monitor.ivr.filter;

import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author:guoguoyun
 * @date:Created in 2018/1/31下午2:41
 */

@Configuration
public class FilterRegistrys {

    @Bean
    public FilterRegistrationBean ivrFilter(DiamondConfig diamondConfig) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new IvrFilter(diamondConfig));
        registration.setName("ivrFilter");
        registration.addUrlPatterns("/ivr/*");
        registration.setOrder(2);
        return registration;
    }
}
