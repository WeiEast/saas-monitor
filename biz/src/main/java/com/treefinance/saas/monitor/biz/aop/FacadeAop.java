package com.treefinance.saas.monitor.biz.aop;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by yh-treefinance on 2017/6/5.
 */
@Aspect
@Component
public class FacadeAop {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("execution(* com.treefinance.saas.monitor.biz.facade..*.*(..))")
    public void service() {
    }

    @Around("service()")
    public Object arround(ProceedingJoinPoint point) {
        if (logger.isDebugEnabled()) {
            logger.debug("{}.{} request {}", point.getTarget().getClass(), point.getSignature().getName(), JSON.toJSONString(point.getArgs()));
        }
        Object result = null;
        try {
            result = point.proceed();
        } catch (ParamCheckerException e) {
            logger.info("{} 参数校验失败：args={} ", point.getTarget().getClass(), JSON.toJSONString(point.getArgs()));
            result = MonitorResultBuilder.build(e.getMessage());
        } catch (Throwable e) {
            logger.error(point.getTarget().getClass() + " 处理请求失败：args=" + JSON.toJSONString(point.getArgs()), e);
            result = MonitorResultBuilder.build("服务器内部异常：errorMsg=" + e.getMessage());
        }
        return result;
    }
}
