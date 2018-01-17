package com.treefinance.saas.monitor.dao.ecommerce;

import com.treefinance.saas.monitor.common.domain.dto.EcommerceTimeShareDTO;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccess;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatDayAccess;

import java.util.Date;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/1/15上午11:52
 */
public interface EcommerceDetailAccessDao {

    List<EcommerceAllStatAccess> getEcommerceAllDetailList(EcommerceTimeShareDTO request);

    List<EcommerceAllStatDayAccess> getEcommerceAllList(EcommerceTimeShareDTO ecommerceTimeShareDTO);
}
