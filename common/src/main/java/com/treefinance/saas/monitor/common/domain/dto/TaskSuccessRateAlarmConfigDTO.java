package com.treefinance.saas.monitor.common.domain.dto;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.constants.MonitorConstants;
import com.treefinance.saas.monitor.common.domain.Constants;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created by haojiahong on 2017/11/24.
 */
public class TaskSuccessRateAlarmConfigDTO implements Serializable {

    private static final long serialVersionUID = 7938223164128700307L;

    /**
     * saas环境
     */
    private Byte saasEnv;

    /**
     * saas环境描述
     */
    private String saasEnvDesc;
    /**
     * 任务业务类型
     */
    private String type;

    private int succesThreshold;

    /**
     * 预警间隔时间,5分钟,10分钟等
     */
    private Integer intervalMins;
    /**
     * 低于阀值多少次预警,3次,5次等
     */
    private Integer times;

    /**
     * 任务超时时间
     */
    private Integer taskTimeoutSecs;

    /**集成的开关配置*/
    private HashMap<String, String> switches;

    /**
     * 时间段配置
     * */
    private List<TaskSuccRateAlarmTimeListDTO> timeConfig;


    public static void main(String[] args) {
        List<TaskSuccessRateAlarmConfigDTO> list = Lists.newArrayList();
        TaskSuccessRateAlarmConfigDTO dto1 = getTaskSuccessRateAlarmConfigDTO(ESaasEnv.PRODUCT);
        TaskSuccessRateAlarmConfigDTO dto2 = getTaskSuccessRateAlarmConfigDTO(ESaasEnv.PRE_PRODUCT);
        TaskSuccessRateAlarmConfigDTO dto3 = getTaskSuccessRateAlarmConfigDTO(ESaasEnv.ALL);

        list.add(dto1);
        list.add(dto2);
        list.add(dto3);

        System.out.println(JSON.toJSONString(list));
    }

    private static TaskSuccessRateAlarmConfigDTO getTaskSuccessRateAlarmConfigDTO(ESaasEnv saasEnv) {
        TaskSuccessRateAlarmConfigDTO dto1 = new TaskSuccessRateAlarmConfigDTO();
        dto1.setSaasEnv((byte)saasEnv.getValue());
        dto1.setSaasEnvDesc(saasEnv.getDesc());
        dto1.setType("OPERATOR");
        dto1.setIntervalMins(3);
        dto1.setTimes(3);
        dto1.setTaskTimeoutSecs(600);
        dto1.setSuccesThreshold(40);

        HashMap<String,String> map = new HashMap<>();
        map.put(EAlarmChannel.IVR.getValue(), AlarmConstants.SWITCH_ON);
        map.put(EAlarmChannel.EMAIL.getValue(),AlarmConstants.SWITCH_ON);
        map.put(EAlarmChannel.SMS.getValue(),AlarmConstants.SWITCH_ON);
        map.put(EAlarmChannel.WECHAT.getValue(),AlarmConstants.SWITCH_ON);

        dto1.setSwitches(map);

        List<TaskSuccRateAlarmTimeListDTO> listDTOS = Lists.newArrayList();

        TaskSuccRateAlarmTimeListDTO timeListDTO = new TaskSuccRateAlarmTimeListDTO();
        timeListDTO.setEndTime("23:59:59");
        timeListDTO.setStartTime("19:00:00");
        timeListDTO.setThresholdError(new BigDecimal("70"));
        timeListDTO.setThresholdWarning(new BigDecimal("80"));
        timeListDTO.setThresholdInfo(new BigDecimal("90"));

        TaskSuccRateAlarmTimeListDTO timeListDTO1 = new TaskSuccRateAlarmTimeListDTO();
        timeListDTO1.setEndTime("06:00:00");
        timeListDTO1.setStartTime("00:00:00");
        timeListDTO1.setThresholdError(new BigDecimal("70"));
        timeListDTO1.setThresholdWarning(new BigDecimal("80"));
        timeListDTO1.setThresholdInfo(new BigDecimal("90"));

        TaskSuccRateAlarmTimeListDTO timeListDTO2 = new TaskSuccRateAlarmTimeListDTO();
        timeListDTO2.setEndTime("19:00:00");
        timeListDTO2.setStartTime("06:00:00");
        timeListDTO2.setThresholdError(new BigDecimal("70"));
        timeListDTO2.setThresholdWarning(new BigDecimal("80"));
        timeListDTO2.setThresholdInfo(new BigDecimal("90"));


        listDTOS.add(timeListDTO);
        listDTOS.add(timeListDTO1);
        listDTOS.add(timeListDTO2);

        dto1.setTimeConfig(listDTOS);
        return dto1;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIntervalMins() {
        return intervalMins;
    }

    public void setIntervalMins(Integer intervalMins) {
        this.intervalMins = intervalMins;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Integer getTaskTimeoutSecs() {
        return taskTimeoutSecs;
    }

    public void setTaskTimeoutSecs(Integer taskTimeoutSecs) {
        this.taskTimeoutSecs = taskTimeoutSecs;
    }

    public Byte getSaasEnv() {
        return saasEnv;
    }

    public void setSaasEnv(Byte saasEnv) {
        this.saasEnv = saasEnv;
    }

    public String getSaasEnvDesc() {
        return saasEnvDesc;
    }

    public void setSaasEnvDesc(String saasEnvDesc) {
        this.saasEnvDesc = saasEnvDesc;
    }

    public HashMap<String, String> getSwitches() {
        return switches;
    }

    public void setSwitches(HashMap<String, String> switches) {
        this.switches = switches;
    }

    public List<TaskSuccRateAlarmTimeListDTO> getTimeConfig() {
        return timeConfig;
    }

    public void setTimeConfig(List<TaskSuccRateAlarmTimeListDTO> timeConfig) {
        this.timeConfig = timeConfig;
    }

    public int getSuccesThreshold() {
        return succesThreshold;
    }

    public void setSuccesThreshold(int succesThreshold) {
        this.succesThreshold = succesThreshold;
    }
}
