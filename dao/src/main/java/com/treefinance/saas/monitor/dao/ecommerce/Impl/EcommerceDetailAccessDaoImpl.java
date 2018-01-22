package com.treefinance.saas.monitor.dao.ecommerce.Impl;

import com.treefinance.saas.monitor.common.domain.dto.EcommerceTimeShareDTO;
import com.treefinance.saas.monitor.dao.ecommerce.EcommerceDetailAccessDao;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccess;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatDayAccess;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatDayAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.EcommerceAllStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.EcommerceAllStatDayAccessMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/1/15上午11:53
 */
@Repository
public class EcommerceDetailAccessDaoImpl implements EcommerceDetailAccessDao {


    private final static Logger logger = LoggerFactory.getLogger(EcommerceDetailAccessDaoImpl.class);

    @Autowired
    EcommerceAllStatAccessMapper ecommerceAllStatAccessMapper;
    @Autowired
    EcommerceAllStatDayAccessMapper ecommerceAllStatDayAccessMapper;

    @Override
    public List<EcommerceAllStatAccess> getEcommerceAllDetailList(EcommerceTimeShareDTO request) {

        Date dataDate = request.getDataDate();
        Byte sourceType = request.getSourceType();


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter1.format(request.getDataDate());
        ParsePosition pos = new ParsePosition(0);
        Date dataDate2 = formatter.parse((dateString + " 23:59:59"), pos);


        logger.info("查询电商日监控分时统计数据 dao层,传入的参数为{}", request.toString());
        EcommerceAllStatAccessCriteria ecommerceAllStatAccessCriteria = new EcommerceAllStatAccessCriteria();
        ecommerceAllStatAccessCriteria.setOrderByClause("dataTime desc");




        ecommerceAllStatAccessCriteria.createCriteria().andAppIdEqualTo(request.getAppId()).andDataTypeEqualTo(request.getStatType()).andSourceTypeEqualTo(request.getSourceType()).andDataTimeBetween(dataDate, dataDate2);

        List<EcommerceAllStatAccess> allStatAccessList = ecommerceAllStatAccessMapper.selectByExample(ecommerceAllStatAccessCriteria);

        return allStatAccessList;


    }

    @Override
    public List<EcommerceAllStatDayAccess> getEcommerceAllList(EcommerceTimeShareDTO request) {


        logger.info("查询电商日监控整体统计数据 dao层,传入的参数为{}", request.toString());
        EcommerceAllStatDayAccessCriteria ecommerceAllStatDayAccessCriteria = new EcommerceAllStatDayAccessCriteria();


        ecommerceAllStatDayAccessCriteria.setOrderByClause("dataTime desc");
        ecommerceAllStatDayAccessCriteria.setLimit(request.getPageSize());
        ecommerceAllStatDayAccessCriteria.setOffset(request.getOffset());


        ecommerceAllStatDayAccessCriteria.createCriteria().andAppIdEqualTo(request.getAppId()).andSourceTypeEqualTo(request.getSourceType()).andDataTypeEqualTo(request.getStatType()).andDataTimeBetween(request.getStartDate(), request.getEndDate());

        List<EcommerceAllStatDayAccess> allStatAccessList = ecommerceAllStatDayAccessMapper.selectByExample(ecommerceAllStatDayAccessCriteria);

        return allStatAccessList;


    }

    @Override
    public long countByExample(EcommerceTimeShareDTO ecommerceTimeShareDTO) {


        EcommerceAllStatDayAccessCriteria ecommerceAllStatDayAccessCriteria = new EcommerceAllStatDayAccessCriteria();


        ecommerceAllStatDayAccessCriteria.setOrderByClause("dataTime desc");
        ecommerceAllStatDayAccessCriteria.setLimit(ecommerceTimeShareDTO.getPageSize());
        ecommerceAllStatDayAccessCriteria.setOffset(ecommerceTimeShareDTO.getOffset());


        ecommerceAllStatDayAccessCriteria.createCriteria().andSourceTypeEqualTo(ecommerceTimeShareDTO.getSourceType()).andAppIdEqualTo(ecommerceTimeShareDTO.getAppId()).andDataTypeEqualTo(ecommerceTimeShareDTO.getStatType()).andDataTimeBetween(ecommerceTimeShareDTO.getStartDate(), ecommerceTimeShareDTO.getEndDate());


        long total = ecommerceAllStatDayAccessMapper.countByExample(ecommerceAllStatDayAccessCriteria);
        logger.info("查询电商日监控整体统计数据 dao层,返回的分页数目为{}", total);


        return total;
    }


}
