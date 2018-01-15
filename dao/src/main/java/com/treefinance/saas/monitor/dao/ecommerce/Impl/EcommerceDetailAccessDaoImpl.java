package com.treefinance.saas.monitor.dao.ecommerce.Impl;

import com.treefinance.saas.monitor.dao.ecommerce.EcommerceDetailAccessDao;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccess;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.EcommerceAllStatAccessMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
    public List<EcommerceAllStatAccess> getEcommerceAllDetailList(Date dataDate, Byte statType, String appId) {
        logger.info("查询电商日监控分时统计数据 dao层,传入的参数为 dataDate:{} statType:{} appId:{}", dataDate, statType, appId);
        List<EcommerceAllStatAccess> allStatAccessList = new ArrayList<>();

        try {
            EcommerceAllStatAccessCriteria ecommerceAllStatAccessCriteria = new EcommerceAllStatAccessCriteria();
            ecommerceAllStatAccessCriteria.createCriteria().andAppIdEqualTo(appId).andDataTypeEqualTo(statType).andDataTimeEqualTo(dataDate);
            allStatAccessList = ecommerceAllStatAccessMapper.selectByExample(ecommerceAllStatAccessCriteria);
        } catch (Exception e) {
            logger.info("查询为空异常");
        }
        for(EcommerceAllStatAccess ecommerceAllStatAccess:allStatAccessList) {

            logger.info("查询电商日监控分时统计数据为{}", ecommerceAllStatAccess.toString());
        }

        return allStatAccessList;


    }


}
