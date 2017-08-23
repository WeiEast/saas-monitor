package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.SaasErrorStepDayStatUpdateService;
import com.treefinance.saas.monitor.common.domain.dto.SaasErrorStepDayStatDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.SaasErrorStepDayStat;
import com.treefinance.saas.monitor.dao.mapper.SaasErrorStepDayStatUpdateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by haojiahong on 2017/8/22.
 */
@Service("saasErrorStepDayStatUpdateService")
public class SaasErrorStepDayStatUpdateServiceImpl implements SaasErrorStepDayStatUpdateService {

    @Autowired
    private SaasErrorStepDayStatUpdateMapper saasErrorStepDayStatUpdateMapper;

    @Override
    public void batchInsertErrorDayStat(List<SaasErrorStepDayStatDTO> list) {
        List<SaasErrorStepDayStat> dataList = DataConverterUtils.convert(list, SaasErrorStepDayStat.class);
        dataList.forEach(data -> saasErrorStepDayStatUpdateMapper.insertOrUpdateSelectiveDayError(data));
    }
}
