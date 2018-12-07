package com.treefinance.saas.monitor.biz.service.impl;

import com.google.common.base.Splitter;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.service.OperatorStatAccessService;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import com.treefinance.saas.monitor.util.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.OperatorStatAccessMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chengtong
 * @date 18/9/12 16:58
 */
@Service
public class OperatorStatAccessServiceImpl implements OperatorStatAccessService {

    private static final Logger logger = LoggerFactory.getLogger(OperatorStatAccessServiceImpl.class);

    @Autowired
    private OperatorStatAccessMapper operatorStatAccessMapper;
    @Autowired
    protected DiamondConfig diamondConfig;


    private final double RATE = -0.1D;


    @Override
    public List<String> queryDecreasedOperator(ESaasEnv saasEnv) {

        Date now = new Date();

        Date start = MonitorDateUtils.getOClockTime(MonitorDateUtils.addTimeUnit(now, Calendar.DATE, -7));

        List<String> operatorNameList = Splitter.on(",").splitToList(diamondConfig.getOperatorAlarmOperatorNameList());

        OperatorStatAccessCriteria criteria = new OperatorStatAccessCriteria();
        criteria.createCriteria().andSaasEnvEqualTo((byte) saasEnv.getValue()).andGroupNameIn(operatorNameList).andDataTimeGreaterThanOrEqualTo(start);

        List<OperatorStatAccess> list = operatorStatAccessMapper.selectByExample(criteria);

        if (list.isEmpty()) {
            return Collections.singletonList("");
        }

        Map<String, List<OperatorStatAccess>> map = list.stream().collect(Collectors.groupingBy(OperatorStatAccess::getGroupName));

        List<AllBizTypeStatAccessServiceImpl.CalculateModel> orderedList = new ArrayList<>();


        for (String name : map.keySet()) {

            List<OperatorStatAccess> operatorStatAccessList = map.get(name);

            AllBizTypeStatAccessServiceImpl.CalculateModel model = new AllBizTypeStatAccessServiceImpl.CalculateModel();

            for (OperatorStatAccess operatorStatAccess : operatorStatAccessList) {

                if (MonitorDateUtils.isSameDay(operatorStatAccess.getDataTime(), now)) {
                    model.succToday += operatorStatAccess.getCallbackSuccessCount();
                    model.totalToday += operatorStatAccess.getEntryCount();
                    continue;
                }

                model.succCount += operatorStatAccess.getCallbackSuccessCount();
                model.totalCount += operatorStatAccess.getEntryCount();

            }

            if(model.totalCount == 0){
                model.average = BigDecimal.ZERO;
            }else {
                model.average = new BigDecimal(model.succCount).divide(new BigDecimal(model.totalCount), 4,
                        RoundingMode.HALF_UP);
            }

            if(model.totalToday == 0){
                continue;
            }else {
                model.rateToday = new BigDecimal(model.succToday).divide(new BigDecimal(model.totalToday), 4,
                        RoundingMode.HALF_UP);
            }
            BigDecimal compare;
            if(model.average.compareTo(BigDecimal.ZERO) <= 0){
                compare = BigDecimal.ZERO;
            }else {
                compare = model.rateToday.subtract(model.average).divide(model.average, 4, RoundingMode
                        .HALF_UP);
            }


            if (compare.compareTo(new BigDecimal(RATE)) < 0) {
                model.increase = compare;
                model.name = name;
                orderedList.add(model);
            }


        }

        if(orderedList.isEmpty()){
            return Collections.singletonList("");
        }

        logger.info("ordered list：{}", orderedList);

        orderedList.sort(Comparator.comparing(o -> o.increase));

        return orderedList.stream().map(calculateModel -> calculateModel.name).collect(Collectors.toList());
    }
}
