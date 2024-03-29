package com.treefinance.saas.monitor.biz.service.impl;

import com.google.common.collect.Lists;
import com.treefinance.b2b.saas.util.SaasDateUtils;
import com.treefinance.saas.monitor.biz.service.StatAccessService;
import com.treefinance.saas.monitor.common.constants.MonitorConstants;
import com.treefinance.saas.monitor.context.component.AbstractService;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccess;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.MerchantStatBank;
import com.treefinance.saas.monitor.dao.entity.MerchantStatBankCriteria;
import com.treefinance.saas.monitor.dao.entity.MerchantStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.MerchantStatDayAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.MerchantStatEcommerce;
import com.treefinance.saas.monitor.dao.entity.MerchantStatEcommerceCriteria;
import com.treefinance.saas.monitor.dao.entity.MerchantStatMail;
import com.treefinance.saas.monitor.dao.entity.MerchantStatMailCriteria;
import com.treefinance.saas.monitor.dao.entity.MerchantStatOperator;
import com.treefinance.saas.monitor.dao.entity.MerchantStatOperatorCriteria;
import com.treefinance.saas.monitor.dao.entity.SaasErrorStepDayStat;
import com.treefinance.saas.monitor.dao.entity.SaasErrorStepDayStatCriteria;
import com.treefinance.saas.monitor.dao.mapper.MerchantStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.MerchantStatBankMapper;
import com.treefinance.saas.monitor.dao.mapper.MerchantStatDayAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.MerchantStatEcommerceMapper;
import com.treefinance.saas.monitor.dao.mapper.MerchantStatMailMapper;
import com.treefinance.saas.monitor.dao.mapper.MerchantStatOperatorMapper;
import com.treefinance.saas.monitor.dao.mapper.SaasErrorStepDayStatMapper;
import com.treefinance.saas.monitor.facade.domain.request.MerchantStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.request.MerchantStatBankRequest;
import com.treefinance.saas.monitor.facade.domain.request.MerchantStatDayAccessRequest;
import com.treefinance.saas.monitor.facade.domain.request.MerchantStatEcommerceRequest;
import com.treefinance.saas.monitor.facade.domain.request.MerchantStatMailRequest;
import com.treefinance.saas.monitor.facade.domain.request.MerchantStatOperaterRequest;
import com.treefinance.saas.monitor.facade.domain.request.SaasErrorStepDayStatRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.stat.MerchantStatAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.MerchantStatBankRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.MerchantStatDayAccessRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.MerchantStatEcommerceRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.MerchantStatMailRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.MerchantStatOperatorRO;
import com.treefinance.saas.monitor.facade.domain.ro.stat.SaasErrorStepDayStatRO;
import com.treefinance.toolkit.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2017/6/2.
 */
@Service("statAccessService")
public class StatAccessServiceImpl extends AbstractService implements StatAccessService {

    @Autowired
    private MerchantStatDayAccessMapper merchantStatDayAccessMapper;
    @Autowired
    private MerchantStatAccessMapper merchantStatAccessMapper;
    @Autowired
    private MerchantStatBankMapper merchantStatBankMapper;
    @Autowired
    private MerchantStatEcommerceMapper merchantStatEcommerceMapper;
    @Autowired
    private MerchantStatMailMapper merchantStatMailMapper;
    @Autowired
    private MerchantStatOperatorMapper merchantStatOperatorMapper;
    @Autowired
    private SaasErrorStepDayStatMapper saasErrorStepDayStatMapper;

