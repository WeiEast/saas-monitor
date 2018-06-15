package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import com.treefinance.saas.monitor.common.utils.BeanUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

import static com.treefinance.saas.monitor.common.constants.AlarmConstants.SWITCH_ON;

/**
 * @Author: chengtong
 * @Date: 18/3/9 16:36
 */
@Setter
@Getter
@ToString
public class EmailMonitorAlarmConfigDTO extends BaseAlarmConfigDTO {

    static final long serialVersionUID = 42123131212L;

    /**
     * 默认是所有的appId
     * {@see com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper.VIRTUAL_TOTAL_STAT_APP_ID}
     */
    private String appId;

    /**
     * 默认是 所有商户
     */
    private String appName;

    /**
     * 预警类型:按人统计预警为1,按任务统计为0
     */
    private Integer alarmType;
    /**
     * 预警类型描述：
     */
    private String alarmTypeDesc;

    /**
     * 邮箱类型
     */
    private List<String> emails;

    /**
     * 之前几天的平均值
     */
    private Integer previousDays;
    /**
     * 数量少于多少忽略的值
     */
    private Integer fewNum;

    /**
     * 当<fewnum时 某一项目的比率的阈值
     */
    private Integer threshold;

    /* ============== time config ===============*/
    /**
     * 预警等级-预警渠道的配置
     */
    private List<MonitorAlarmLevelConfigDTO> levelConfig;

    /**
     * 不同时间段 不同阈值的配置
     */
    private List<EmailMonitorAlarmTimeConfigDTO> timeConfig;

