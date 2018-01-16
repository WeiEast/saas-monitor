package com.treefinance.saas.monitor.dao.ecommerce.Impl;

import com.treefinance.saas.monitor.common.domain.dto.EcommerceTimeShareDTO;
import com.treefinance.saas.monitor.dao.ecommerce.EcommerceDetailAccessDao;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccess;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.EcommerceAllStatAccessMapper;
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

    @Override
    public List<EcommerceAllStatAccess> getEcommerceAllDetailList(EcommerceTimeShareDTO request) {

        Date dataDate = request.getDataDate();
        Byte statType = request.getStatType();
        String appId = request.getAppId();


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter1.format(request.getDataDate());
        ParsePosition pos = new ParsePosition(0);
        Date dataDate2 = formatter.parse((dateString + " 23:59:59"), pos);


        logger.info("查询电商日监控分时统计数据 dao层,传入的参数为{}", request.toString());
        EcommerceAllStatAccessCriteria ecommerceAllStatAccessCriteria = new EcommerceAllStatAccessCriteria();
        ecommerceAllStatAccessCriteria.createCriteria().andAppIdEqualTo(appId).andDataTypeEqualTo(statType).andDataTimeBetween(dataDate, dataDate2);
        List<EcommerceAllStatAccess> allStatAccessList = ecommerceAllStatAccessMapper.selectByExample(ecommerceAllStatAccessCriteria);

        return allStatAccessList;


    }


}
