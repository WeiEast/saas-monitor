package com.treefinance.saas.monitor.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.treefinance.saas.assistant.annotation.EnableMonitorListener;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ServletComponentScan("com.treefinance.saas.monitor.web")
@ImportResource("classpath:spring/applicationContext.xml")
@EnableMonitorListener
@EnableScheduling
@EnableAsync
public class SaasMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasMonitorApplication.class);
    }
}
