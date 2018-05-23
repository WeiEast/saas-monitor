package com.treefinance.saas.monitor.common.domain.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.utils.BeanUtils;

import java.io.Serializable;
import java.util.*;

import static com.treefinance.saas.monitor.common.constants.AlarmConstants.SWITCH_ON;

/**
 * @Author: chengtong
 * @Date: 18/3/9 16:36
 */
public class EmailMonitorAlarmConfigDTO implements Serializable {

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
     * 任务超时时间
     */
    private Integer taskTimeoutSecs;
    /**
     * 此appId是否预警,方便以后开关
     */
    private String alarmSwitch;
    /**
     * 预警类型:按人统计预警为1,按任务统计为0
     */
    private Integer alarmType;
    /**
     * 预警类型描述：
     */
    private String alarmTypeDesc;
    /**
     * 分区时间段 表示的是统计的时间段
     */
    private Integer intervalMins;

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
    private List<EmailMonitorAlarmLevelConfigDTO> levelConfig;


    /* ======= switch ========*/
    /**
     * 开关
     */
    private HashMap<String, String> switches;

    /* ===========  time config ============= */
    /**
     * 不同时间段 不同阈值的配置
     */
    private List<EmailMonitorAlarmTimeConfigDTO> list;

    public Integer getFewNum() {
        return fewNum;
    }

