package com.treefinance.saas.monitor.facade.service;

import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.enumeration.EOrderStatus;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import com.treefinance.saas.monitor.facade.domain.request.AlarmRecordDashBoardRequest;
import com.treefinance.saas.monitor.facade.domain.request.AlarmRecordRequest;
import com.treefinance.saas.monitor.facade.domain.request.AlarmRecordStatRequest;
import com.treefinance.saas.monitor.facade.domain.request.SaasWorkerRequest;
import com.treefinance.saas.monitor.facade.domain.request.UpdateWorkOrderRequest;
import com.treefinance.saas.monitor.facade.domain.request.WorkOrderLogRequest;
import com.treefinance.saas.monitor.facade.domain.request.WorkOrderRequest;
import com.treefinance.toolkit.util.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author chengtong
 * @date 18/5/30 14:44
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AlarmRecordFacadeTest extends BaseTest {

    @Autowired
    AlarmRecordFacade alarmRecordFacade;

    @Test
    public void queryAlarmRecord() {
        AlarmRecordRequest recordRequest = new AlarmRecordRequest();
        recordRequest.setAlarmType(EAlarmType.operator_alarm.name());
        recordRequest.setPageNumber(1);
        recordRequest.setPageSize(5);

        result = alarmRecordFacade.queryAlarmRecord(recordRequest);
    }

    @Test
    public void querySaasWorker() {
        result = alarmRecordFacade.querySaasWorker();
    }

    @Test
    public void querySaasWorkerPaginate() {

        result = alarmRecordFacade.querySaasWorkerPaginate(new SaasWorkerRequest());
    }

    @Test
    public void queryAlarmWorkerOrder() {
        WorkOrderRequest request = new WorkOrderRequest();
        result = alarmRecordFacade.queryAlarmWorkerOrder(request);
    }

    @Test
    public void updateWorkerOrder() {
        UpdateWorkOrderRequest request = new UpdateWorkOrderRequest();
        request.setId(192282788811010048L);
        request.setProcessorName("程通");
        request.setOpName("叶徽");
        result = alarmRecordFacade.updateWorkerOrderProcessor(request);
    }

    @Test
    public void updateWorkerOrderStatus() {
        UpdateWorkOrderRequest request = new UpdateWorkOrderRequest();
        request.setId(188973703973859328L);
        request.setStatus(EOrderStatus.PROCESSED.getCode());
        request.setRemark("已处理");
        result = alarmRecordFacade.updateWorkerOrderStatus(request);
    }

    @Test
    public void queryWorkOrderLog() {
        WorkOrderLogRequest request = new WorkOrderLogRequest();
        request.setOrderId(186791617926033408L);
        result = alarmRecordFacade.queryWorkOrderLog(request);
    }

    @Test
    public void queryAlarmStatistic() {
        AlarmRecordStatRequest recordStatRequest = new AlarmRecordStatRequest();

        result = alarmRecordFacade.queryAlarmStatistic(recordStatRequest);
    }

    @Test
    public void queryAlarmTypeList() {
        result = alarmRecordFacade.queryAlarmTypeList();
    }

    @Test
    public void queryAlarmRecordInDashBoard(){

        AlarmRecordDashBoardRequest request = new AlarmRecordDashBoardRequest();

        request.setBizType(EBizType.OPERATOR.getCode());
        request.setEndTime(DateUtils.parseDate("2018-09-20"));
        request.setStartTime(DateUtils.parseDate("2018-09-13"));
        request.setSaasEnv((byte)ESaasEnv.ALL.getValue());

        result = alarmRecordFacade.queryAlarmRecordInDashBoard(request);
    }





}