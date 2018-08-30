package com.treefinance.saas.monitor.facade.domain.request.autoalarm;

import com.treefinance.saas.monitor.facade.domain.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author haojiahong
 * @date 2018/7/19
 */
@Getter
@Setter
public class AlarmBasicConfigurationTestRequest extends BaseRequest {

    private static final long serialVersionUID = 3803246362323207422L;

    /**
     * 要测试的选项的编码
     */
    private String testCode;

    /**
     * 要测试的选项类型
     * 1:常量
     * 2:预警数据查询
     * 3:变量
     * 4:预警触发条件
     */
    private Byte testType;

    /**
     * 预警配置表相关信息
     */
    private AsAlarmInfoRequest asAlarmInfoRequest;

    /**
     * 预警常量表相关信息
     */
    private List<AsAlarmConstantInfoRequest> asAlarmConstantInfoRequestList;

    /**
     * 预警数据查询表相关信息
     */
    private List<AsAlarmQueryInfoRequest> asAlarmQueryInfoRequestList;

    /**
     * 预警变量表相关信息
     */
    private List<AsAlarmVariableInfoRequest> asAlarmVariableInfoRequestList;

    /**
     * 预警通知方式表相关信息
     */
    private List<AsAlarmNotifyInfoRequest> asAlarmNotifyInfoRequestList;

    /**
     * 预警通知消息
     */
    private List<AsAlarmMsgInfoRequest> asAlarmNotifyMsgInfoRequestList;
    /**
     * 预警恢复消息
     */
    private List<AsAlarmMsgInfoRequest> asAlarmRecoveryMsgInfoRequestList;

    /**
     * 预警触发条件表相关信息
     */
    private List<AsAlarmTriggerInfoRequest> asAlarmTriggerInfoRequestList;

}
