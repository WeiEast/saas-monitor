package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.result.IvrCallBackResult;

/**
 * @author:guoguoyun
 * @date:Created in 2018/1/31下午3:54
 */
public interface IvrCallBackService {
    void dealIvrCallBackMessage(IvrCallBackResult ivrCallBackResult);
}
