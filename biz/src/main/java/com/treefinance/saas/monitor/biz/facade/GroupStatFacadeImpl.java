package com.treefinance.saas.monitor.biz.facade;

import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatGroupService;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.StatGroup;
import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
import com.treefinance.saas.monitor.facade.domain.request.GroupStatRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.StatGroupRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.stat.GroupStatFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/26下午8:16
 */
@Service("groupStatFacade")
public class GroupStatFacadeImpl implements GroupStatFacade{

    private static  final Logger logger = LoggerFactory.getLogger(GroupStatFacade.class);

    @Autowired
    StatGroupService statGroupService;
    @Autowired
    UidService uidService;


    @Override
    public MonitorResult<List<StatGroupRO>> queryStatGroupByTemplateId(GroupStatRequest groupStatRequest){
        if(groupStatRequest.getId()==null) {
            logger.error("根据模板ID返回分组列表，传入的模板ID为空");
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("根据模板ID返回分组列表，传入的模板ID为{}",groupStatRequest.toString());
        List<StatGroup> statGroupList = statGroupService.queryByTemplateId(groupStatRequest.getTemplateId());
        List<StatGroupRO> statTemplateROList = DataConverterUtils.convert(statGroupList, StatGroupRO.class);
        if(StringUtils.isEmpty(statGroupList))
        {
            logger.error("查不到统计分组");
            return new MonitorResult(System.currentTimeMillis(), "查不到统计分组", null);
        }
        return  new MonitorResult<>(statTemplateROList);

    }


    @Override
    public MonitorResult<Boolean> addOrUpdateStatGroup(GroupStatRequest groupStatRequest) {
        if(groupStatRequest.getGroupCode()==null||groupStatRequest.getGroupExpression()==null||groupStatRequest.getGroupIndex()==null||groupStatRequest.getGroupName()==null||groupStatRequest.getTemplateId()==null)
        {
            logger.error("统计分组数据操作，传入的参数不能为空，{}",groupStatRequest.toString());
            throw new ParamCheckerException("请求参数非法");
        }
        StatGroup  statGroup = new StatGroup();
        if(groupStatRequest.getId()==null)
        {
            logger.info("新增分组数据操作,传入的参数为{}",groupStatRequest.toString());
            statGroup.setId(uidService.getId());

        }
        else {
            logger.info("更新分组数据操作,传入的参数为{}",groupStatRequest.toString());
        }
        BeanUtils.copyProperties(groupStatRequest,statGroup);
        statGroupService.addOrUpdateStatGroup(statGroup);

        return new MonitorResult<>(true);



    }


    @Override
    public MonitorResult<List<Integer>>  queryAllGroupIndex(BaseRequest baseRequest) {
        List<StatGroup> statGroupList = statGroupService.queryAll();
        if(CollectionUtils.isEmpty(statGroupList))
        {
            logger.error("查找不到统计分组数据");
            return new MonitorResult(System.currentTimeMillis(), "查不到统计分组", null);
        }
        List<Integer> integerList = new ArrayList<>();
        for(StatGroup statGroup:statGroupList){
            integerList.add(statGroup.getGroupIndex());
        }
        logger.info("返回统计分组序号为{}",integerList.toString());
        return new MonitorResult<>(integerList);

    }



}
