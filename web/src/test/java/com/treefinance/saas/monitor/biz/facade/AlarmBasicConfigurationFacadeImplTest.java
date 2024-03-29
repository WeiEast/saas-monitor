package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandlerChain;
import com.treefinance.saas.monitor.dao.entity.AsAlarm;
import com.treefinance.saas.monitor.dao.entity.AsAlarmConstant;
import com.treefinance.saas.monitor.dao.entity.AsAlarmQuery;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTrigger;
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
    @Autowired
    AlarmHandlerChain alarmHandlerChain;


    @Test
    public void addOrUpdate() {
        AlarmBasicConfigurationDetailRequest request = new AlarmBasicConfigurationDetailRequest();

        AsAlarmInfoRequest asAlarmInfoRequest = new AsAlarmInfoRequest();
        asAlarmInfoRequest.setId(304963666255179776L);
        asAlarmInfoRequest.setName("测试预警配置33356");
        asAlarmInfoRequest.setAlarmSwitch("off");
        asAlarmInfoRequest.setRunEnv((byte) 0);
        asAlarmInfoRequest.setRunInterval("0 0/5 * * * *");
        request.setAsAlarmInfoRequest(asAlarmInfoRequest);

        List<AsAlarmConstantInfoRequest> asAlarmConstantInfoRequestList = Lists.newArrayList();
        AsAlarmConstantInfoRequest asAlarmConstantInfoRequest = new AsAlarmConstantInfoRequest();
        asAlarmConstantInfoRequest.setId(304963667748352000L);
        asAlarmConstantInfoRequest.setName("业务类型");
        asAlarmConstantInfoRequest.setCode("bizType");
        asAlarmConstantInfoRequest.setValue("0");
        asAlarmConstantInfoRequest.setDescription("所有业务类型");
//        asAlarmConstantInfoRequestList.add(asAlarmConstantInfoRequest);
        request.setAsAlarmConstantInfoRequestList(asAlarmConstantInfoRequestList);

        List<AsAlarmQueryInfoRequest> asAlarmQueryInfoRequestList = Lists.newArrayList();
        AsAlarmQueryInfoRequest asAlarmQueryInfoRequest = new AsAlarmQueryInfoRequest();
        asAlarmQueryInfoRequest.setId(304963667970650112L);
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
        asAlarmVariableInfoRequest.setId(304963668138422272L);
        asAlarmVariableInfoRequest.setName("总转化率333");
        asAlarmVariableInfoRequest.setCode("totalRate");
        asAlarmVariableInfoRequest.setValue("100* (#data.callback / #data.total)");
        asAlarmVariableInfoRequest.setDescription("");
        asAlarmVariableInfoRequestList.add(asAlarmVariableInfoRequest);
        request.setAsAlarmVariableInfoRequestList(asAlarmVariableInfoRequestList);

        List<AsAlarmNotifyInfoRequest> asAlarmNotifyInfoRequestList = Lists.newArrayList();
        AsAlarmNotifyInfoRequest asAlarmNotifyInfoRequest = new AsAlarmNotifyInfoRequest();
        asAlarmNotifyInfoRequest.setId(304963668281028608L);
        asAlarmNotifyInfoRequest.setAlarmLevel("info");
        asAlarmNotifyInfoRequest.setEmailSwitch("on");
        asAlarmNotifyInfoRequest.setIvrSwitch("on");
        asAlarmNotifyInfoRequest.setSmsSwitch("on");
        asAlarmNotifyInfoRequest.setWechatSwitch("on");
        asAlarmNotifyInfoRequest.setReceiverType((byte) 0);
        asAlarmNotifyInfoRequestList.add(asAlarmNotifyInfoRequest);
        request.setAsAlarmNotifyInfoRequestList(asAlarmNotifyInfoRequestList);

        List<AsAlarmMsgInfoRequest> asAlarmMsgInfoRequestList = Lists.newArrayList();
        AsAlarmMsgInfoRequest asAlarmMsgInfoRequest = new AsAlarmMsgInfoRequest();
        asAlarmMsgInfoRequest.setId(304963668482355200L);
        asAlarmMsgInfoRequest.setBodyTemplate("【#{level}】您好33335，#{saasEnv}【所有环境】运营商监控(按人数统计)预警,在2018-07-09 03:00:00--2018-07-09 03:30:00时段数据存在问题，此时监控数据如下，请及时处理：\n" +
                "运营商     预警描述     当前指标值     指标阀值     偏离阀值程度\n" +
                "中国联通     回调成功率低于前7天平均值的70%     54.55% (6/11)     55.22% (9.5/12.0*0.7)     1.21%");
        asAlarmMsgInfoRequest.setTitleTemplate("【#level】【#level】【所有环境】【运营商-人数】发生总转化率预警");
        asAlarmMsgInfoRequest.setMsgType((byte) 1);
        asAlarmMsgInfoRequest.setAnalysisType((byte) 1);
        asAlarmMsgInfoRequest.setNotifyChannel("wechat");
        asAlarmMsgInfoRequestList.add(asAlarmMsgInfoRequest);
        request.setAsAlarmMsgInfoRequestList(asAlarmMsgInfoRequestList);

        List<AsAlarmTriggerInfoRequest> asAlarmTriggerInfoRequestList = Lists.newArrayList();
        AsAlarmTriggerInfoRequest asAlarmTriggerInfoRequest = new AsAlarmTriggerInfoRequest();
        asAlarmTriggerInfoRequest.setId(304963668725624832L);
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
        MonitorResult<AsAlarmBasicConfigurationDetailRO> result = facade.queryAlarmConfigurationDetailById(304963666255179776L);
    }

    @Test
    public void getCronComputeValue() {
        MonitorResult<Map<String, String>> result = facade.getCronComputeValue("0 0/2 * * * ?");
    }

    @Test
    public void testAlarmHandlerChainHandle() {
        AlarmConfig alarmConfig = new AlarmConfig();
        AsAlarm asAlarm = new AsAlarm();
        asAlarm.setAlarmSwitch("on");
        asAlarm.setName("测试");
        asAlarm.setRunEnv((byte) 1);
        asAlarm.setRunInterval("0 0/2 * * * ?");
        asAlarm.setTimeInterval(0);
        alarmConfig.setAlarm(asAlarm);

        List<AsAlarmConstant> constantList = Lists.newArrayList();
        AsAlarmConstant constant1 = new AsAlarmConstant();
        constant1.setCode("intervalTime");
        constant1.setName("预警时间间隔");
        constant1.setConstIndex(1);
        constant1.setValue("2");
        constantList.add(constant1);

        AsAlarmConstant constant2 = new AsAlarmConstant();
        constant2.setCode("alarmTime");
        constant2.setName("当前预警时间");
        constant2.setConstIndex(2);
        constant2.setValue("2018-08-02 10:00:00");
        constantList.add(constant2);

        alarmConfig.setAlarmConstants(constantList);


        List<AsAlarmQuery> queryList = Lists.newArrayList();
        AsAlarmQuery asAlarmQuery = new AsAlarmQuery();
        asAlarmQuery.setResultCode("data");
        asAlarmQuery.setQueryIndex(1);
        asAlarmQuery.setQuerySql("select count(*) from as_alarm;");
        queryList.add(asAlarmQuery);
        alarmConfig.setAlarmQueries(queryList);

        List<AsAlarmTrigger> triggerList = Lists.newArrayList();
        AsAlarmTrigger trigger1 = new AsAlarmTrigger();
        trigger1.setName("夜间总转化率预警");
        trigger1.setStatus((byte) 0);
        trigger1.setTriggerIndex(1);
        trigger1.setInfoTrigger("true");
        trigger1.setWarningTrigger("true");
        triggerList.add(trigger1);
        alarmConfig.setAlarmTriggers(triggerList);

        AlarmContext alarmContext = alarmHandlerChain.handle(alarmConfig);
        System.out.println(JSON.toJSONString(alarmContext));


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

    @Test
    public void updateAlarmSwitch() {
        facade.updateAlarmSwitch((long) 1);
    }
}