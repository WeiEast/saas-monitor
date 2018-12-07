package com.treefinance.saas.monitor.exception;

/**
 * @author Jerry
 * @date 2018/11/15 15:58
 */
public class UnexpectedException extends RuntimeException {

    public UnexpectedException(String message) {
        super(message);
    }

    public UnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedException(Throwable cause) {
        super(cause);
    }
}
