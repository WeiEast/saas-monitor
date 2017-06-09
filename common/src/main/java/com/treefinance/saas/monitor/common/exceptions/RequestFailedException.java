package com.treefinance.saas.monitor.common.exceptions;

/**
 * 请求失败异常
 * Created by yh-treefinance on 2017/5/18.
 */
public class RequestFailedException extends RuntimeException {
    private static final long serialVersionUID = -290315695168000010L;

    public RequestFailedException() {
        super();
    }

    public RequestFailedException(String message) {
        super(message);
    }

    public RequestFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestFailedException(Throwable cause) {
        super(cause);
    }

    protected RequestFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}