    @Override
    public MonitorResult<List<MerchantStatDayAccessRO>> queryDayAccessList(MerchantStatDayAccessRequest request) {
        MerchantStatDayAccessCriteria criteria = new MerchantStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());
        MerchantStatDayAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        if (request.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(request.getSaasEnv());
        }
        long totalCount = merchantStatDayAccessMapper.countByExample(criteria);
        List<MerchantStatDayAccessRO> data = Lists.newArrayList();
        if (totalCount > 0) {
            List<MerchantStatDayAccess> list = merchantStatDayAccessMapper.selectByExample(criteria);
            data = convert(list, MerchantStatDayAccessRO.class);
        }
        return MonitorResultBuilder.pageResult(request, data, totalCount);
    }

    @Override
    public MonitorResult<List<MerchantStatDayAccessRO>> queryDayAccessListNoPage(MerchantStatDayAccessRequest request) {
        MerchantStatDayAccessCriteria criteria = new MerchantStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        MerchantStatDayAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        if (request.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(request.getSaasEnv());
        }
        List<MerchantStatDayAccess> list = merchantStatDayAccessMapper.selectByExample(criteria);
        List<MerchantStatDayAccessRO> data = convert(list, MerchantStatDayAccessRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatDayAccessRO>> queryAllDayAccessList(MerchantStatDayAccessRequest request) {
        MerchantStatDayAccessCriteria criteria = new MerchantStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.setLimit(request.getPageSize());
        criteria.setOffset(request.getOffset());
        criteria.createCriteria().andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        long totalCount = merchantStatDayAccessMapper.countByExample(criteria);
        List<MerchantStatDayAccessRO> data = Lists.newArrayList();
        if (totalCount > 0) {
            List<MerchantStatDayAccess> list = merchantStatDayAccessMapper.selectByExample(criteria);
            data = convert(list, MerchantStatDayAccessRO.class);
        }
        return MonitorResultBuilder.pageResult(request, data, totalCount);
    }

    @Override
    public MonitorResult<List<MerchantStatDayAccessRO>> queryAllDayAccessListNoPage(MerchantStatDayAccessRequest request) {
        MerchantStatDayAccessCriteria criteria = new MerchantStatDayAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        MerchantStatDayAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        if (request.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(request.getSaasEnv());
        }
        List<MerchantStatDayAccess> list = merchantStatDayAccessMapper.selectByExample(criteria);
        List<MerchantStatDayAccessRO> data = convert(list, MerchantStatDayAccessRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatAccessRO>> queryAccessList(MerchantStatAccessRequest request) {
        MerchantStatAccessCriteria criteria = new MerchantStatAccessCriteria();
        criteria.setOrderByClause("dataTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatAccess> list = merchantStatAccessMapper.selectByExample(criteria);
        List<MerchantStatAccessRO> data = convert(list, MerchantStatAccessRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatAccessRO>> queryAllAccessList(MerchantStatAccessRequest request) {
        MerchantStatAccessCriteria criteria = new MerchantStatAccessCriteria();
        criteria.setOrderByClause("dataTime asc");
        MerchantStatAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        if (request.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(request.getSaasEnv());
        }
        if (StringUtils.isNotBlank(request.getAppId())) {
            innerCriteria.andAppIdEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_APP_ID);
        }
        List<MerchantStatAccess> list = merchantStatAccessMapper.selectByExample(criteria);
        List<MerchantStatAccessRO> data = convert(list, MerchantStatAccessRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatAccessRO>> queryAllSuccessAccessList(MerchantStatAccessRequest request) {
        if (request.getIntervalMins() != null && request.getIntervalMins() > 0) {
            request.setStartDate(SaasDateUtils.getIntervalDateTime(request.getStartDate(), request.getIntervalMins()));
        }
        if (request.getIntervalMins() != null && request.getIntervalMins() > 0) {
            request.setEndDate(SaasDateUtils.getLaterBorderIntervalDateTime(request.getEndDate(), request.getIntervalMins()));
        }
        MerchantStatAccessCriteria criteria = new MerchantStatAccessCriteria();
        MerchantStatAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andDataTypeEqualTo(request.getDataType());
        if (request.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(request.getSaasEnv());
        }
        if (StringUtils.isNotBlank(request.getAppId())) {
            innerCriteria.andAppIdEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_APP_ID);
        }
        innerCriteria.andDataTimeGreaterThanOrEqualTo(request.getStartDate())
                .andDataTimeLessThan(request.getEndDate());
        List<MerchantStatAccess> list = merchantStatAccessMapper.selectByExample(criteria);
        List<MerchantStatAccessRO> dataList = convert(list, MerchantStatAccessRO.class);
        Integer intervalMins = request.getIntervalMins() == null ? 1 : request.getIntervalMins();

        dataList = this.convertIntervalMinsData(dataList, intervalMins);
        Map<String, MerchantStatAccessRO> dataMap = dataList.stream()
                .collect(Collectors.toMap(d -> DateUtils.format(d.getDataTime()), d -> d));
        List<String> timeList = this.getIntervalTimeStrList(request.getStartDate(), request.getEndDate(),
                intervalMins);
        List<MerchantStatAccessRO> result = Lists.newArrayList();
        for (String timeStr : timeList) {
            if (dataMap.get(timeStr) == null) {
                //初始化一个空值,填充时间空白点
                MerchantStatAccessRO data = new MerchantStatAccessRO();
                data.setDataTime(DateUtils.parse(timeStr));
                data.setTotalCount(0);
                data.setSuccessCount(0);
                data.setFailCount(0);
                data.setCancelCount(0);
                data.setSuccessRate(BigDecimal.ZERO);
                data.setFailRate(BigDecimal.ZERO);
                data.setCancelRate(BigDecimal.ZERO);
                result.add(data);
            } else {
                result.add(dataMap.get(timeStr));
            }
        }
        return MonitorResultBuilder.build(result);
    }

    private List<String> getIntervalTimeStrList(Date startTime, Date endTime, Integer intervalMins) {
        List<Date> list = Lists.newArrayList();
        Date endTimeInterval = SaasDateUtils.getIntervalDateTime(DateUtils.minusMinutes(endTime, intervalMins), intervalMins);
        Date startTimeInterval = SaasDateUtils.getIntervalDateTime(DateUtils.plusMinutes(startTime, intervalMins), intervalMins);
        while (endTimeInterval.compareTo(startTimeInterval) >= 0) {
            list.add(endTimeInterval);
            endTimeInterval = DateUtils.minusMinutes(endTimeInterval, intervalMins);
        }
        list = list.stream().sorted(Date::compareTo).collect(Collectors.toList());
        return list.stream().map(DateUtils::format).collect(Collectors.toList());
    }

    private List<MerchantStatAccessRO> convertIntervalMinsData(List<MerchantStatAccessRO> list, Integer intervalMins) {
        //数据库中最小时间单位为1min钟,若比1min小则不转换,直接返回
        if (intervalMins == null || intervalMins < 1) {
            return list;
        }
        List<MerchantStatAccessRO> result = Lists.newArrayList();

        Map<Date, List<MerchantStatAccessRO>> map = list.stream()
                .collect(Collectors.groupingBy(data -> SaasDateUtils.getLaterBorderIntervalDateTime(data.getDataTime(), intervalMins)));

        for (Map.Entry<Date, List<MerchantStatAccessRO>> entry : map.entrySet()) {
            MerchantStatAccessRO data = new MerchantStatAccessRO();
            BeanUtils.copyProperties(entry.getValue().get(0), data);
            data.setDataTime(entry.getKey());
            int successCount = 0, failCount = 0, cancelCount = 0;
            for (MerchantStatAccessRO item : entry.getValue()) {
                successCount = successCount + item.getSuccessCount();
                failCount = failCount + item.getFailCount();
                cancelCount = cancelCount + item.getCancelCount();
            }
            int totalCount = successCount + failCount + cancelCount;
            BigDecimal successRate = BigDecimal.valueOf(successCount * 100).divide(BigDecimal.valueOf(totalCount), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal failRate = BigDecimal.valueOf(failCount * 100).divide(BigDecimal.valueOf(totalCount), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal cancelRate = BigDecimal.valueOf(cancelCount * 100).divide(BigDecimal.valueOf(totalCount), 2, BigDecimal.ROUND_HALF_UP);

            data.setTotalCount(totalCount);
            data.setSuccessCount(successCount);
            data.setFailCount(failCount);
            data.setCancelCount(cancelCount);
            data.setSuccessRate(successRate);
            data.setFailRate(failRate);
            data.setCancelRate(cancelRate);

            result.add(data);
        }
        return result;
    }

    @Override
    public MonitorResult<List<MerchantStatBankRO>> queryBankList(MerchantStatBankRequest request) {
        MerchantStatBankCriteria criteria = new MerchantStatBankCriteria();
        criteria.setOrderByClause("dataTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andBankIdEqualTo(request.getBankId())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatBank> list = merchantStatBankMapper.selectByExample(criteria);

        List<MerchantStatBankRO> data = convert(list, MerchantStatBankRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatEcommerceRO>> queryEcommerceList(MerchantStatEcommerceRequest request) {
        MerchantStatEcommerceCriteria criteria = new MerchantStatEcommerceCriteria();
        criteria.setOrderByClause("dataTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andEcommerceIdEqualTo(request.getEcommerceId())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatEcommerce> list = merchantStatEcommerceMapper.selectByExample(criteria);

        List<MerchantStatEcommerceRO> data = convert(list, MerchantStatEcommerceRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatMailRO>> queryMailList(MerchantStatMailRequest request) {
        MerchantStatMailCriteria criteria = new MerchantStatMailCriteria();
        criteria.setOrderByClause("dataTime asc");
        criteria.createCriteria().andAppIdEqualTo(request.getAppId())
                .andMailCodeEqualTo(request.getMailCode())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<MerchantStatMail> list = merchantStatMailMapper.selectByExample(criteria);

        List<MerchantStatMailRO> data = convert(list, MerchantStatMailRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<MerchantStatOperatorRO>> queryOperatorList(MerchantStatOperaterRequest request) {
        MerchantStatOperatorCriteria criteria = new MerchantStatOperatorCriteria();
        criteria.setOrderByClause("dataTime asc");
        MerchantStatOperatorCriteria.Criteria operatorCriteria = criteria.createCriteria();
        operatorCriteria.andAppIdEqualTo(request.getAppId())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        if (StringUtils.isNotBlank(request.getOperaterId())) {
            operatorCriteria.andOperaterIdEqualTo(request.getOperaterId());
        }
        List<MerchantStatOperator> list = merchantStatOperatorMapper.selectByExample(criteria);

        List<MerchantStatOperatorRO> data = Lists.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return MonitorResultBuilder.build(data);
        }
        data = convert(list, MerchantStatOperatorRO.class);
        return MonitorResultBuilder.build(data);
    }

    @Override
    public MonitorResult<List<SaasErrorStepDayStatRO>> querySaasErrorStepDayStatListNoPage(SaasErrorStepDayStatRequest request) {
        SaasErrorStepDayStatCriteria criteria = new SaasErrorStepDayStatCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria().andDataTypeEqualTo(request.getDataType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());
        List<SaasErrorStepDayStat> list = saasErrorStepDayStatMapper.selectByExample(criteria);
        List<SaasErrorStepDayStatRO> data = convert(list, SaasErrorStepDayStatRO.class);
        return MonitorResultBuilder.build(data);
    }
}
