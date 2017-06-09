package com.treefinance.saas.monitor.facade.exception;

/**
 * 参数验证异常
 * Created by yh-treefinance on 2017/6/5.
 */
public class ParamCheckerException extends RuntimeException {

    public ParamCheckerException() {
    }

    public ParamCheckerException(String message) {
        super(message);
    }

    public ParamCheckerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamCheckerException(Throwable cause) {
        super(cause);
    }

    public ParamCheckerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
