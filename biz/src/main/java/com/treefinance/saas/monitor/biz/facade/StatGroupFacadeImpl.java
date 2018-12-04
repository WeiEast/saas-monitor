package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatGroupService;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.StatGroup;
import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
import com.treefinance.saas.monitor.facade.domain.request.StatGroupRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.StatGroupRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.autostat.StatGroupFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/26下午8:16
 */
@Service("statGroupFacade")
public class StatGroupFacadeImpl implements StatGroupFacade {

    private static final Logger logger = LoggerFactory.getLogger(StatGroupFacade.class);

    @Autowired
    private StatGroupService statGroupService;
    @Autowired
    private UidService uidService;


    @Override
    public MonitorResult<List<StatGroupRO>> queryStatGroup(StatGroupRequest groupStatRequest) {
        if (groupStatRequest.getTemplateId() == null) {
            logger.error("根据模板ID返回分组列表，传入的模板ID为空");
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("返回模板分组列表，传入的请求参数为{}", JSON.toJSONString(groupStatRequest));
        List<StatGroup> statGroupList = statGroupService.queryStatGroup(groupStatRequest);
        if (StringUtils.isEmpty(statGroupList)) {
            logger.error("查不到统计分组");
            return MonitorResultBuilder.build(System.currentTimeMillis(), "查不到统计分组", null);
        }
        List<StatGroupRO> statTemplateROList = DataConverterUtils.convert(statGroupList, StatGroupRO.class);
        return new MonitorResult<>(statTemplateROList);

    }


    @Override
    public MonitorResult<Boolean> addOrUpdateStatGroup(StatGroupRequest groupStatRequest) {
        if (groupStatRequest.getGroupCode() == null || groupStatRequest.getGroupExpression() == null || groupStatRequest.getGroupIndex() == null || groupStatRequest.getGroupName() == null || groupStatRequest.getTemplateId() == null) {
            logger.error("统计分组数据操作，传入的参数不能为空，{}", JSON.toJSONString(groupStatRequest));
            throw new ParamCheckerException("请求参数非法");
        }
        StatGroup statGroup = new StatGroup();
        BeanUtils.copyProperties(groupStatRequest, statGroup);
        if (groupStatRequest.getId() == null) {
            logger.info("新增分组数据操作,传入的参数为{}", JSON.toJSONString(groupStatRequest));
            statGroup.setId(uidService.getId());
            statGroupService.addStatGroup(statGroup);
        } else {
            logger.info("更新分组数据操作,传入的参数为{}", JSON.toJSONString(groupStatRequest));
            statGroupService.updateStatGroup(statGroup);
        }

        return new MonitorResult<>(true);


    }


    @Override
    public MonitorResult<Set<Integer>> queryAllGroupIndex(BaseRequest baseRequest) {
        List<StatGroup> statGroupList = statGroupService.queryAll();
        if (CollectionUtils.isEmpty(statGroupList)) {
            logger.error("查找不到统计分组数据");
            return MonitorResultBuilder.build(System.currentTimeMillis(), "查不到统计分组", null);
        }
        Set<Integer> integerList = new HashSet<>();
        for (StatGroup statGroup : statGroupList) {
            integerList.add(statGroup.getGroupIndex());
        }
        logger.info("返回统计分组序号为{}", JSON.toJSONString(integerList));
        return new MonitorResult<>(integerList);

    }


}
