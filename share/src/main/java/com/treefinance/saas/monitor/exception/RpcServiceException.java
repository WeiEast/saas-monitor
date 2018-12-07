package com.treefinance.saas.monitor.exception;

/**
 * @author Jerry
 * @date 2018/11/15 15:58
 */
public class RpcServiceException extends UnexpectedException {

    public RpcServiceException(String message) {
        super(message);
    }

    public RpcServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcServiceException(Throwable cause) {
        super(cause);
    }
}
