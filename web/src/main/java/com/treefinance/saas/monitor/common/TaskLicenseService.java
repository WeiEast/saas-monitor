package com.treefinance.saas.monitor.common;

import com.treefinance.saas.monitor.common.domain.dto.AppLicenseDTO;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.web.auth.exception.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by luoyihua on 2017/5/10.
 */
@Service
public class TaskLicenseService {
    private static final Logger logger = LoggerFactory.getLogger(TaskLicenseService.class);

    @Autowired
    private AppLicenseService appLicenseService;
    @Autowired
    private AppBizLicenseService appBizLicenseService;

    public void verifyCreateTask(String appId, EBizType bizType) throws ForbiddenException {
        boolean hasCreateTaskAuth = false;
        AppLicenseDTO appLicense = appLicenseService.selectOneByAppId(appId);
        if (appLicense != null) {
            List<AppBizLicense> appBizLicenseList = appBizLicenseService.getByLicenseId(appLicense.getId());
            if (!appBizLicenseList.isEmpty()) {
                for (AppBizLicense appBizLicense : appBizLicenseList) {
                    if (appBizLicense.getBizType() == bizType.getCode()) {
                        hasCreateTaskAuth = true;
                        break;
                    }
                }
            }
        }
        if (!hasCreateTaskAuth) {
            throw new ForbiddenException("Can not find license for app '" + appId + "' bizType '" + bizType + "'.");
        }
    }
}