    public void setFewNum(Integer fewNum) {
        this.fewNum = fewNum;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getTaskTimeoutSecs() {
        return taskTimeoutSecs;
    }

    public void setTaskTimeoutSecs(Integer taskTimeoutSecs) {
        this.taskTimeoutSecs = taskTimeoutSecs;
    }

    public String getAlarmSwitch() {
        return alarmSwitch;
    }

    public void setAlarmSwitch(String alarmSwitch) {
        this.alarmSwitch = alarmSwitch;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmTypeDesc() {
        return alarmTypeDesc;
    }

    public void setAlarmTypeDesc(String alarmTypeDesc) {
        this.alarmTypeDesc = alarmTypeDesc;
    }

    public Integer getIntervalMins() {
        return intervalMins;
    }

    public void setIntervalMins(Integer intervalMins) {
        this.intervalMins = intervalMins;
    }

    public Integer getPreviousDays() {
        return previousDays;
    }

    public void setPreviousDays(Integer previousDays) {
        this.previousDays = previousDays;
    }

    public List<EmailMonitorAlarmTimeConfigDTO> getList() {
        return list;
    }

    public void setList(List<EmailMonitorAlarmTimeConfigDTO> list) {
        this.list = list;
    }

    public List<EmailMonitorAlarmLevelConfigDTO> getLevelConfig() {
        return levelConfig;
    }

    public void setLevelConfig(List<EmailMonitorAlarmLevelConfigDTO> levelConfig) {
        this.levelConfig = levelConfig;
    }

    public HashMap<String, String> getSwitches() {
        return switches;
    }

    public void setSwitches(HashMap<String, String> switches) {
        this.switches = switches;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public static void main(String... args) {
        EmailMonitorAlarmConfigDTO emailMonitorAlarmConfigDTO = new EmailMonitorAlarmConfigDTO();
        emailMonitorAlarmConfigDTO.alarmSwitch = AlarmConstants.SWITCH_ON;
        emailMonitorAlarmConfigDTO.alarmType = 1;
        emailMonitorAlarmConfigDTO.alarmTypeDesc = "所有邮箱所有商户按人数统计预警";
        emailMonitorAlarmConfigDTO.appName = "所有商户";
        emailMonitorAlarmConfigDTO.intervalMins = 30;
        emailMonitorAlarmConfigDTO.previousDays = 7;
        emailMonitorAlarmConfigDTO.taskTimeoutSecs = 600;
        emailMonitorAlarmConfigDTO.appId = "virtual_total_stat_appId";
        emailMonitorAlarmConfigDTO.threshold = 20;
        emailMonitorAlarmConfigDTO.fewNum = 5;

        HashMap<String, String> map = Maps.newHashMap();
        map.put(EAlarmChannel.IVR.getValue(), SWITCH_ON);
        map.put(EAlarmChannel.SMS.getValue(), SWITCH_ON);
        map.put(EAlarmChannel.EMAIL.getValue(), SWITCH_ON);
        map.put(EAlarmChannel.WECHAT.getValue(), SWITCH_ON);

        emailMonitorAlarmConfigDTO.switches = map;

        emailMonitorAlarmConfigDTO.setEmails(Arrays.asList(AlarmConstants.ALL_EMAIL));

        EmailMonitorAlarmTimeConfigDTO timeConfigDTO = new EmailMonitorAlarmTimeConfigDTO();
        timeConfigDTO.setCallbackSuccessRate(70);
        timeConfigDTO.setLoginConversionRate(70);
        timeConfigDTO.setLoginSuccessRate(70);
        timeConfigDTO.setProcessSuccessRate(70);
        timeConfigDTO.setWholeConversionRate(70);
        timeConfigDTO.setCrawlSuccessRate(70);
        timeConfigDTO.setEndTime("23:59:59");
        timeConfigDTO.setStartTime("18:00:00");

        EmailMonitorAlarmTimeConfigDTO timeConfigDTO1 = new EmailMonitorAlarmTimeConfigDTO();
        timeConfigDTO1.setCallbackSuccessRate(70);
        timeConfigDTO1.setLoginConversionRate(70);
        timeConfigDTO1.setLoginSuccessRate(70);
        timeConfigDTO1.setProcessSuccessRate(70);
        timeConfigDTO1.setWholeConversionRate(70);
        timeConfigDTO1.setCrawlSuccessRate(70);
        timeConfigDTO1.setEndTime("06:00:00");
        timeConfigDTO1.setStartTime("00:00:00");

        EmailMonitorAlarmTimeConfigDTO timeConfigDTO2 = new EmailMonitorAlarmTimeConfigDTO();
        timeConfigDTO2.setCallbackSuccessRate(90);
        timeConfigDTO2.setLoginConversionRate(90);
        timeConfigDTO2.setLoginSuccessRate(90);
        timeConfigDTO2.setProcessSuccessRate(90);
        timeConfigDTO2.setWholeConversionRate(90);
        timeConfigDTO2.setCrawlSuccessRate(90);
        timeConfigDTO2.setEndTime("18:00:00");
        timeConfigDTO2.setStartTime("06:00:00");

        List<EmailMonitorAlarmTimeConfigDTO> list = new ArrayList<>();
        list.add(timeConfigDTO);
        list.add(timeConfigDTO1);
        list.add(timeConfigDTO2);

        emailMonitorAlarmConfigDTO.setList(list);

        EmailMonitorAlarmLevelConfigDTO errorConfig = new EmailMonitorAlarmLevelConfigDTO();
        errorConfig.setChannels(Arrays.asList("email", "ivr", "wechat"));
        errorConfig.setLevel(EAlarmLevel.error.name());

        EmailMonitorAlarmLevelConfigDTO warning = new EmailMonitorAlarmLevelConfigDTO();
        warning.setChannels(Arrays.asList("email", "sms", "wechat"));
        warning.setLevel(EAlarmLevel.warning.name());

        EmailMonitorAlarmLevelConfigDTO info = new EmailMonitorAlarmLevelConfigDTO();
        info.setChannels(Arrays.asList("email", "wechat"));
        info.setLevel(EAlarmLevel.info.name());

        emailMonitorAlarmConfigDTO.setLevelConfig(Arrays.asList(errorConfig, warning, info));
        EmailMonitorAlarmConfigDTO configDTO = new EmailMonitorAlarmConfigDTO();
        configDTO = BeanUtils.convert(emailMonitorAlarmConfigDTO,
                configDTO);

        configDTO.setEmails(Arrays.asList("163.com","126.com","139.com"));
        configDTO.setLevelConfig(emailMonitorAlarmConfigDTO.levelConfig);
        configDTO.setList(emailMonitorAlarmConfigDTO.list);
        configDTO.setSwitches(emailMonitorAlarmConfigDTO.switches);

        System.err.println(JSON.toJSONString(Arrays.asList(configDTO),
                SerializerFeature.DisableCircularReferenceDetect));
    }


}
