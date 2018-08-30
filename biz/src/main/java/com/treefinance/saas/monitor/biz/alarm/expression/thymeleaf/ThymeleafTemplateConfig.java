package com.treefinance.saas.monitor.biz.alarm.expression.thymeleaf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.dialect.SpringStandardDialect;
import org.thymeleaf.templateresolver.TemplateResolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;

/**
 * Created by yh-treefinance on 2018/8/2.
 */
@Configuration
public class ThymeleafTemplateConfig {

    @Bean(name = "springTemplateEngine")
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();

        IDialect iDialect = new SpringStandardDialect();
        springTemplateEngine.setDialect(iDialect);


        TemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setCacheable(false);
        templateResolver.setTemplateMode("XHTML");
        templateResolver.setPrefix(" ");
        templateResolver.setSuffix(" ");
        springTemplateEngine.setTemplateResolver(templateResolver);
        return springTemplateEngine;
    }


    /**
     * 基于字符串的资源加载
     */
    public static class StringResourceResolver implements IResourceResolver {

        @Override
        public String getName() {
            return "STRING_BASE";
        }

        @Override
        public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters, String resourceName) {
            return new ByteArrayInputStream(resourceName.getBytes());
        }
    }


    /**
     * String base TemplateResolver
     */
    public static class StringTemplateResolver extends TemplateResolver {

        public StringTemplateResolver() {
            super();
            super.setResourceResolver(new StringResourceResolver());
        }
    }

}