    public static void main(String... args) {
        EmailMonitorAlarmConfigDTO configDTO = getGroupConfig(ESaasEnv.ALL);
//        EmailMonitorAlarmConfigDTO configDTO1 = getGroupConfig(ESaasEnv.PRODUCT);
//        EmailMonitorAlarmConfigDTO configDTO2 = getGroupConfig(ESaasEnv.PRE_PRODUCT);

        EmailMonitorAlarmConfigDTO configAllDTO = getAllConfig(ESaasEnv.ALL);
//        EmailMonitorAlarmConfigDTO configAllDTO1 = getAllConfig(ESaasEnv.PRODUCT);
//        EmailMonitorAlarmConfigDTO configAllDTO2 = getAllConfig(ESaasEnv.PRE_PRODUCT);


        System.err.println(JSON.toJSONString(Arrays.asList(configDTO,configAllDTO),
                SerializerFeature.DisableCircularReferenceDetect));
    }
    private static EmailMonitorAlarmConfigDTO getAllConfig(ESaasEnv eSaasEnv) {
        EmailMonitorAlarmConfigDTO emailMonitorAlarmConfigDTO = new EmailMonitorAlarmConfigDTO();
        emailMonitorAlarmConfigDTO.setSaasEnv((byte) eSaasEnv.getValue());
        emailMonitorAlarmConfigDTO.setSaasEnvDesc(eSaasEnv.getDesc());

        emailMonitorAlarmConfigDTO.setAlarmSwitch(AlarmConstants.SWITCH_ON);
        emailMonitorAlarmConfigDTO.alarmType = 1;
        emailMonitorAlarmConfigDTO.alarmTypeDesc = "所有商户邮箱大盘按人数统计预警";
        emailMonitorAlarmConfigDTO.appName = "所有商户";
        emailMonitorAlarmConfigDTO.setIntervalMins(30);
        emailMonitorAlarmConfigDTO.previousDays = 7;
        emailMonitorAlarmConfigDTO.setTaskTimeoutSecs(600);
        emailMonitorAlarmConfigDTO.appId = "virtual_total_stat_appId";
        emailMonitorAlarmConfigDTO.threshold = 20;
        emailMonitorAlarmConfigDTO.fewNum = 5;

        HashMap<String, String> map = Maps.newHashMap();
        map.put(EAlarmChannel.IVR.getValue(), SWITCH_ON);
        map.put(EAlarmChannel.SMS.getValue(), SWITCH_ON);
        map.put(EAlarmChannel.EMAIL.getValue(), SWITCH_ON);
        map.put(EAlarmChannel.WECHAT.getValue(), SWITCH_ON);


        emailMonitorAlarmConfigDTO.setEmails(Collections.singletonList(AlarmConstants.ALL_EMAIL));

        EmailMonitorAlarmTimeConfigDTO timeConfigDTO = new EmailMonitorAlarmTimeConfigDTO();
        timeConfigDTO.setCallbackSuccessRate(70);
        timeConfigDTO.setLoginConversionRate(70);
        timeConfigDTO.setLoginSuccessRate(70);
        timeConfigDTO.setProcessSuccessRate(70);
        timeConfigDTO.setWholeConversionRate(70);
        timeConfigDTO.setCrawlSuccessRate(70);
        timeConfigDTO.setEndTime("23:59:59");
        timeConfigDTO.setStartTime("18:00:00");
        timeConfigDTO.setSwitches(map);

        EmailMonitorAlarmTimeConfigDTO timeConfigDTO1 = new EmailMonitorAlarmTimeConfigDTO();
        timeConfigDTO1.setCallbackSuccessRate(70);
        timeConfigDTO1.setLoginConversionRate(70);
        timeConfigDTO1.setLoginSuccessRate(70);
        timeConfigDTO1.setProcessSuccessRate(70);
        timeConfigDTO1.setWholeConversionRate(70);
        timeConfigDTO1.setCrawlSuccessRate(70);
        timeConfigDTO1.setEndTime("06:00:00");
        timeConfigDTO1.setStartTime("00:00:00");
        timeConfigDTO1.setSwitches(map);

        EmailMonitorAlarmTimeConfigDTO timeConfigDTO2 = new EmailMonitorAlarmTimeConfigDTO();
        timeConfigDTO2.setCallbackSuccessRate(90);
        timeConfigDTO2.setLoginConversionRate(90);
        timeConfigDTO2.setLoginSuccessRate(90);
        timeConfigDTO2.setProcessSuccessRate(90);
        timeConfigDTO2.setWholeConversionRate(90);
        timeConfigDTO2.setCrawlSuccessRate(90);
        timeConfigDTO2.setEndTime("18:00:00");
        timeConfigDTO2.setStartTime("06:00:00");
        timeConfigDTO2.setSwitches(map);

        List<EmailMonitorAlarmTimeConfigDTO> list = new ArrayList<>();
        list.add(timeConfigDTO);
        list.add(timeConfigDTO1);
        list.add(timeConfigDTO2);

        emailMonitorAlarmConfigDTO.setTimeConfig(list);

        MonitorAlarmLevelConfigDTO errorConfig = new MonitorAlarmLevelConfigDTO();
        errorConfig.setChannels(Arrays.asList("email", "ivr", "wechat"));
        errorConfig.setLevel(EAlarmLevel.error.name());

        MonitorAlarmLevelConfigDTO warning = new MonitorAlarmLevelConfigDTO();
        warning.setChannels(Arrays.asList("email", "sms", "wechat"));
        warning.setLevel(EAlarmLevel.warning.name());

        MonitorAlarmLevelConfigDTO info = new MonitorAlarmLevelConfigDTO();
        info.setChannels(Arrays.asList("email", "wechat"));
        info.setLevel(EAlarmLevel.info.name());

        emailMonitorAlarmConfigDTO.setLevelConfig(Arrays.asList(errorConfig, warning, info));

        EmailMonitorAlarmConfigDTO configDTO = new EmailMonitorAlarmConfigDTO();
        configDTO = BeanUtils.convert(emailMonitorAlarmConfigDTO,
                configDTO);

        configDTO.setEmails(Arrays.asList("virtual_total_stat_email"));
        configDTO.setLevelConfig(emailMonitorAlarmConfigDTO.levelConfig);
        return configDTO;
    }
    private static EmailMonitorAlarmConfigDTO getGroupConfig(ESaasEnv eSaasEnv) {
        EmailMonitorAlarmConfigDTO emailMonitorAlarmConfigDTO = new EmailMonitorAlarmConfigDTO();
        emailMonitorAlarmConfigDTO.setSaasEnv((byte) eSaasEnv.getValue());
        emailMonitorAlarmConfigDTO.setSaasEnvDesc(eSaasEnv.getDesc());

        emailMonitorAlarmConfigDTO.setAlarmSwitch(AlarmConstants.SWITCH_ON);
        emailMonitorAlarmConfigDTO.alarmType = 1;
        emailMonitorAlarmConfigDTO.alarmTypeDesc = "所有商户邮箱分组按人数统计预警";
        emailMonitorAlarmConfigDTO.appName = "所有商户";
        emailMonitorAlarmConfigDTO.setIntervalMins(30);
        emailMonitorAlarmConfigDTO.previousDays = 7;
        emailMonitorAlarmConfigDTO.setTaskTimeoutSecs(600);
        emailMonitorAlarmConfigDTO.appId = "virtual_total_stat_appId";
        emailMonitorAlarmConfigDTO.threshold = 20;
        emailMonitorAlarmConfigDTO.fewNum = 5;

        HashMap<String, String> map = Maps.newHashMap();
        map.put(EAlarmChannel.IVR.getValue(), SWITCH_ON);
        map.put(EAlarmChannel.SMS.getValue(), SWITCH_ON);
        map.put(EAlarmChannel.EMAIL.getValue(), SWITCH_ON);
        map.put(EAlarmChannel.WECHAT.getValue(), SWITCH_ON);


        emailMonitorAlarmConfigDTO.setEmails(Collections.singletonList(AlarmConstants.ALL_EMAIL));

        EmailMonitorAlarmTimeConfigDTO timeConfigDTO = new EmailMonitorAlarmTimeConfigDTO();
        timeConfigDTO.setCallbackSuccessRate(70);
        timeConfigDTO.setLoginConversionRate(70);
        timeConfigDTO.setLoginSuccessRate(70);
        timeConfigDTO.setProcessSuccessRate(70);
        timeConfigDTO.setWholeConversionRate(70);
        timeConfigDTO.setCrawlSuccessRate(70);
        timeConfigDTO.setEndTime("23:59:59");
        timeConfigDTO.setStartTime("18:00:00");
        timeConfigDTO.setSwitches(map);

        EmailMonitorAlarmTimeConfigDTO timeConfigDTO1 = new EmailMonitorAlarmTimeConfigDTO();
        timeConfigDTO1.setCallbackSuccessRate(70);
        timeConfigDTO1.setLoginConversionRate(70);
        timeConfigDTO1.setLoginSuccessRate(70);
        timeConfigDTO1.setProcessSuccessRate(70);
        timeConfigDTO1.setWholeConversionRate(70);
        timeConfigDTO1.setCrawlSuccessRate(70);
        timeConfigDTO1.setEndTime("06:00:00");
        timeConfigDTO1.setStartTime("00:00:00");
        timeConfigDTO1.setSwitches(map);

        EmailMonitorAlarmTimeConfigDTO timeConfigDTO2 = new EmailMonitorAlarmTimeConfigDTO();
        timeConfigDTO2.setCallbackSuccessRate(90);
        timeConfigDTO2.setLoginConversionRate(90);
        timeConfigDTO2.setLoginSuccessRate(90);
        timeConfigDTO2.setProcessSuccessRate(90);
        timeConfigDTO2.setWholeConversionRate(90);
        timeConfigDTO2.setCrawlSuccessRate(90);
        timeConfigDTO2.setEndTime("18:00:00");
        timeConfigDTO2.setStartTime("06:00:00");
        timeConfigDTO2.setSwitches(map);

        List<EmailMonitorAlarmTimeConfigDTO> list = new ArrayList<>();
        list.add(timeConfigDTO);
        list.add(timeConfigDTO1);
        list.add(timeConfigDTO2);

        emailMonitorAlarmConfigDTO.setTimeConfig(list);

        MonitorAlarmLevelConfigDTO errorConfig = new MonitorAlarmLevelConfigDTO();
        errorConfig.setChannels(Arrays.asList("email", "ivr", "wechat"));
        errorConfig.setLevel(EAlarmLevel.error.name());

        MonitorAlarmLevelConfigDTO warning = new MonitorAlarmLevelConfigDTO();
        warning.setChannels(Arrays.asList("email", "sms", "wechat"));
        warning.setLevel(EAlarmLevel.warning.name());

        MonitorAlarmLevelConfigDTO info = new MonitorAlarmLevelConfigDTO();
        info.setChannels(Arrays.asList("email", "wechat"));
        info.setLevel(EAlarmLevel.info.name());

        emailMonitorAlarmConfigDTO.setLevelConfig(Arrays.asList(errorConfig, warning, info));

        EmailMonitorAlarmConfigDTO configDTO = new EmailMonitorAlarmConfigDTO();
        configDTO = BeanUtils.convert(emailMonitorAlarmConfigDTO,
                configDTO);

        configDTO.setEmails(Arrays.asList("126.com","163.com","139.com","exmail.qq.com","qq.com","sina.com","其他"));
        configDTO.setLevelConfig(emailMonitorAlarmConfigDTO.levelConfig);
        return configDTO;
    }


}
