package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.biz.autostat.basicdata.service.BasicDataService;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.BasicData;
import com.treefinance.saas.monitor.facade.domain.base.PageRequest;
import com.treefinance.saas.monitor.facade.domain.request.BasicDataRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.BasicDataRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.BasicDataFacade;
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
public class BasicDataFacadeImpl implements BasicDataFacade {

    private  static  final Logger logger = LoggerFactory.getLogger(BasicDataFacade.class);

    @Autowired
    private BasicDataService  basicDataService;

    @Override
    public MonitorResult<List<BasicDataRO>> queryAllBasicData(PageRequest pageRequest) {

        logger.info("查询所有的基础数据，传入的分页数据为{}",pageRequest.toString());

        List<BasicData> dataList=basicDataService.queryAll();
        if(CollectionUtils.isEmpty(dataList)){
            logger.info("查不到基础数据");
            return new MonitorResult(System.currentTimeMillis(),"查询不到基础数据",null);
        }
        List<BasicDataRO> dataROList = DataConverterUtils.convert(dataList,BasicDataRO.class);
        long totalCount = basicDataService.countBasicData();
        MonitorResult<List<BasicDataRO>>  monitorResult = new MonitorResult(pageRequest,dataROList,totalCount);
        logger.info("返回查询基础数据的result为{}",monitorResult.toString());
       return monitorResult;

    }

    @Override
    public int  addBasicData(BasicDataRequest basicDataRequest) {
        if(basicDataRequest.getDataCode()==null||basicDataRequest.getDataJson()==null||basicDataRequest.getDataName()==null||basicDataRequest.getDataSource()==null||basicDataRequest.getDataSourceConfigJson()==null)
        {
            logger.info("新增基础数据，请求参数不能为空", JSON.toJSON(basicDataRequest));
            throw  new ParamCheckerException("请求参数非法");
        }
        logger.info("新增一个基础数据，传入的基础数据为{]",basicDataRequest.toString());
        BasicData basicData= new BasicData();
        basicData.setDataCode(basicDataRequest.getDataCode());
        basicData.setDataJson(basicDataRequest.getDataJson());
        basicData.setDataName(basicDataRequest.getDataName());
        basicData.setDataSource(basicDataRequest.getDataSource());
        basicData.setDataSourceConfigJson(basicDataRequest.getDataSourceConfigJson());


        return  basicDataService.addBasicData(basicData);



    }


}
