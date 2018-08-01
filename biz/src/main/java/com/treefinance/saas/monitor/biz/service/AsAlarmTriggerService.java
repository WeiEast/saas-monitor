package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.dao.entity.AsAlarmTrigger;

/**
 * @author:guoguoyun
 * @date:Created in 2018/7/18下午8:15
 */
public interface AsAlarmTriggerService {

     /**
      * 根据条件ID查询预警触发条件
      * @param id
      * @return
      */
     AsAlarmTrigger getAsAlarmTriggerByPrimaryKey(long id);

}
