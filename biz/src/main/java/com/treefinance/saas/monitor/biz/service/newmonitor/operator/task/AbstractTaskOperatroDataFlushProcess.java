package com.treefinance.saas.monitor.biz.service.newmonitor.operator.task;

/**
 * Created by haojiahong on 2017/10/31.
 */
public abstract class AbstractTaskOperatroDataFlushProcess implements OperatorMonitorDataFlushService {

    @Override
    public void doDataFlushProcess() {
        doIntervalDataFlush();
        doDayDataFlush();
        if (isAlarm()) {
            doAlarm();
        }
    }

    protected abstract void doIntervalDataFlush();

    protected abstract void doDayDataFlush();

    protected abstract void doAlarm();

    protected boolean isAlarm() {
        return false;
    }

}
