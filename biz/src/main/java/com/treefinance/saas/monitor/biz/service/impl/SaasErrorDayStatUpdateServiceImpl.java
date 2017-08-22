package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.SaasErrorDayStatUpdateService;
import com.treefinance.saas.monitor.common.domain.dto.SaasErrorDayStatDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.dao.entity.SaasErrorDayStat;
import com.treefinance.saas.monitor.dao.mapper.SaasErrorDayStatUpdateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by haojiahong on 2017/8/22.
 */
@Service("saasErrorDayStatUpdateService")
public class SaasErrorDayStatUpdateServiceImpl implements SaasErrorDayStatUpdateService {

    @Autowired
    private SaasErrorDayStatUpdateMapper saasErrorDayStatUpdateMapper;

    @Override
    public void batchInsertErrorDayStat(List<SaasErrorDayStatDTO> list) {
        List<SaasErrorDayStat> dataList = DataConverterUtils.convert(list, SaasErrorDayStat.class);
        dataList.forEach(data -> saasErrorDayStatUpdateMapper.insertOrUpdateSelectiveDayError(data));
    }
}
