package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.AsAlarmMsgService;
import com.treefinance.saas.monitor.dao.entity.AsAlarmMsg;
import com.treefinance.saas.monitor.dao.entity.AsAlarmMsgCriteria;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmMsgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/19上午10:57
 */
@Service("asAlarmMsgService")
public class AsAlarmMsgServiceImpl implements AsAlarmMsgService {

    @Autowired
    AsAlarmMsgMapper asAlarmMsgMapper;

    @Override
    public List<AsAlarmMsg> selectByExample(AsAlarmMsgCriteria alarmMsgCriteria) {
        return asAlarmMsgMapper.selectByExample(alarmMsgCriteria);
    }


}
