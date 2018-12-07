package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.monitor.biz.alarm.service.AlaramJobService;
import com.treefinance.saas.monitor.biz.service.AsAlarmService;
import com.treefinance.saas.monitor.context.component.AbstractService;
import com.treefinance.saas.monitor.dao.entity.AsAlarm;
import com.treefinance.saas.monitor.dao.entity.AsAlarmConstant;
import com.treefinance.saas.monitor.dao.entity.AsAlarmConstantCriteria;
import com.treefinance.saas.monitor.dao.entity.AsAlarmCriteria;
import com.treefinance.saas.monitor.dao.entity.AsAlarmMsg;
import com.treefinance.saas.monitor.dao.entity.AsAlarmMsgCriteria;
import com.treefinance.saas.monitor.dao.entity.AsAlarmNotify;
import com.treefinance.saas.monitor.dao.entity.AsAlarmNotifyCriteria;
import com.treefinance.saas.monitor.dao.entity.AsAlarmQuery;
import com.treefinance.saas.monitor.dao.entity.AsAlarmQueryCriteria;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTrigger;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerCriteria;
import com.treefinance.saas.monitor.dao.entity.AsAlarmVariable;
import com.treefinance.saas.monitor.dao.entity.AsAlarmVariableCriteria;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmConstantMapper;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmMapper;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmMsgMapper;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmNotifyMapper;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmQueryMapper;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmTriggerMapper;
import com.treefinance.saas.monitor.dao.mapper.AsAlarmVariableMapper;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationDetailRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AlarmBasicConfigurationRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AsAlarmConstantInfoRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AsAlarmInfoRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AsAlarmMsgInfoRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AsAlarmNotifyInfoRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AsAlarmQueryInfoRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AsAlarmTriggerInfoRequest;
import com.treefinance.saas.monitor.facade.domain.request.autoalarm.AsAlarmVariableInfoRequest;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmBasicConfigurationDetailRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmConstantRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmMsgRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmNotifyRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmQueryRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmTriggerRO;
import com.treefinance.saas.monitor.facade.domain.ro.autoalarm.AsAlarmVariableRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/19上午10:57
 */
@Service
public class AsAlarmServiceImpl extends AbstractService implements AsAlarmService {

    @Autowired
    private AsAlarmMapper asAlarmMapper;
    @Autowired
    private AsAlarmConstantMapper asAlarmConstantMapper;
    @Autowired
    private AsAlarmQueryMapper asAlarmQueryMapper;
    @Autowired
    private AsAlarmVariableMapper asAlarmVariableMapper;
    @Autowired
    private AsAlarmNotifyMapper asAlarmNotifyMapper;
    @Autowired
    private AsAlarmMsgMapper asAlarmMsgMapper;
    @Autowired
    private AsAlarmTriggerMapper asAlarmTriggerMapper;

    @Autowired
    private AlaramJobService alaramJobService;

    @Resource
    private UidService uidService;

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
        criteria.setOrderByClause("alarmSwitch desc,createTime desc");
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
        AsAlarmCriteria.Criteria innerCriteria = asAlarmCriteria.createCriteria();
        innerCriteria.andNameEqualTo(asAlarmInfoRequest.getName());
        if (asAlarmInfoRequest.getId() != null) {
            innerCriteria.andIdNotEqualTo(asAlarmInfoRequest.getId());
        }
        List<AsAlarm> asAlarmList = asAlarmMapper.selectByExample(asAlarmCriteria);
        if (!CollectionUtils.isEmpty(asAlarmList)) {
            throw new ParamCheckerException("预警名称已被使用~");

        }

