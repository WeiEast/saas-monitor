package com.treefinance.saas.monitor.common.domain.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author chengtong
 * @date 18/3/12 18:57
 */
@Setter
@Getter
public class EmailAlarmMsgDTO extends BaseAlarmMsgDTO {


    /**邮箱*/
    private String email;


}
