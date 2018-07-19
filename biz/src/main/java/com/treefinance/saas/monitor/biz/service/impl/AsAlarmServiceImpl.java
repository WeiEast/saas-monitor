package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.monitor.biz.service.AsAlarmService;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.dao.mapper.*;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/19上午10:57
 */
@Service
public class AsAlarmServiceImpl implements AsAlarmService {

    @Autowired
    AsAlarmMapper asAlarmMapper;
    @Autowired
    AsAlarmConstantMapper asAlarmConstantMapper;
    @Autowired
    AsAlarmQueryMapper asAlarmQueryMapper;
    @Autowired
    AsAlarmVariableMapper asAlarmVariableMapper;
    @Autowired
    AsAlarmNotifyMapper asAlarmNotifyMapper;
    @Autowired
    AsAlarmMsgMapper asAlarmMsgMapper;
    @Autowired
    AsAlarmTriggerMapper asAlarmTriggerMapper;

    @Override
    public AsAlarm getAsAlarmByPrimaryKey(long id) {
        return asAlarmMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<AsAlarm> selectPaginationByExample(AsAlarmCriteria criteria) {
        return asAlarmMapper.selectPaginationByExample(criteria);
    }

    @Override
    public List<AsAlarm> queryPagingList(AlarmBasicConfigurationRequest request) {
        AsAlarmCriteria criteria = new AsAlarmCriteria();
        criteria.createCriteria().andNameLike(request.getName()).andRunEnvEqualTo(request.getRunEnv());
        criteria.setOffset(request.getOffset());
        criteria.setLimit(request.getPageSize());
        return asAlarmMapper.selectPaginationByExample(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdate(AlarmBasicConfigurationDetailRequest request) {
        //预警配置表
        AsAlarmInfoRequest asAlarmInfoRequest = request.getAsAlarmInfoRequest();
        AsAlarm asAlarm = DataConverterUtils.convert(asAlarmInfoRequest, AsAlarm.class);
        if (asAlarm.getId() == null) {
            asAlarm.setId(UidGenerator.getId());
        }
        asAlarmMapper.insertOrUpdateBySelective(asAlarm);

        //预警常量表
        List<AsAlarmConstantInfoRequest> asAlarmConstantInfoRequestList = request.getAsAlarmConstantInfoRequestList();
        for (AsAlarmConstantInfoRequest asAlarmConstantInfoRequest : asAlarmConstantInfoRequestList) {
            AsAlarmConstant asAlarmConstant = DataConverterUtils.convert(asAlarmConstantInfoRequest, AsAlarmConstant.class);
            if (asAlarmConstant.getId() == null) {
                asAlarmConstant.setId(UidGenerator.getId());
            }
            asAlarmConstantMapper.insertOrUpdateBySelective(asAlarmConstant);
        }

        //预警数据查询表
        List<AsAlarmQueryInfoRequest> asAlarmQueryInfoRequestList = request.getAsAlarmQueryInfoRequestList();
        for (AsAlarmQueryInfoRequest asAlarmQueryInfoRequest : asAlarmQueryInfoRequestList) {
            AsAlarmQuery asAlarmQuery = DataConverterUtils.convert(asAlarmQueryInfoRequest, AsAlarmQuery.class);
            if (asAlarmQuery.getId() == null) {
                asAlarmQuery.setId(UidGenerator.getId());
            }
            asAlarmQueryMapper.insertOrUpdateBySelective(asAlarmQuery);
        }

        //预警变量表
        List<AsAlarmVariableInfoRequest> asAlarmVariableInfoRequestList = request.getAsAlarmVariableInfoRequestList();
        for (AsAlarmVariableInfoRequest asAlarmVariableInfoRequest : asAlarmVariableInfoRequestList) {
            AsAlarmVariable asAlarmVariable = DataConverterUtils.convert(asAlarmVariableInfoRequest, AsAlarmVariable.class);
            if (asAlarmVariable.getId() == null) {
                asAlarmVariable.setId(UidGenerator.getId());
            }
            asAlarmVariableMapper.insertOrUpdateBySelective(asAlarmVariable);
        }

        //预警通知表
        List<AsAlarmNotifyInfoRequest> asAlarmNotifyInfoRequestList = request.getAsAlarmNotifyInfoRequestList();
        for (AsAlarmNotifyInfoRequest asAlarmNotifyInfoRequest : asAlarmNotifyInfoRequestList) {
            AsAlarmNotify asAlarmNotify = DataConverterUtils.convert(asAlarmNotifyInfoRequest, AsAlarmNotify.class);
            if (asAlarmNotify.getId() == null) {
                asAlarmNotify.setId(UidGenerator.getId());
            }
            asAlarmNotifyMapper.insertOrUpdateBySelective(asAlarmNotify);
        }

        //预警消息模板表
        AsAlarmMsgInfoRequest asAlarmMsgInfoRequest = request.getAsAlarmMsgInfoRequest();
        AsAlarmMsg asAlarmMsg = DataConverterUtils.convert(asAlarmMsgInfoRequest, AsAlarmMsg.class);
        if (asAlarmMsg.getId() == null) {
            asAlarmMsg.setId(UidGenerator.getId());
        }
        asAlarmMsgMapper.insertOrUpdateBySelective(asAlarmMsg);

        //预警触发条件表
        List<AsAlarmTriggerInfoRequest> asAlarmTriggerInfoRequestList = request.getAsAlarmTriggerInfoRequestList();
        for (AsAlarmTriggerInfoRequest asAlarmTriggerInfoRequest : asAlarmTriggerInfoRequestList) {
            AsAlarmTrigger asAlarmTrigger = DataConverterUtils.convert(asAlarmTriggerInfoRequest, AsAlarmTrigger.class);
            if (asAlarmTrigger.getId() == null) {
                asAlarmTrigger.setId(UidGenerator.getId());
            }
            asAlarmTriggerMapper.insertOrUpdateBySelective(asAlarmTrigger);
        }


    }
}