        AsAlarm asAlarm = convert(asAlarmInfoRequest, AsAlarm.class);
        if (asAlarm.getId() == null) {
            asAlarm.setId(uidService.getId());
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
            AsAlarmConstant asAlarmConstant = convert(asAlarmConstantInfoRequest, AsAlarmConstant.class);
            if (asAlarmConstant.getId() == null) {
                asAlarmConstant.setId(uidService.getId());
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
            AsAlarmQuery asAlarmQuery = convert(asAlarmQueryInfoRequest, AsAlarmQuery.class);
            if (asAlarmQuery.getId() == null) {
                asAlarmQuery.setId(uidService.getId());
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
            AsAlarmVariable asAlarmVariable = convert(asAlarmVariableInfoRequest, AsAlarmVariable.class);
            if (asAlarmVariable.getId() == null) {
                asAlarmVariable.setId(uidService.getId());
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
            AsAlarmNotify asAlarmNotify = convert(asAlarmNotifyInfoRequest, AsAlarmNotify.class);
            if (asAlarmNotify.getId() == null) {
                asAlarmNotify.setId(uidService.getId());
            }
            asAlarmNotify.setAlarmId(asAlarm.getId());
            asAlarmNotify.setCreateTime(new Date());
            asAlarmNotifyMapper.insertSelective(asAlarmNotify);
        }

        //预警消息模板表
        List<AsAlarmMsgInfoRequest> asAlarmMsgInfoRequestList = request.getAsAlarmMsgInfoRequestList();
        AsAlarmMsgCriteria asAlarmMsgCriteria = new AsAlarmMsgCriteria();
        asAlarmMsgCriteria.createCriteria().andAlarmIdEqualTo(asAlarm.getId());
        asAlarmMsgMapper.deleteByExample(asAlarmMsgCriteria);
        for (AsAlarmMsgInfoRequest asAlarmMsgInfoRequest : asAlarmMsgInfoRequestList) {
            AsAlarmMsg asAlarmMsg = convert(asAlarmMsgInfoRequest, AsAlarmMsg.class);
            if (asAlarmMsg.getId() == null) {
                asAlarmMsg.setId(uidService.getId());
            }
            asAlarmMsg.setAlarmId(asAlarm.getId());
            asAlarmMsgMapper.insertSelective(asAlarmMsg);
        }

        //预警触发条件表
        List<AsAlarmTriggerInfoRequest> asAlarmTriggerInfoRequestList = request.getAsAlarmTriggerInfoRequestList();
        AsAlarmTriggerCriteria asAlarmTriggerCriteria = new AsAlarmTriggerCriteria();
        asAlarmTriggerCriteria.createCriteria().andAlarmIdEqualTo(asAlarm.getId());
        asAlarmTriggerMapper.deleteByExample(asAlarmTriggerCriteria);
        for (AsAlarmTriggerInfoRequest asAlarmTriggerInfoRequest : asAlarmTriggerInfoRequestList) {
            AsAlarmTrigger asAlarmTrigger = convert(asAlarmTriggerInfoRequest, AsAlarmTrigger.class);
            if (asAlarmTrigger.getId() == null) {
                asAlarmTrigger.setId(uidService.getId());
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
        AsAlarmRO asAlarmRO = convert(asAlarm, AsAlarmRO.class);
        result.setAsAlarmRO(asAlarmRO);

        //预警常量表
        AsAlarmConstantCriteria asAlarmConstantCriteria = new AsAlarmConstantCriteria();
        asAlarmConstantCriteria.setOrderByClause("constIndex asc,id asc");
        asAlarmConstantCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmConstant> asAlarmConstantList = asAlarmConstantMapper.selectByExample(asAlarmConstantCriteria);
        List<AsAlarmConstantRO> asAlarmConstantROList = convert(asAlarmConstantList, AsAlarmConstantRO.class);
        result.setAsAlarmConstantROList(asAlarmConstantROList);

        //预警数据查询表
        AsAlarmQueryCriteria asAlarmQueryCriteria = new AsAlarmQueryCriteria();
        asAlarmQueryCriteria.setOrderByClause("queryIndex asc,id asc");
        asAlarmQueryCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmQuery> asAlarmQueryList = asAlarmQueryMapper.selectByExample(asAlarmQueryCriteria);
        List<AsAlarmQueryRO> asAlarmQueryROList = convert(asAlarmQueryList, AsAlarmQueryRO.class);
        result.setAsAlarmQueryROList(asAlarmQueryROList);

        //预警变量表
        AsAlarmVariableCriteria asAlarmVariableCriteria = new AsAlarmVariableCriteria();
        asAlarmVariableCriteria.setOrderByClause("varIndex asc,id asc");
        asAlarmVariableCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmVariable> asAlarmVariableList = asAlarmVariableMapper.selectByExample(asAlarmVariableCriteria);
        List<AsAlarmVariableRO> asAlarmVariableROList = convert(asAlarmVariableList, AsAlarmVariableRO.class);
        result.setAsAlarmVariableROList(asAlarmVariableROList);

        //预警通知表
        AsAlarmNotifyCriteria asAlarmNotifyCriteria = new AsAlarmNotifyCriteria();
        asAlarmNotifyCriteria.setOrderByClause("id asc");
        asAlarmNotifyCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmNotify> asAlarmNotifyList = asAlarmNotifyMapper.selectByExample(asAlarmNotifyCriteria);
        List<AsAlarmNotifyRO> asAlarmNotifyROList = convert(asAlarmNotifyList, AsAlarmNotifyRO.class);
        result.setAsAlarmNotifyROList(asAlarmNotifyROList);

        //预警消息模板表
        AsAlarmMsgCriteria asAlarmMsgCriteria = new AsAlarmMsgCriteria();
        asAlarmMsgCriteria.setOrderByClause("id asc");
        asAlarmMsgCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmMsg> asAlarmMsgList = asAlarmMsgMapper.selectByExample(asAlarmMsgCriteria);
        List<AsAlarmMsgRO> asAlarmMsgROList = convert(asAlarmMsgList, AsAlarmMsgRO.class);
        result.setAsAlarmMsgROList(asAlarmMsgROList);

        //预警触发条件表
        AsAlarmTriggerCriteria asAlarmTriggerCriteria = new AsAlarmTriggerCriteria();
        asAlarmTriggerCriteria.setOrderByClause("triggerIndex asc,id asc");
        asAlarmTriggerCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmTrigger> asAlarmTriggerList = asAlarmTriggerMapper.selectByExample(asAlarmTriggerCriteria);
        List<AsAlarmTriggerRO> asAlarmTriggerROList = convert(asAlarmTriggerList, AsAlarmTriggerRO.class);
        result.setAsAlarmTriggerROList(asAlarmTriggerROList);

        return result;
    }

    @Override
    public void updateAlarmSwitch(Long alarmId) {
        AsAlarm asAlarm = asAlarmMapper.selectByPrimaryKey(alarmId);

        if (("off").equals(asAlarm.getAlarmSwitch())) {
            asAlarm.setAlarmSwitch("on");
            asAlarmMapper.updateByPrimaryKeySelective(asAlarm);
            alaramJobService.startJob(alarmId);
        } else {
            asAlarm.setAlarmSwitch("off");
            asAlarmMapper.updateByPrimaryKeySelective(asAlarm);
            alaramJobService.stopJob(alarmId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long alarmId) {
        asAlarmMapper.deleteByPrimaryKey(alarmId);

        AsAlarmConstantCriteria constantCriteria = new AsAlarmConstantCriteria();
        constantCriteria.createCriteria().andAlarmIdEqualTo(alarmId);
        asAlarmConstantMapper.deleteByExample(constantCriteria);

        AsAlarmQueryCriteria queryCriteria = new AsAlarmQueryCriteria();
        queryCriteria.createCriteria().andAlarmIdEqualTo(alarmId);
        asAlarmQueryMapper.deleteByExample(queryCriteria);

        AsAlarmVariableCriteria variableCriteria = new AsAlarmVariableCriteria();
        variableCriteria.createCriteria().andAlarmIdEqualTo(alarmId);
        asAlarmVariableMapper.deleteByExample(variableCriteria);

        AsAlarmNotifyCriteria asAlarmNotifyCriteria = new AsAlarmNotifyCriteria();
        asAlarmNotifyCriteria.createCriteria().andAlarmIdEqualTo(alarmId);
        asAlarmNotifyMapper.deleteByExample(asAlarmNotifyCriteria);

        AsAlarmTriggerCriteria asAlarmTriggerCriteria = new AsAlarmTriggerCriteria();
        asAlarmTriggerCriteria.createCriteria().andAlarmIdEqualTo(alarmId);
        asAlarmTriggerMapper.deleteByExample(asAlarmTriggerCriteria);

        AsAlarmMsgCriteria asAlarmMsgCriteria = new AsAlarmMsgCriteria();
        asAlarmMsgCriteria.createCriteria().andAlarmIdEqualTo(alarmId);
        asAlarmMsgMapper.deleteByExample(asAlarmMsgCriteria);

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyAlarm(Long id) {

        Date now = new Date();

        //预警配置表
        AsAlarm asAlarm = asAlarmMapper.selectByPrimaryKey(id);

        asAlarm.setId(uidService.getId());
        asAlarm.setName(asAlarm.getName()+"-副本");
        asAlarm.setAlarmSwitch("off");
        asAlarm.setCreateTime(now);
        asAlarm.setLastUpdateTime(now);

        //预警常量表
        AsAlarmConstantCriteria asAlarmConstantCriteria = new AsAlarmConstantCriteria();
        asAlarmConstantCriteria.setOrderByClause("constIndex asc,id asc");
        asAlarmConstantCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmConstant> asAlarmConstantList = asAlarmConstantMapper.selectByExample(asAlarmConstantCriteria);

        for(AsAlarmConstant constant : asAlarmConstantList){
            constant.setId(uidService.getId());
            constant.setAlarmId(asAlarm.getId());
            constant.setCreateTime(now);
            constant.setLastUpdateTime(now);
        }

        //预警数据查询表
        AsAlarmQueryCriteria asAlarmQueryCriteria = new AsAlarmQueryCriteria();
        asAlarmQueryCriteria.setOrderByClause("queryIndex asc,id asc");
        asAlarmQueryCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmQuery> asAlarmQueryList = asAlarmQueryMapper.selectByExample(asAlarmQueryCriteria);

        for (AsAlarmQuery asAlarmQuery:asAlarmQueryList){
            asAlarmQuery.setAlarmId(asAlarm.getId());
            asAlarmQuery.setId(uidService.getId());
            asAlarmQuery.setCreateTime(now);
            asAlarmQuery.setLastUpdateTime(now);
        }

        //预警变量表
        AsAlarmVariableCriteria asAlarmVariableCriteria = new AsAlarmVariableCriteria();
        asAlarmVariableCriteria.setOrderByClause("varIndex asc,id asc");
        asAlarmVariableCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmVariable> asAlarmVariableList = asAlarmVariableMapper.selectByExample(asAlarmVariableCriteria);

        for (AsAlarmVariable variable:asAlarmVariableList){
            variable.setAlarmId(asAlarm.getId());
            variable.setId(uidService.getId());
            variable.setCreateTime(now);
            variable.setLastUpdateTime(now);
        }



        //预警通知表
        AsAlarmNotifyCriteria asAlarmNotifyCriteria = new AsAlarmNotifyCriteria();
        asAlarmNotifyCriteria.setOrderByClause("id asc");
        asAlarmNotifyCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmNotify> asAlarmNotifyList = asAlarmNotifyMapper.selectByExample(asAlarmNotifyCriteria);

        for (AsAlarmNotify variable:asAlarmNotifyList){
            variable.setAlarmId(asAlarm.getId());
            variable.setId(uidService.getId());
            variable.setCreateTime(now);
            variable.setLastUpdateTime(now);
        }


        //预警消息模板表
        AsAlarmMsgCriteria asAlarmMsgCriteria = new AsAlarmMsgCriteria();
        asAlarmMsgCriteria.setOrderByClause("id asc");
        asAlarmMsgCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmMsg> asAlarmMsgList = asAlarmMsgMapper.selectByExample(asAlarmMsgCriteria);
        for (AsAlarmMsg variable:asAlarmMsgList){
            variable.setAlarmId(asAlarm.getId());
            variable.setId(uidService.getId());
            variable.setCreateTime(now);
            variable.setLastUpdateTime(now);
        }
        //预警触发条件表
        AsAlarmTriggerCriteria asAlarmTriggerCriteria = new AsAlarmTriggerCriteria();
        asAlarmTriggerCriteria.setOrderByClause("triggerIndex asc,id asc");
        asAlarmTriggerCriteria.createCriteria().andAlarmIdEqualTo(id);
        List<AsAlarmTrigger> asAlarmTriggerList = asAlarmTriggerMapper.selectByExample(asAlarmTriggerCriteria);

        for (AsAlarmTrigger variable:asAlarmTriggerList){
            variable.setAlarmId(asAlarm.getId());
            variable.setId(uidService.getId());
            variable.setCreateTime(now);
            variable.setLastUpdateTime(now);
        }

        asAlarmMapper.insert(asAlarm);
        asAlarmConstantMapper.batchInsert(asAlarmConstantList);
        asAlarmQueryMapper.batchInsert(asAlarmQueryList);
        asAlarmVariableMapper.batchInsert(asAlarmVariableList);
        asAlarmNotifyMapper.batchInsert(asAlarmNotifyList);
        asAlarmMsgMapper.batchInsert(asAlarmMsgList);
        asAlarmTriggerMapper.batchInsert(asAlarmTriggerList);

    }
}
