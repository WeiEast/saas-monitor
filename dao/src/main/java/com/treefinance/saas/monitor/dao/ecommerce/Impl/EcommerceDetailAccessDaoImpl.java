package com.treefinance.saas.monitor.dao.ecommerce.Impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.common.domain.dto.EcommerceTimeShareDTO;
import com.treefinance.saas.monitor.dao.ecommerce.EcommerceDetailAccessDao;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccess;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatDayAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.EcommerceAllStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.EcommerceAllStatDayAccessMapper;
import com.treefinance.toolkit.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/1/15上午11:53
 */
@Repository
public class EcommerceDetailAccessDaoImpl implements EcommerceDetailAccessDao {


    private final static Logger logger = LoggerFactory.getLogger(EcommerceDetailAccessDaoImpl.class);

    @Autowired
    private EcommerceAllStatAccessMapper ecommerceAllStatAccessMapper;
    @Autowired
    private EcommerceAllStatDayAccessMapper ecommerceAllStatDayAccessMapper;

    @Override
    public List<EcommerceAllStatAccess> getEcommerceAllDetailList(EcommerceTimeShareDTO request) {
        logger.info("查询电商日监控分时统计数据 dao层,传入的参数为{}", JSON.toJSONString(request));
        EcommerceAllStatAccessCriteria ecommerceAllStatAccessCriteria = new EcommerceAllStatAccessCriteria();
        ecommerceAllStatAccessCriteria.setOrderByClause("dataTime desc");

        EcommerceAllStatAccessCriteria.Criteria innerCriteria = ecommerceAllStatAccessCriteria.createCriteria();
        if (request.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(request.getSaasEnv());
        } else {
            innerCriteria.andSaasEnvEqualTo((byte) 0);
        }

        innerCriteria.andAppIdEqualTo(request.getAppId())
                .andDataTypeEqualTo(request.getStatType()).andSourceTypeEqualTo(request.getSourceType())
                .andDataTimeBetween(request.getDataDate(), DateUtils.getEndTimeOfDay(request.getDataDate()));

        return ecommerceAllStatAccessMapper.selectByExample(ecommerceAllStatAccessCriteria);

    }


    @Override
    public List<EcommerceAllStatDayAccess> getEcommerceAllList(EcommerceTimeShareDTO request) {


        logger.info("查询电商日监控整体统计数据 dao层,传入的参数为:{}", JSON.toJSONString(request));
        EcommerceAllStatDayAccessCriteria ecommerceAllStatDayAccessCriteria = new EcommerceAllStatDayAccessCriteria();


        ecommerceAllStatDayAccessCriteria.setOrderByClause("dataTime desc");
        ecommerceAllStatDayAccessCriteria.setLimit(request.getPageSize());
        ecommerceAllStatDayAccessCriteria.setOffset(request.getOffset());

        EcommerceAllStatDayAccessCriteria.Criteria innerCriteria = ecommerceAllStatDayAccessCriteria.createCriteria();
        if (request.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(request.getSaasEnv());
        } else {
            innerCriteria.andSaasEnvEqualTo((byte) 0);
        }
        innerCriteria.andAppIdEqualTo(request.getAppId())
                .andSourceTypeEqualTo(request.getSourceType()).andDataTypeEqualTo(request.getStatType())
                .andDataTimeBetween(request.getStartDate(), request.getEndDate());

        return ecommerceAllStatDayAccessMapper.selectByExample(ecommerceAllStatDayAccessCriteria);

    }

    @Override
    public long countByExample(EcommerceTimeShareDTO ecommerceTimeShareDTO) {


        EcommerceAllStatDayAccessCriteria ecommerceAllStatDayAccessCriteria = new EcommerceAllStatDayAccessCriteria();

        ecommerceAllStatDayAccessCriteria.setOrderByClause("dataTime desc");
        ecommerceAllStatDayAccessCriteria.setLimit(ecommerceTimeShareDTO.getPageSize());
        ecommerceAllStatDayAccessCriteria.setOffset(ecommerceTimeShareDTO.getOffset());
        EcommerceAllStatDayAccessCriteria.Criteria innerCriteria = ecommerceAllStatDayAccessCriteria.createCriteria();

        if (ecommerceTimeShareDTO.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(ecommerceTimeShareDTO.getSaasEnv());
        } else {
            innerCriteria.andSaasEnvEqualTo((byte) 0);
        }

        innerCriteria.andSourceTypeEqualTo(ecommerceTimeShareDTO.getSourceType())
                .andAppIdEqualTo(ecommerceTimeShareDTO.getAppId()).andDataTypeEqualTo(ecommerceTimeShareDTO.getStatType())
                .andDataTimeBetween(ecommerceTimeShareDTO.getStartDate(), ecommerceTimeShareDTO.getEndDate());


        long total = ecommerceAllStatDayAccessMapper.countByExample(ecommerceAllStatDayAccessCriteria);
        logger.info("查询电商日监控整体统计数据 dao层,返回的分页数目为{}", total);


        return total;
    }


}
