package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.monitor.biz.service.AsAlarmService;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.dao.mapper.*;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.*;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

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
        AsAlarmCriteria.Criteria innerCriteria = criteria.createCriteria();
        if (request.getName() != null) {
            innerCriteria.andNameLike(request.getName());
        }
        if (request.getRunEnv() != null) {
            innerCriteria.andRunEnvEqualTo(request.getRunEnv());
        }
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
        //添加或者更新前,先删除
        List<AsAlarmConstantInfoRequest> asAlarmConstantInfoRequestList = request.getAsAlarmConstantInfoRequestList();
        List<Long> constantDeleteIdList = asAlarmConstantInfoRequestList.stream()
                .filter(r -> Byte.valueOf("1").equals(r.getToDelete()))
                .map(AsAlarmConstantInfoRequest::getId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(constantDeleteIdList)) {
            AsAlarmConstantCriteria asAlarmConstantCriteria = new AsAlarmConstantCriteria();
            asAlarmConstantCriteria.createCriteria().andIdIn(constantDeleteIdList);
            asAlarmConstantMapper.deleteByExample(asAlarmConstantCriteria);
        }

        for (AsAlarmConstantInfoRequest asAlarmConstantInfoRequest : asAlarmConstantInfoRequestList) {
            if (Byte.valueOf("1").equals(asAlarmConstantInfoRequest.getToDelete())) {
                continue;
            }
            AsAlarmConstant asAlarmConstant = DataConverterUtils.convert(asAlarmConstantInfoRequest, AsAlarmConstant.class);
            if (asAlarmConstant.getId() == null) {
                asAlarmConstant.setId(UidGenerator.getId());
            }
            asAlarmConstant.setAlarmId(asAlarm.getId());
            asAlarmConstantMapper.insertOrUpdateBySelective(asAlarmConstant);
        }


        //预警数据查询表
        List<AsAlarmQueryInfoRequest> asAlarmQueryInfoRequestList = request.getAsAlarmQueryInfoRequestList();
        List<Long> queryDeleteIdList = asAlarmQueryInfoRequestList.stream()
                .filter(r -> Byte.valueOf("1").equals(r.getToDelete()))
                .map(AsAlarmQueryInfoRequest::getId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(queryDeleteIdList)) {
            AsAlarmQueryCriteria asAlarmQueryCriteria = new AsAlarmQueryCriteria();
            asAlarmQueryCriteria.createCriteria().andIdIn(queryDeleteIdList);
            asAlarmQueryMapper.deleteByExample(asAlarmQueryCriteria);
        }

        for (AsAlarmQueryInfoRequest asAlarmQueryInfoRequest : asAlarmQueryInfoRequestList) {
            if (Byte.valueOf("1").equals(asAlarmQueryInfoRequest.getToDelete())) {
                continue;
            }
            AsAlarmQuery asAlarmQuery = DataConverterUtils.convert(asAlarmQueryInfoRequest, AsAlarmQuery.class);
            if (asAlarmQuery.getId() == null) {
                asAlarmQuery.setId(UidGenerator.getId());
            }
            asAlarmQuery.setAlarmId(asAlarm.getId());
            asAlarmQueryMapper.insertOrUpdateBySelective(asAlarmQuery);
        }

        //预警变量表
        List<AsAlarmVariableInfoRequest> asAlarmVariableInfoRequestList = request.getAsAlarmVariableInfoRequestList();
        List<Long> variableDeleteIdList = asAlarmVariableInfoRequestList.stream()
                .filter(r -> Byte.valueOf("1").equals(r.getToDelete()))
                .map(AsAlarmVariableInfoRequest::getId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(variableDeleteIdList)) {
            AsAlarmVariableCriteria asAlarmVariableCriteria = new AsAlarmVariableCriteria();
            asAlarmVariableCriteria.createCriteria().andIdIn(variableDeleteIdList);
            asAlarmVariableMapper.deleteByExample(asAlarmVariableCriteria);
        }

        for (AsAlarmVariableInfoRequest asAlarmVariableInfoRequest : asAlarmVariableInfoRequestList) {
            if (Byte.valueOf("1").equals(asAlarmVariableInfoRequest.getToDelete())) {
                continue;
            }
            AsAlarmVariable asAlarmVariable = DataConverterUtils.convert(asAlarmVariableInfoRequest, AsAlarmVariable.class);
            if (asAlarmVariable.getId() == null) {
                asAlarmVariable.setId(UidGenerator.getId());
            }
            asAlarmVariable.setAlarmId(asAlarm.getId());
            asAlarmVariableMapper.insertOrUpdateBySelective(asAlarmVariable);
        }

        //预警通知表
        List<AsAlarmNotifyInfoRequest> asAlarmNotifyInfoRequestList = request.getAsAlarmNotifyInfoRequestList();
        List<Long> notifyDeleteIdList = asAlarmNotifyInfoRequestList.stream()
                .filter(r -> Byte.valueOf("1").equals(r.getToDelete()))
                .map(AsAlarmNotifyInfoRequest::getId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(notifyDeleteIdList)) {
            AsAlarmNotifyCriteria asAlarmNotifyCriteria = new AsAlarmNotifyCriteria();
            asAlarmNotifyCriteria.createCriteria().andIdIn(notifyDeleteIdList);
            asAlarmNotifyMapper.deleteByExample(asAlarmNotifyCriteria);
        }

        for (AsAlarmNotifyInfoRequest asAlarmNotifyInfoRequest : asAlarmNotifyInfoRequestList) {
            if (Byte.valueOf("1").equals(asAlarmNotifyInfoRequest.getToDelete())) {
                continue;
            }
            AsAlarmNotify asAlarmNotify = DataConverterUtils.convert(asAlarmNotifyInfoRequest, AsAlarmNotify.class);
            if (asAlarmNotify.getId() == null) {
                asAlarmNotify.setId(UidGenerator.getId());
            }
            asAlarmNotify.setAlarmId(asAlarm.getId());
            asAlarmNotifyMapper.insertOrUpdateBySelective(asAlarmNotify);
        }

        //预警消息模板表
        AsAlarmMsgInfoRequest asAlarmMsgInfoRequest = request.getAsAlarmMsgInfoRequest();
        AsAlarmMsg asAlarmMsg = DataConverterUtils.convert(asAlarmMsgInfoRequest, AsAlarmMsg.class);
        if (asAlarmMsg.getId() == null) {
            asAlarmMsg.setId(UidGenerator.getId());
        }
        asAlarmMsg.setAlarmId(asAlarm.getId());
        asAlarmMsgMapper.insertOrUpdateBySelective(asAlarmMsg);

        //预警触发条件表
        List<AsAlarmTriggerInfoRequest> asAlarmTriggerInfoRequestList = request.getAsAlarmTriggerInfoRequestList();
        List<Long> triggerDeleteIdList = asAlarmTriggerInfoRequestList.stream()
                .filter(r -> Byte.valueOf("1").equals(r.getToDelete()))
                .map(AsAlarmTriggerInfoRequest::getId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(triggerDeleteIdList)) {
            AsAlarmTriggerCriteria asAlarmTriggerCriteria = new AsAlarmTriggerCriteria();
            asAlarmTriggerCriteria.createCriteria().andIdIn(triggerDeleteIdList);
            asAlarmTriggerMapper.deleteByExample(asAlarmTriggerCriteria);
        }

        for (AsAlarmTriggerInfoRequest asAlarmTriggerInfoRequest : asAlarmTriggerInfoRequestList) {
            if (Byte.valueOf("1").equals(asAlarmTriggerInfoRequest.getToDelete())) {
                continue;
            }
            AsAlarmTrigger asAlarmTrigger = DataConverterUtils.convert(asAlarmTriggerInfoRequest, AsAlarmTrigger.class);
            if (asAlarmTrigger.getId() == null) {
                asAlarmTrigger.setId(UidGenerator.getId());
            }
            asAlarmTrigger.setAlarmId(asAlarm.getId());
            asAlarmTriggerMapper.insertOrUpdateBySelective(asAlarmTrigger);
        }
    }

    @Override
    public AsAlarmBasicConfigurationDetailRO queryAsAlarmBasicConfigurationDetailById(Long id) {
        AsAlarmBasicConfigurationDetailRO result = new AsAlarmBasicConfigurationDetailRO();

        //预警配置表
        AsAlarm asAlarm = asAlarmMapper.selectByPrimaryKey(id);
        AsAlarmRO asAlarmRO = DataConverterUtils.convert(asAlarm, AsAlarmRO.class);
        result.setAsAlarmRO(asAlarmRO);

        //预警常量表
        AsAlarmConstantCriteria asAlarmConstantCriteria = new AsAlarmConstantCriteria();
        asAlarmConstantCriteria.setOrderByClause("createTime asc");
        asAlarmConstantCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmConstant> asAlarmConstantList = asAlarmConstantMapper.selectByExample(asAlarmConstantCriteria);
        List<AsAlarmConstantRO> asAlarmConstantROList = DataConverterUtils.convert(asAlarmConstantList, AsAlarmConstantRO.class);
        result.setAsAlarmConstantROList(asAlarmConstantROList);

        //预警数据查询表
        AsAlarmQueryCriteria asAlarmQueryCriteria = new AsAlarmQueryCriteria();
        asAlarmQueryCriteria.setOrderByClause("createTime asc");
        asAlarmQueryCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmQuery> asAlarmQueryList = asAlarmQueryMapper.selectByExample(asAlarmQueryCriteria);
        List<AsAlarmQueryRO> asAlarmQueryROList = DataConverterUtils.convert(asAlarmQueryList, AsAlarmQueryRO.class);
        result.setAsAlarmQueryROList(asAlarmQueryROList);

        //预警变量表
        AsAlarmVariableCriteria asAlarmVariableCriteria = new AsAlarmVariableCriteria();
        asAlarmVariableCriteria.setOrderByClause("createTime asc");
        asAlarmVariableCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmVariable> asAlarmVariableList = asAlarmVariableMapper.selectByExample(asAlarmVariableCriteria);
        List<AsAlarmVariableRO> asAlarmVariableROList = DataConverterUtils.convert(asAlarmVariableList, AsAlarmVariableRO.class);
        result.setAsAlarmVariableROList(asAlarmVariableROList);

        //预警通知表
        AsAlarmNotifyCriteria asAlarmNotifyCriteria = new AsAlarmNotifyCriteria();
        asAlarmNotifyCriteria.setOrderByClause("createTime asc");
        asAlarmNotifyCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmNotify> asAlarmNotifyList = asAlarmNotifyMapper.selectByExample(asAlarmNotifyCriteria);
        List<AsAlarmNotifyRO> asAlarmNotifyROList = DataConverterUtils.convert(asAlarmNotifyList, AsAlarmNotifyRO.class);
        result.setAsAlarmNotifyROList(asAlarmNotifyROList);

        //预警消息模板表
        AsAlarmMsgCriteria asAlarmMsgCriteria = new AsAlarmMsgCriteria();
        asAlarmMsgCriteria.setOrderByClause("createTime asc");
        asAlarmMsgCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmMsg> asAlarmMsgList = asAlarmMsgMapper.selectByExample(asAlarmMsgCriteria);
        AsAlarmMsgRO asAlarmMsgRO = new AsAlarmMsgRO();
        if (!CollectionUtils.isEmpty(asAlarmMsgList)) {
            AsAlarmMsg asAlarmMsg = asAlarmMsgList.get(0);
            asAlarmMsgRO = DataConverterUtils.convert(asAlarmMsg, AsAlarmMsgRO.class);
        }
        result.setAsAlarmMsgRO(asAlarmMsgRO);

        //预警触发条件表
        AsAlarmTriggerCriteria asAlarmTriggerCriteria = new AsAlarmTriggerCriteria();
        asAlarmTriggerCriteria.setOrderByClause("createTime asc");
        asAlarmTriggerCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmTrigger> asAlarmTriggerList = asAlarmTriggerMapper.selectByExample(asAlarmTriggerCriteria);
        List<AsAlarmTriggerRO> asAlarmTriggerROList = DataConverterUtils.convert(asAlarmTriggerList, AsAlarmTriggerRO.class);
        result.setAsAlarmTriggerROList(asAlarmTriggerROList);

        return result;
    }
}
