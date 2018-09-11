package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.facade.domain.ro.WholeConversionResult;

/**
 * @author chengtong
 * @date 18/9/11 16:48
 */
public interface AllBizTypeStatAccessService {

    WholeConversionResult calcConversionResult(EBizType bizType);


}
