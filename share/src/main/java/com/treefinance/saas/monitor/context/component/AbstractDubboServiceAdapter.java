package com.treefinance.saas.monitor.context.component;

import com.alibaba.fastjson.JSON;
import com.treefinance.b2b.saas.context.adapter.AbstractDomainObjectAdapter;
import com.treefinance.saas.monitor.exception.RpcServiceException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jerry
 * @date 2018/11/23 19:15
 */
public abstract class AbstractDubboServiceAdapter extends AbstractDomainObjectAdapter {

    protected <Response> void validateResponse(Response result, RpcActionEnum action, Object... args) {
        if (result == null) {
            throw new RpcServiceException("Bad response! - action: " + action + appendArgs(args));
        }
    }

    protected String appendArgs(Object... args) {
        return ArrayUtils.isNotEmpty(args) ? ", args: " + JSON.toJSONString(args) : StringUtils.EMPTY;
    }

}
