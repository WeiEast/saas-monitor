package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.*;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmBasicConfigurationDetailRO;
import com.treefinance.saas.monitor.facade.service.autoalarm.AlarmBasicConfigurationFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * @author chengtong
 * @date 18/7/19 14:37
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AlarmBasicConfigurationFacadeImplTest {

    @Autowired
    AlarmBasicConfigurationFacade facade;

    @Test
    public void addOrUpdate() {
        AlarmBasicConfigurationDetailRequest request = new AlarmBasicConfigurationDetailRequest();

        AsAlarmInfoRequest asAlarmInfoRequest = new AsAlarmInfoRequest();
        asAlarmInfoRequest.setId(204963666255179776L);
        asAlarmInfoRequest.setName("测试预警配置33356");
        asAlarmInfoRequest.setAlarmSwitch("on");
        asAlarmInfoRequest.setRunEnv((byte) 0);
        asAlarmInfoRequest.setRunInterval("0 0/5 * * * *");
        request.setAsAlarmInfoRequest(asAlarmInfoRequest);

        List<AsAlarmConstantInfoRequest> asAlarmConstantInfoRequestList = Lists.newArrayList();
        AsAlarmConstantInfoRequest asAlarmConstantInfoRequest = new AsAlarmConstantInfoRequest();
        asAlarmConstantInfoRequest.setId(204963667748352000L);
        asAlarmConstantInfoRequest.setName("业务类型");
        asAlarmConstantInfoRequest.setCode("bizType");
        asAlarmConstantInfoRequest.setValue("0");
        asAlarmConstantInfoRequest.setDescription("所有业务类型");
//        asAlarmConstantInfoRequestList.add(asAlarmConstantInfoRequest);
        request.setAsAlarmConstantInfoRequestList(asAlarmConstantInfoRequestList);

        List<AsAlarmQueryInfoRequest> asAlarmQueryInfoRequestList = Lists.newArrayList();
        AsAlarmQueryInfoRequest asAlarmQueryInfoRequest = new AsAlarmQueryInfoRequest();
        asAlarmQueryInfoRequest.setId(204963667970650112L);
        asAlarmQueryInfoRequest.setQuerySql("select sum(total) as total,sum(success) as success\n" +
                "from  ecommerce_all_stat_access \n" +
                "where appId = #appId \n" +
                "        and dataType= #dataType\n" +
                "        and dataTime <= #alarmTime\n" +
                "        and dataTime >= DATE_SUB(#alarmTime,INTERVAL #dataPointNum * #intervalTime MINUTE) ");
        asAlarmQueryInfoRequest.setResultCode("data");
        asAlarmQueryInfoRequest.setDescription("当前时段数据，自动根据选择模板生成");
        asAlarmQueryInfoRequestList.add(asAlarmQueryInfoRequest);
        request.setAsAlarmQueryInfoRequestList(asAlarmQueryInfoRequestList);

        List<AsAlarmVariableInfoRequest> asAlarmVariableInfoRequestList = Lists.newArrayList();
        AsAlarmVariableInfoRequest asAlarmVariableInfoRequest = new AsAlarmVariableInfoRequest();
        asAlarmVariableInfoRequest.setId(204963668138422272L);
        asAlarmVariableInfoRequest.setName("总转化率333");
        asAlarmVariableInfoRequest.setCode("totalRate");
        asAlarmVariableInfoRequest.setValue("100* (#data.callback / #data.total)");
        asAlarmVariableInfoRequest.setDescription("");
        asAlarmVariableInfoRequestList.add(asAlarmVariableInfoRequest);
        request.setAsAlarmVariableInfoRequestList(asAlarmVariableInfoRequestList);

        List<AsAlarmNotifyInfoRequest> asAlarmNotifyInfoRequestList = Lists.newArrayList();
        AsAlarmNotifyInfoRequest asAlarmNotifyInfoRequest = new AsAlarmNotifyInfoRequest();
        asAlarmNotifyInfoRequest.setId(204963668281028608L);
        asAlarmNotifyInfoRequest.setAlarmLevel("info");
        asAlarmNotifyInfoRequest.setEmailSwitch("on");
        asAlarmNotifyInfoRequest.setIvrSwitch("on");
        asAlarmNotifyInfoRequest.setSmsSwitch("on");
        asAlarmNotifyInfoRequest.setWechatSwitch("on");
        asAlarmNotifyInfoRequest.setReceiverType((byte) 0);
        asAlarmNotifyInfoRequestList.add(asAlarmNotifyInfoRequest);
        request.setAsAlarmNotifyInfoRequestList(asAlarmNotifyInfoRequestList);

        AsAlarmMsgInfoRequest asAlarmMsgInfoRequest = new AsAlarmMsgInfoRequest();
        asAlarmMsgInfoRequest.setId(204963668482355200L);
        asAlarmMsgInfoRequest.setBodyTemplate("【#{level}】您好33335，#{saasEnv}【所有环境】运营商监控(按人数统计)预警,在2018-07-09 03:00:00--2018-07-09 03:30:00时段数据存在问题，此时监控数据如下，请及时处理：\n" +
                "运营商     预警描述     当前指标值     指标阀值     偏离阀值程度\n" +
                "中国联通     回调成功率低于前7天平均值的70%     54.55% (6/11)     55.22% (9.5/12.0*0.7)     1.21%");
        asAlarmMsgInfoRequest.setTitleTemplate("【#level】【#level】【所有环境】【运营商-人数】发生总转化率预警");
        request.setAsAlarmMsgInfoRequest(asAlarmMsgInfoRequest);

        List<AsAlarmTriggerInfoRequest> asAlarmTriggerInfoRequestList = Lists.newArrayList();
        AsAlarmTriggerInfoRequest asAlarmTriggerInfoRequest = new AsAlarmTriggerInfoRequest();
        asAlarmTriggerInfoRequest.setId(204963668725624832L);
        asAlarmTriggerInfoRequest.setName("夜间总转化率预警");
        asAlarmTriggerInfoRequest.setStatus((byte) 0);
        asAlarmTriggerInfoRequest.setInfoTrigger("#hour(#alarmTime) >= 22 && #hour(#alarmTime) <= 7 && #totalRate <= #historyTotalRate * 0.9");
        asAlarmTriggerInfoRequest.setWarningTrigger("#hour(#alarmTime) >= 22 && #hour(#alarmTime) <= 7 && #totalRate <= #historyTotalRate * 0.75");
        asAlarmTriggerInfoRequest.setErrorTrigger("#hour(#alarmTime) >= 22 && #hour(#alarmTime) <= 7 && #totalRate <= #historyTotalRate * 0.6");
        asAlarmTriggerInfoRequestList.add(asAlarmTriggerInfoRequest);
        request.setAsAlarmTriggerInfoRequestList(asAlarmTriggerInfoRequestList);

        facade.addOrUpdate(request);

    }

    @Test
    public void queryAlarmConfigurationDetailById() {
        MonitorResult<AsAlarmBasicConfigurationDetailRO> result = facade.queryAlarmConfigurationDetailById(204917728920760320L);
    }

    @Test
    public void getCronComputeValue() {
        MonitorResult<Map<String, String>> result = facade.getCronComputeValue("0 0/2 * * * ?");
    }


    @Test
    public void queryAlaramExecuteLogByAlarmId() {
    }

    @Test
    public void queryAlarmConfigurationList() {
        AlarmBasicConfigurationRequest request = new AlarmBasicConfigurationRequest();
        request.setName("");
        request.setRunEnv((byte) 0);
        facade.queryAlarmConfigurationList(request);
    }
}