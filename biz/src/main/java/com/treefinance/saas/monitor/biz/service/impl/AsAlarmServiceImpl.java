package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.monitor.biz.service.AsAlarmService;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.*;
import com.treefinance.saas.monitor.dao.mapper.*;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.*;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.*;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
        if (!StringUtils.isEmpty(request.getName())) {
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
    public long countByCondition(AlarmBasicConfigurationRequest request) {
        AsAlarmCriteria criteria = new AsAlarmCriteria();
        AsAlarmCriteria.Criteria innerCriteria = criteria.createCriteria();
        if (!StringUtils.isEmpty(request.getName())) {
            innerCriteria.andNameLike(request.getName());
        }
        if (request.getRunEnv() != null) {
            innerCriteria.andRunEnvEqualTo(request.getRunEnv());
        }
        return asAlarmMapper.countByExample(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdate(AlarmBasicConfigurationDetailRequest request) {
        //预警配置表
        AsAlarmInfoRequest asAlarmInfoRequest = request.getAsAlarmInfoRequest();
        if (StringUtils.isEmpty(asAlarmInfoRequest.getName())) {
            throw new ParamCheckerException("预警名称不能为空~");
        }
        AsAlarmCriteria asAlarmCriteria = new AsAlarmCriteria();
        asAlarmCriteria.createCriteria().andNameEqualTo(asAlarmInfoRequest.getName());
        List<AsAlarm> asAlarmList = asAlarmMapper.selectByExample(asAlarmCriteria);
        if (asAlarmInfoRequest.getId() == null) {
            if (!CollectionUtils.isEmpty(asAlarmList)) {
                throw new ParamCheckerException("预警名称已被使用~");

            }
        } else {
            if (asAlarmList.size() > 1) {
                throw new ParamCheckerException("预警名称已被使用~");
            }
        }
        AsAlarm asAlarm = DataConverterUtils.convert(asAlarmInfoRequest, AsAlarm.class);
        if (asAlarm.getId() == null) {
            asAlarm.setId(UidGenerator.getId());
        }
        asAlarmMapper.insertOrUpdateBySelective(asAlarm);

        //预警常量表
        //添加或者更新前,先删除
        List<AsAlarmConstantInfoRequest> asAlarmConstantInfoRequestList = request.getAsAlarmConstantInfoRequestList();
        Map<String, List<AsAlarmConstantInfoRequest>> constMap
                = asAlarmConstantInfoRequestList.stream().collect(Collectors.groupingBy(AsAlarmConstantInfoRequest::getCode));
        checkUniqueCode(constMap, "预警常量");
        AsAlarmConstantCriteria asAlarmConstantCriteria = new AsAlarmConstantCriteria();
        asAlarmConstantCriteria.createCriteria().andAlarmIdEqualTo(asAlarm.getId());
        asAlarmConstantMapper.deleteByExample(asAlarmConstantCriteria);
        for (AsAlarmConstantInfoRequest asAlarmConstantInfoRequest : asAlarmConstantInfoRequestList) {
            AsAlarmConstant asAlarmConstant = DataConverterUtils.convert(asAlarmConstantInfoRequest, AsAlarmConstant.class);
            if (asAlarmConstant.getId() == null) {
                asAlarmConstant.setId(UidGenerator.getId());
            }
            asAlarmConstant.setAlarmId(asAlarm.getId());
            asAlarmConstantMapper.insertSelective(asAlarmConstant);
        }


        //预警数据查询表
        List<AsAlarmQueryInfoRequest> asAlarmQueryInfoRequestList = request.getAsAlarmQueryInfoRequestList();
        Map<String, List<AsAlarmQueryInfoRequest>> queryMap
                = asAlarmQueryInfoRequestList.stream().collect(Collectors.groupingBy(AsAlarmQueryInfoRequest::getResultCode));
        checkUniqueCode(queryMap, "预警数据查询");
        AsAlarmQueryCriteria asAlarmQueryCriteria = new AsAlarmQueryCriteria();
        asAlarmQueryCriteria.createCriteria().andAlarmIdEqualTo(asAlarm.getId());
        asAlarmQueryMapper.deleteByExample(asAlarmQueryCriteria);
        for (AsAlarmQueryInfoRequest asAlarmQueryInfoRequest : asAlarmQueryInfoRequestList) {
            AsAlarmQuery asAlarmQuery = DataConverterUtils.convert(asAlarmQueryInfoRequest, AsAlarmQuery.class);
            if (asAlarmQuery.getId() == null) {
                asAlarmQuery.setId(UidGenerator.getId());
            }
            asAlarmQuery.setAlarmId(asAlarm.getId());
            asAlarmQueryMapper.insertSelective(asAlarmQuery);
        }

        //预警变量表
        List<AsAlarmVariableInfoRequest> asAlarmVariableInfoRequestList = request.getAsAlarmVariableInfoRequestList();
        Map<String, List<AsAlarmVariableInfoRequest>> varMap
                = asAlarmVariableInfoRequestList.stream().collect(Collectors.groupingBy(AsAlarmVariableInfoRequest::getCode));
        checkUniqueCode(varMap, "预警变量");
        AsAlarmVariableCriteria asAlarmVariableCriteria = new AsAlarmVariableCriteria();
        asAlarmVariableCriteria.createCriteria().andAlarmIdEqualTo(asAlarm.getId());
        asAlarmVariableMapper.deleteByExample(asAlarmVariableCriteria);
        for (AsAlarmVariableInfoRequest asAlarmVariableInfoRequest : asAlarmVariableInfoRequestList) {
            AsAlarmVariable asAlarmVariable = DataConverterUtils.convert(asAlarmVariableInfoRequest, AsAlarmVariable.class);
            if (asAlarmVariable.getId() == null) {
                asAlarmVariable.setId(UidGenerator.getId());
            }
            asAlarmVariable.setAlarmId(asAlarm.getId());
            asAlarmVariableMapper.insertSelective(asAlarmVariable);
        }

        //预警通知表
        List<AsAlarmNotifyInfoRequest> asAlarmNotifyInfoRequestList = request.getAsAlarmNotifyInfoRequestList();
        AsAlarmNotifyCriteria asAlarmNotifyCriteria = new AsAlarmNotifyCriteria();
        asAlarmNotifyCriteria.createCriteria().andAlarmIdEqualTo(asAlarm.getId());
        asAlarmNotifyMapper.deleteByExample(asAlarmNotifyCriteria);
        for (AsAlarmNotifyInfoRequest asAlarmNotifyInfoRequest : asAlarmNotifyInfoRequestList) {
            AsAlarmNotify asAlarmNotify = DataConverterUtils.convert(asAlarmNotifyInfoRequest, AsAlarmNotify.class);
            if (asAlarmNotify.getId() == null) {
                asAlarmNotify.setId(UidGenerator.getId());
            }
            asAlarmNotify.setAlarmId(asAlarm.getId());
            asAlarmNotify.setCreateTime(new Date());
            asAlarmNotifyMapper.insertSelective(asAlarmNotify);
        }

        //预警消息模板表
        AsAlarmMsgCriteria asAlarmMsgCriteria = new AsAlarmMsgCriteria();
        asAlarmMsgCriteria.createCriteria().andAlarmIdEqualTo(asAlarm.getId());
        asAlarmMsgMapper.deleteByExample(asAlarmMsgCriteria);
        AsAlarmMsgInfoRequest asAlarmMsgInfoRequest = request.getAsAlarmMsgInfoRequest();
        AsAlarmMsg asAlarmMsg = DataConverterUtils.convert(asAlarmMsgInfoRequest, AsAlarmMsg.class);
        if (asAlarmMsg.getId() == null) {
            asAlarmMsg.setId(UidGenerator.getId());
        }
        asAlarmMsg.setAlarmId(asAlarm.getId());
        asAlarmMsgMapper.insertSelective(asAlarmMsg);

        //预警触发条件表
        List<AsAlarmTriggerInfoRequest> asAlarmTriggerInfoRequestList = request.getAsAlarmTriggerInfoRequestList();
        AsAlarmTriggerCriteria asAlarmTriggerCriteria = new AsAlarmTriggerCriteria();
        asAlarmTriggerCriteria.createCriteria().andAlarmIdEqualTo(asAlarm.getId());
        asAlarmTriggerMapper.deleteByExample(asAlarmTriggerCriteria);
        for (AsAlarmTriggerInfoRequest asAlarmTriggerInfoRequest : asAlarmTriggerInfoRequestList) {
            AsAlarmTrigger asAlarmTrigger = DataConverterUtils.convert(asAlarmTriggerInfoRequest, AsAlarmTrigger.class);
            if (asAlarmTrigger.getId() == null) {
                asAlarmTrigger.setId(UidGenerator.getId());
            }
            asAlarmTrigger.setAlarmId(asAlarm.getId());
            asAlarmTriggerMapper.insertSelective(asAlarmTrigger);
        }
    }

    private <T> void checkUniqueCode(Map<String, List<T>> map, String table) {
        for (Map.Entry<String, List<T>> entry : map.entrySet()) {
            if (entry.getValue().size() > 1) {
                throw new ParamCheckerException(table + ":" + entry.getKey() + "重复,请修改");
            }
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
        asAlarmConstantCriteria.setOrderByClause("constIndex asc,createTime asc");
        asAlarmConstantCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmConstant> asAlarmConstantList = asAlarmConstantMapper.selectByExample(asAlarmConstantCriteria);
        List<AsAlarmConstantRO> asAlarmConstantROList = DataConverterUtils.convert(asAlarmConstantList, AsAlarmConstantRO.class);
        result.setAsAlarmConstantROList(asAlarmConstantROList);

        //预警数据查询表
        AsAlarmQueryCriteria asAlarmQueryCriteria = new AsAlarmQueryCriteria();
        asAlarmQueryCriteria.setOrderByClause("queryIndex asc,createTime asc");
        asAlarmQueryCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmQuery> asAlarmQueryList = asAlarmQueryMapper.selectByExample(asAlarmQueryCriteria);
        List<AsAlarmQueryRO> asAlarmQueryROList = DataConverterUtils.convert(asAlarmQueryList, AsAlarmQueryRO.class);
        result.setAsAlarmQueryROList(asAlarmQueryROList);

        //预警变量表
        AsAlarmVariableCriteria asAlarmVariableCriteria = new AsAlarmVariableCriteria();
        asAlarmVariableCriteria.setOrderByClause("varIndex asc,createTime asc");
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
        asAlarmTriggerCriteria.setOrderByClause("triggerIndex asc,createTime asc");
        asAlarmTriggerCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmTrigger> asAlarmTriggerList = asAlarmTriggerMapper.selectByExample(asAlarmTriggerCriteria);
        List<AsAlarmTriggerRO> asAlarmTriggerROList = DataConverterUtils.convert(asAlarmTriggerList, AsAlarmTriggerRO.class);
        result.setAsAlarmTriggerROList(asAlarmTriggerROList);

        return result;
    }

    @Override
    public void updateAlarmSwitch(Long alarmId) {
        AsAlarm asAlarm = asAlarmMapper.selectByPrimaryKey(alarmId);

        if (("off").equals(asAlarm.getAlarmSwitch())) {
            asAlarm.setAlarmSwitch("on");
        } else {
            asAlarm.setAlarmSwitch("off");
        }
        asAlarmMapper.updateByPrimaryKeySelective(asAlarm);

    }
}
