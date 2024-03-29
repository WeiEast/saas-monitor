package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.monitor.biz.autostat.basicdata.service.BasicDataService;
import com.treefinance.saas.monitor.context.component.AbstractFacade;
import com.treefinance.saas.monitor.dao.entity.BasicData;
import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
import com.treefinance.saas.monitor.facade.domain.base.PageRequest;
import com.treefinance.saas.monitor.facade.domain.request.BasicDataRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.BasicDataRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.autostat.BasicDataFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/23下午5:29
 */
@Service("basicDataFacade")
public class BasicDataFacadeImpl extends AbstractFacade implements BasicDataFacade {

    private static final Logger logger = LoggerFactory.getLogger(BasicDataFacade.class);

    @Autowired
    private BasicDataService basicDataService;
    @Autowired
    private UidService uidService;

    @Override
    public MonitorResult<List<BasicDataRO>> queryAllBasicData(PageRequest pageRequest) {

        logger.info("查询所有的基础数据，传入的分页数据为:{}", JSON.toJSONString(pageRequest));

        List<BasicData> dataList = basicDataService.queryAllBasicDataWithPaging(pageRequest);
        if (CollectionUtils.isEmpty(dataList)) {
            logger.error("查不到基础数据");
            return MonitorResultBuilder.build(System.currentTimeMillis(), "查询不到基础数据", null);
        }
        List<BasicDataRO> dataROList = convert(dataList, BasicDataRO.class);
        long totalCount = basicDataService.countBasicData(pageRequest);
        MonitorResult<List<BasicDataRO>> result = MonitorResultBuilder.pageResult(pageRequest, dataROList, totalCount);
        logger.info("返回查询基础数据的result为{}", JSON.toJSONString(result));
        return result;

    }

    @Override
    public MonitorResult<Boolean> addBasicData(BasicDataRequest basicDataRequest) {
        if (basicDataRequest.getDataCode() == null || basicDataRequest.getDataJson() == null || basicDataRequest.getDataName() == null || basicDataRequest.getDataSource() == null
            || basicDataRequest.getDataSourceConfigJson() == null) {
            logger.error("新增基础数据，请求参数不能为空:{}", JSON.toJSONString(basicDataRequest));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("新增一个基础数据，传入的基础数据为{}", JSON.toJSONString(basicDataRequest));
        long id = uidService.getId();
        BasicData basicData = new BasicData();
        copyProperties(basicDataRequest, basicData);

        basicData.setId(id);
        basicDataService.addBasicData(basicData);
        return new MonitorResult<>(true);
    }

    @Override
    public MonitorResult<Boolean> updateBasicData(BasicDataRequest basicDataRequest) {
        if (basicDataRequest.getDataCode() == null || basicDataRequest.getDataJson() == null || basicDataRequest.getDataName() == null || basicDataRequest.getDataSource() == null
            || basicDataRequest.getDataSourceConfigJson() == null) {
            logger.error("更新基础数据，请求参数不能为空{}", JSON.toJSONString(basicDataRequest));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("更新基础数据，传入的基础数据为{}", JSON.toJSONString(basicDataRequest));
        BasicData basicData = new BasicData();
        basicData.setId(basicDataRequest.getId());
        basicData.setDataSourceConfigJson(basicDataRequest.getDataSourceConfigJson());
        basicData.setDataSource(basicDataRequest.getDataSource());
        basicData.setDataName(basicDataRequest.getDataName());
        basicData.setDataJson(basicDataRequest.getDataJson());
        basicData.setDataCode(basicDataRequest.getDataCode());

        basicDataService.updateBasicData(basicData);

        return new MonitorResult<>(true);
    }

    @Override
    public MonitorResult<List<String>> queryAllDataName(BaseRequest baseRequest) {

        List<String> dataList = basicDataService.queryAllDataName();
        if (CollectionUtils.isEmpty(dataList)) {
            logger.error("查不到基础数据名字");
            return MonitorResultBuilder.build(System.currentTimeMillis(), "查询不到基础数据名字", null);
        }

        logger.info("返回查询基础数据的result为{}", JSON.toJSONString(dataList));
        return new MonitorResult<>(dataList);

    }

    @Override
    public MonitorResult<BasicDataRO> getBasicDataById(BasicDataRequest basicDataRequest) {

        logger.info("根据ID查询对应的基础数据名字，传入的ID为{}", basicDataRequest.getId());
        BasicData basicData = basicDataService.getBasicDatayId(basicDataRequest.getId());
        if (basicData == null) {
            logger.error("查不到基础数据");
            return MonitorResultBuilder.build(System.currentTimeMillis(), "查询不到基础数据", null);
        }
        BasicDataRO basicDataRO = convert(basicData, BasicDataRO.class);
        MonitorResult<BasicDataRO> monitorResult = MonitorResultBuilder.build(basicDataRO);
        logger.info("返回查询基础数据的result为{}", JSON.toJSONString(monitorResult));
        return monitorResult;

    }

}
