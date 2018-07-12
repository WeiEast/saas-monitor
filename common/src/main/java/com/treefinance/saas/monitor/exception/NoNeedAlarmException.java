package com.treefinance.saas.monitor.exception;

/**
 * @author chengtong
 * @date 18/7/10 10:47
 */
public class NoNeedAlarmException extends RuntimeException {

    private int code;

    private Object[] args;

    public NoNeedAlarmException(String message) {
        super(message);
    }

}
