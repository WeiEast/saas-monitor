package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haojiahong on 2017/12/5.
 */

@Setter
@Getter
@ToString
public class OperatorMonitorAlarmConfigDTO extends BaseAlarmConfigDTO {

    private static final long serialVersionUID = 4249280455861844218L;

    private String appId;
    private String appName;

    /**
     * saas环境: 0:所有环境 1:生产环境 2:预发布环境
     */
    private Byte saasEnv;
    private String saasEnvDesc;

    /**
     * 数据类型: 0:按任务 1:按人数
     */
    private Byte dataType;
    private String dataTypeDesc;

    /**
     * 预警维度: 1:总运营商 2:分运营商
     */
    private Integer alarmType;
    private String alarmTypeDesc;

    private Integer previousDays;

    private Integer confirmMobileConversionRate;
    /**
     * 登录转化率
     */
    private Integer loginConversionRate;

    private Integer loginSuccessRate;

    private Integer crawlSuccessRate;

    private Integer processSuccessRate;

    private Integer callbackSuccessRate;

    private Integer wholeConversionRate;

    private String mailAlarmSwitch;
    private String weChatAlarmSwitch;

    public static void main(String[] args) {
        List<OperatorMonitorAlarmConfigDTO> list = Lists.newArrayList();
        OperatorMonitorAlarmConfigDTO dto1 = new OperatorMonitorAlarmConfigDTO();
        dto1.setAppId("virtual_total_stat_appId");
        dto1.setAppName("所有商户");
        dto1.setAlarmType(1);
        dto1.setAlarmTypeDesc("总运营商");
        dto1.setSaasEnv((byte) 0);
        dto1.setSaasEnvDesc("所有环境");
        dto1.setDataType((byte) 1);
        dto1.setDataTypeDesc("按人数");
        dto1.setIntervalMins(30);
        dto1.setTaskTimeoutSecs(600);
        dto1.setAlarmSwitch(AlarmConstants.SWITCH_ON);
        dto1.setPreviousDays(7);
        dto1.setConfirmMobileConversionRate(70);
        dto1.setLoginConversionRate(70);
        dto1.setLoginSuccessRate(70);
        dto1.setCrawlSuccessRate(70);
        dto1.setProcessSuccessRate(70);
        dto1.setCallbackSuccessRate(70);
        dto1.setWholeConversionRate(90);
        dto1.setMailAlarmSwitch(AlarmConstants.SWITCH_ON);
        dto1.setWeChatAlarmSwitch(AlarmConstants.SWITCH_ON);
        list.add(dto1);

        OperatorMonitorAlarmConfigDTO dto2 = new OperatorMonitorAlarmConfigDTO();
        dto2.setAppId("virtual_total_stat_appId");
        dto2.setAppName("所有商户");
        dto2.setAlarmType(2);
        dto2.setAlarmTypeDesc("分运营商");
        dto2.setSaasEnv((byte) 0);
        dto2.setSaasEnvDesc("所有环境");
        dto2.setDataType((byte) 1);
        dto2.setDataTypeDesc("按人数");
        dto2.setIntervalMins(30);
        dto2.setTaskTimeoutSecs(600);
        dto2.setAlarmSwitch(AlarmConstants.SWITCH_ON);
        dto2.setPreviousDays(7);
        dto2.setConfirmMobileConversionRate(70);
        dto2.setLoginConversionRate(70);
        dto2.setLoginSuccessRate(70);
        dto2.setCrawlSuccessRate(70);
        dto2.setProcessSuccessRate(70);
        dto2.setCallbackSuccessRate(70);
        dto2.setMailAlarmSwitch(AlarmConstants.SWITCH_ON);
        dto2.setWeChatAlarmSwitch(AlarmConstants.SWITCH_ON);
        list.add(dto2);
        System.out.println(JSON.toJSONString(list));

    }

}
