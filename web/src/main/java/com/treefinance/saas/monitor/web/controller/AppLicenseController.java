package com.treefinance.saas.monitor.web.controller;

import com.treefinance.saas.monitor.common.domain.Result;
import com.treefinance.saas.monitor.common.domain.dto.AppLicenseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 授权管理Controller
 * Created by yh-treefinance on 2017/5/19.
 */
@RestController
@RequestMapping("/saas/backend/applicense")
public class AppLicenseController {
    private static final Logger logger = LoggerFactory.getLogger(AppLicenseController.class);

    @Autowired
    private AppLicenseService appLicenseService;

    @RequestMapping("/get/{appId}")
    public Result<AppLicenseDTO> getApplicenseByAppId(@PathVariable String appId){
        return new Result<>(appLicenseService.selectOneByAppId(appId));
    }

    @RequestMapping("/generate")
    public Result<String> generate(){
        return appLicenseService.generateAppLicense();
    }

    @RequestMapping("/generateBy/{appId}")
    public Result<Integer> generate(@PathVariable String appId){
        return appLicenseService.generateAppLicenseByAppId(appId);
    }
}
