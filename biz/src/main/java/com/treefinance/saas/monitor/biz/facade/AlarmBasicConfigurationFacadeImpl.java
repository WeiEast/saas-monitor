package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.saas.monitor.biz.service.AsAlarmMsgService;
import com.treefinance.saas.monitor.biz.service.AsAlarmService;
import com.treefinance.saas.monitor.biz.service.AsAlarmTriggerRecordService;
import com.treefinance.saas.monitor.biz.service.AsAlarmTriggerService;
import com.treefinance.saas.monitor.common.utils.BeanUtils;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.AsAlarm;
import com.treefinance.saas.monitor.dao.entity.AsAlarmMsg;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTrigger;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecord;
import com.treefinance.saas.monitor.facade.domain.request.AlarmExcuteLogRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationDetailRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.AlarmExecuteLogRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmBasicConfigurationDetailRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.autoalarm.AlarmBasicConfigurationFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/19上午11:30
 */
@Service("alarmBasicConfigurationFacade")
public class AlarmBasicConfigurationFacadeImpl implements AlarmBasicConfigurationFacade {
    private static final Logger logger = LoggerFactory.getLogger(AlarmBasicConfigurationFacade.class);

    @Autowired
    private AsAlarmService asAlarmService;

    @Autowired
    private AsAlarmMsgService asAlarmMsgService;

    @Autowired
    private AsAlarmTriggerService asAlarmTriggerService;

    @Autowired
    private AsAlarmTriggerRecordService asAlarmTriggerRecordService;

    @Override
    public MonitorResult<Void> addOrUpdate(AlarmBasicConfigurationDetailRequest request) {
        if (request == null) {
            throw new ParamCheckerException("request不能为null");
        }
        asAlarmService.addOrUpdate(request);
        return MonitorResultBuilder.build();
    }

    @Override
    public MonitorResult<AsAlarmBasicConfigurationDetailRO> queryAlarmConfigurationById(Long id) {
        if (id == null) {
            throw new ParamCheckerException("id不能为null");
        }
        AsAlarmBasicConfigurationDetailRO result = asAlarmService.queryAsAlarmBasicConfigurationDetailById(id);
        return MonitorResultBuilder.build(result);
    }

    @Override
    public MonitorResult<List<AlarmExecuteLogRO>> queryAlaramExecuteLogList(AlarmExcuteLogRequest alarmExcuteLogRequest) {
        if (alarmExcuteLogRequest.getId() == null) {
            return new MonitorResult<>("执行日志查询预警配置id不能为空");
        }
        logger.info("执行日志查询条件为{}", alarmExcuteLogRequest.toString());
        AsAlarm asAlarm = asAlarmService.getAsAlarmByPrimaryKey(alarmExcuteLogRequest.getId());

        AlarmExcuteLogRequest alarmExcuteLogRequest1 = new AlarmExcuteLogRequest();
        alarmExcuteLogRequest1.setId(alarmExcuteLogRequest.getId());

        List<AsAlarmTriggerRecord> totalasAlarmTriggerRecordList = asAlarmTriggerRecordService.queryAsAlarmTriggerRecord(alarmExcuteLogRequest1);
        List<AsAlarmTriggerRecord> asAlarmTriggerRecordList = asAlarmTriggerRecordService.queryAsAlarmTriggerRecordPagination(alarmExcuteLogRequest);


        List<AlarmExecuteLogRO> list = new ArrayList<>();
        for (AsAlarmTriggerRecord asAlarmTriggerRecord : asAlarmTriggerRecordList) {
            AlarmExecuteLogRO alarmExecuteLogRO = new AlarmExecuteLogRO();
            AsAlarmTrigger asAlarmTrigger = asAlarmTriggerService.getAsAlarmTriggerByPrimaryKey(asAlarmTriggerRecord.getConditionId());
            BeanUtils.copyProperties(asAlarmTrigger, alarmExecuteLogRO);
            BeanUtils.copyProperties(asAlarm, alarmExecuteLogRO);
            BeanUtils.copyProperties(asAlarmTriggerRecord, alarmExecuteLogRO);
            alarmExecuteLogRO.setConditionName(asAlarmTrigger.getName());
            list.add(alarmExecuteLogRO);
        }

        return new MonitorResult<>(alarmExcuteLogRequest, list, totalasAlarmTriggerRecordList.size());
    }

    @Override
    public MonitorResult<List<AsAlarmRO>> queryAlarmConfigurationList(AlarmBasicConfigurationRequest request) {
        logger.info("分页查询预警配置：{}", request);

        List<AsAlarm> list = asAlarmService.queryPagingList(request);

        if (list.isEmpty()) {
            return MonitorResultBuilder.pageResult(request, new ArrayList<>(), 0);
        }

        List<Long> ids = list.stream().map(AsAlarm::getId).collect(Collectors.toList());
        List<AsAlarmMsg> asAlarmMsgs = asAlarmMsgService.queryMsgInIdList(ids);

        List<AsAlarmRO> returnList = DataConverterUtils.convert(list, AsAlarmRO.class);
        Map<Long, AsAlarmMsg> map = asAlarmMsgs.stream().collect(Collectors.toMap(AsAlarmMsg::getAlarmId,
                asAlarmMsg -> asAlarmMsg));
        for (AsAlarmRO asAlarmRO : returnList) {
            Long id = asAlarmRO.getId();
            AsAlarmMsg asAlarmMsg = map.get(id);
            if (asAlarmMsg != null) {
                asAlarmRO.setTitleTemplate(asAlarmMsg.getTitleTemplate());
                asAlarmRO.setBodyTemplate(asAlarmMsg.getBodyTemplate());
            }
        }
        return MonitorResultBuilder.pageResult(request, returnList, returnList.size());
    }

}
