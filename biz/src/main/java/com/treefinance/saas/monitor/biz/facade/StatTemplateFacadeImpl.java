package com.treefinance.saas.monitor.biz.facade;

import com.alibaba.fastjson.JSON;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.monitor.biz.autostat.template.service.StatTemplateService;
import com.treefinance.saas.monitor.context.component.AbstractFacade;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import com.treefinance.saas.monitor.facade.domain.request.StatTemplateRequest;
import com.treefinance.saas.monitor.facade.domain.request.autostat.TemplateExpressionTestRequest;
import com.treefinance.saas.monitor.facade.domain.request.autostat.TemplateTestRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.StatTemplateRO;
import com.treefinance.saas.monitor.facade.exception.ParamCheckerException;
import com.treefinance.saas.monitor.facade.service.autostat.StatTemplateFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/25下午4:09
 */
@Service("statTemplateFacade")
public class StatTemplateFacadeImpl extends AbstractFacade implements StatTemplateFacade {

    private static final Logger logger = LoggerFactory.getLogger(StatTemplateFacade.class);

    @Autowired
    private StatTemplateService statTemplateService;
    @Autowired
    private UidService uidService;


    @Override
    public MonitorResult<List<StatTemplateRO>> queryStatTemplate(StatTemplateRequest templateStatRequest) {
        logger.info("查询模板数据，传入的请求信息为{}", JSON.toJSONString(templateStatRequest));
        List<StatTemplate> statTemplateList = statTemplateService.queryStatTemplate(templateStatRequest);
        if (CollectionUtils.isEmpty(statTemplateList)) {
            logger.error("查不到模板数据");
            return MonitorResultBuilder.build(System.currentTimeMillis(), "查询不到模板数据", null);
        }
        List<StatTemplateRO> statTemplateROS = convert(statTemplateList, StatTemplateRO.class);
        long totalCount = statTemplateService.countStatTemplate(templateStatRequest);
        MonitorResult<List<StatTemplateRO>> monitorResult = MonitorResultBuilder.pageResult(templateStatRequest, statTemplateROS, totalCount);
        logger.info("返回查询模板数据的result为{}", JSON.toJSONString(monitorResult));
        return monitorResult;

    }

    @Override
    public MonitorResult<Boolean> addStatTemplate(StatTemplateRequest templateStatRequest) {
        if (templateStatRequest.getStatCron() == null || templateStatRequest.getEffectiveTime() == null || templateStatRequest.getTemplateName() == null || templateStatRequest.getStatus() == null || templateStatRequest.getTemplateCode() == null || templateStatRequest.getBasicDataId() == null || templateStatRequest.getBasicDataFilter() == null || templateStatRequest.getFlushDataCron() == null) {
            logger.error("新增模板数据，请求参数不能为空:{}", JSON.toJSONString(templateStatRequest));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("新增一个模板数据，传入的模板数据为{}", JSON.toJSONString(templateStatRequest));
        long id = uidService.getId();
        StatTemplate statTemplate = new StatTemplate();
        copyProperties(templateStatRequest, statTemplate);
        statTemplate.setId(id);
        statTemplateService.addStatTemplate(statTemplate);
        return new MonitorResult<>(true);
    }

    @Override
    public MonitorResult<Boolean> updateStatTemplate(StatTemplateRequest templateStatRequest) {
        if (templateStatRequest.getStatCron() == null || templateStatRequest.getEffectiveTime() == null || templateStatRequest.getTemplateName() == null || templateStatRequest.getStatus() == null || templateStatRequest.getTemplateCode() == null || templateStatRequest.getBasicDataId() == null || templateStatRequest.getBasicDataFilter() == null || templateStatRequest.getFlushDataCron() == null) {
            logger.error("更新模板数据，请求参数不能为空:{}", JSON.toJSONString(templateStatRequest));
            throw new ParamCheckerException("请求参数非法");
        }
        logger.info("更新模板数据，传入的模板数据为{}", JSON.toJSONString(templateStatRequest));

        StatTemplate statTemplate = new StatTemplate();
        copyProperties(templateStatRequest, statTemplate);
        statTemplateService.updateStatTemplate(statTemplate);
        return new MonitorResult<>(true);
    }

    @Override
    public MonitorResult<String> testTemplateExpression(TemplateExpressionTestRequest request) {
        String result = statTemplateService.testTemplateExpression(request);
        MonitorResult<String> monitorResult = MonitorResultBuilder.build();
        monitorResult.setData(result);
        return monitorResult;
    }

    @Override
    public MonitorResult<String> testTemplate(TemplateTestRequest request) {
        String result = statTemplateService.testTemplate(request);
        MonitorResult<String> monitorResult = MonitorResultBuilder.build();
        monitorResult.setData(result);
        return monitorResult;
    }
}
