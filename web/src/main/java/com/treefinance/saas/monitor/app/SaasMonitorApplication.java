package com.treefinance.saas.monitor.app;

import com.treefinance.saas.assistant.annotation.EnableMonitorListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ServletComponentScan("com.treefinance.saas.monitor.web")
@ImportResource("classpath:spring/applicationContext.xml")
@EnableMonitorListener
public class SaasMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasMonitorApplication.class);
    }
}
