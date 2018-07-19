package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.saas.monitor.biz.service.AsAlarmMsgService;
import com.treefinance.saas.monitor.biz.service.AsAlarmService;
import com.treefinance.saas.monitor.biz.service.AsAlarmTriggerRecordService;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.AsAlarm;
import com.treefinance.saas.monitor.dao.entity.AsAlarmCriteria;
import com.treefinance.saas.monitor.dao.entity.AsAlarmMsg;
import com.treefinance.saas.monitor.dao.entity.AsAlarmMsgCriteria;
import com.treefinance.saas.monitor.facade.domain.request.AlarmExcuteLogRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.AlarmExecuteLogRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmRO;
import com.treefinance.saas.monitor.facade.service.autoalarm.AlarmBasicConfigurationFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private AsAlarmService asalarmService;

    @Autowired
    private AsAlarmMsgService asAlarmMsgService;

    @Autowired
    private AsAlarmTriggerRecordService asAlarmTriggerRecordService;


    @Override
    public void add(){}

    @Override
    public void update(){}

    @Override
    public MonitorResult<AlarmExecuteLogRO> queryAlaramExecuteLogByAlarmId(AlarmExcuteLogRequest alarmExcuteLogRequest) {
        if(alarmExcuteLogRequest.getId()==null)
        {
            return new MonitorResult<>("执行日志查询id不能为空");
        }
        AsAlarm asAlarm = asalarmService.getAsAlarmByPrimaryKey(alarmExcuteLogRequest.getId());
//        AsAlarmTrigger asAlarmTrigger =
        return  null;

    }

    @Override
    public List<AsAlarmRO> queryAlarmConfigurationList(AlarmBasicConfigurationRequest request) {
        logger.info("分页查询预警配置：{}",request);

        AsAlarmCriteria criteria = new AsAlarmCriteria();
        criteria.createCriteria().andNameLike(request.getName()).andRunEnvEqualTo(request.getRunEnv());
        criteria.setOffset(request.getOffset());
        criteria.setLimit(request.getPageSize());
        List<AsAlarm> list = asalarmService.selectPaginationByExample(criteria);
        List<Long> ids = list.stream().map(AsAlarm::getId).collect(Collectors.toList());


        AsAlarmMsgCriteria alarmMsgCriteria = new AsAlarmMsgCriteria();
        alarmMsgCriteria.createCriteria().andIdIn(ids);
        List<AsAlarmMsg> asAlarmMsgs = asAlarmMsgService.selectByExample(alarmMsgCriteria);

        List<AsAlarmRO> returnList = DataConverterUtils.convert(list,AsAlarmRO.class);
        Map<Long,AsAlarmMsg> map = asAlarmMsgs.stream().collect(Collectors.toMap(AsAlarmMsg::getAlarmId,
                asAlarmMsg -> asAlarmMsg));
        for(AsAlarmRO asAlarmRO : returnList){

            Long id = asAlarmRO.getId();

            AsAlarmMsg asAlarmMsg = map.get(id);

            if(asAlarmMsg!=null){
                asAlarmRO.setTitleTemplate(asAlarmMsg.getTitleTemplate());
                asAlarmRO.setBodyTemplate(asAlarmMsg.getBodyTemplate());
            }

        }


        return returnList;
    }
}
