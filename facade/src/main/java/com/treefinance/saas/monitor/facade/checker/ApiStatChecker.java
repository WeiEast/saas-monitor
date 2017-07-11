package com.treefinance.saas.monitor.facade.checker;

import com.treefinance.saas.monitor.facade.domain.request.ApiStatBaseRequest;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * api统计checker
 * Created by haojiahong on 2017/7/7.
 */
public class ApiStatChecker {
    /**
     * 基础参数校验
     *
     * @param request
     */
    public static void checkBase(ApiStatBaseRequest request) {
        if (request == null) {
            throw new ParamCheckerException("请求参数不能空");
        }

        Date startDate = request.getStartDate();
        Date endDate = request.getEndDate();
        if (startDate == null) {
            throw new ParamCheckerException("请求参数startDate不能为空");
        }
        if (endDate == null) {
            throw new ParamCheckerException("请求参数endDate不能为空");
        }
        if (startDate.after(endDate)) {
            throw new ParamCheckerException("请求参数startDate不能大于endDate");
        }
    }

    /**
     * 校验商户统计参数
     *
     * @param request
     */
    public static void checkDayAccessRequest(ApiStatBaseRequest request) {
        checkBase(request);
        if (StringUtils.isEmpty(request.getAppId())) {
            throw new ParamCheckerException("请求参数appId不能为空");
        }
    }

}
