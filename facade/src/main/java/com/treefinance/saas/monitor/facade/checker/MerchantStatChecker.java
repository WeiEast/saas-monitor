package com.treefinance.saas.monitor.facade.checker;

import com.treefinance.saas.monitor.facade.domain.request.*;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 商户统计checker
 * Created by yh-treefinance on 2017/6/5.
 */
public class MerchantStatChecker {
    /**
     * 基础参数校验
     *
     * @param request
     */
    public static void checkBase(MerchantStatBaseRequest request) {
        if (request == null) {
            throw new ParamCheckerException("请求参数不能空");
        }
        if (StringUtils.isEmpty(request.getAppId())) {
            throw new ParamCheckerException("请求参数appId不能为空");
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
    public static void checkAccessRequest(MerchantStatAccessRequest request) {
        checkBase(request);
        Byte dataType = request.getDataType();
        if (request.getDataType() == null) {
            throw new ParamCheckerException("请求参数dataType不能为空");
        }
        if (dataType < 0) {
            throw new ParamCheckerException("请求参数dataType非法");
        }
    }

    public static void checkAllAccessRequest(MerchantStatAccessRequest request) {
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
        Byte dataType = request.getDataType();
        if (request.getDataType() == null) {
            throw new ParamCheckerException("请求参数dataType不能为空");
        }
        if (dataType < 0) {
            throw new ParamCheckerException("请求参数dataType非法");
        }
    }


    public static void checkAllDayAccessRequest(MerchantStatDayAccessRequest request) {
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
        Byte dataType = request.getDataType();
        if (request.getDataType() == null) {
            throw new ParamCheckerException("请求参数dataType不能为空");
        }
        if (dataType < 0) {
            throw new ParamCheckerException("请求参数dataType非法");
        }
    }


    /**
     * 校验商户统计参数
     *
     * @param request
     */
    public static void checkDayAccessRequest(MerchantStatDayAccessRequest request) {
        checkBase(request);
        Byte dataType = request.getDataType();
        if (request.getDataType() == null) {
            throw new ParamCheckerException("请求参数dataType不能为空");
        }
        if (dataType < 0) {
            throw new ParamCheckerException("请求参数dataType非法");
        }
    }

    /**
     * 校验商户统计参数
     *
     * @param request
     */
    public static void checkBankRequest(MerchantStatBankRequest request) {
        checkBase(request);
    }

    /**
     * 电商参数校验
     *
     * @param request
     */
    public static void checkEcommerceRequest(MerchantStatEcommerceRequest request) {
        checkBase(request);
    }

    /**
     * 邮箱参数校验
     *
     * @param request
     */
    public static void checkMailRequest(MerchantStatMailRequest request) {
        checkBase(request);
    }

    /**
     * 运营商参数校验
     *
     * @param request
     */
    public static void checkOperaterRequest(MerchantStatOperaterRequest request) {
        checkBase(request);
    }

    /**
     * 查询任务失败取消环节参数校验
     *
     * @param request
     */
    public static void checkErrorDayStatRequest(SaasErrorStepDayStatRequest request) {
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
        Byte dataType = request.getDataType();
        if (request.getDataType() == null) {
            throw new ParamCheckerException("请求参数dataType不能为空");
        }
        if (dataType < 0) {
            throw new ParamCheckerException("请求参数dataType非法");
        }
    }
}
