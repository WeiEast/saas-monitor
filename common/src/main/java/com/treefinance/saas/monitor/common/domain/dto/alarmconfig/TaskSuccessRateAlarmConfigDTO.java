package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.treefinance.saas.monitor.common.constants.AlarmConstants.SWITCH_ON;

/**
 * Created by haojiahong on 2017/11/24.
 */
public class TaskSuccessRateAlarmConfigDTO extends BaseAlarmConfigDTO {

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
     * 低于阀值多少次预警,3次,5次等
     */
    private Integer times;


    /**对不同预警级别的预警渠道配置*/
    private List<MonitorAlarmLevelConfigDTO> levelConfig;

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

        System.out.println(JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect));
    }

    private static TaskSuccessRateAlarmConfigDTO getTaskSuccessRateAlarmConfigDTO(ESaasEnv saasEnv) {
        TaskSuccessRateAlarmConfigDTO dto1 = new TaskSuccessRateAlarmConfigDTO();
        dto1.setAlarmSwitch(SWITCH_ON);
        dto1.setSaasEnv((byte)saasEnv.getValue());
        dto1.setSaasEnvDesc(saasEnv.getDesc());
        dto1.setType("OPERATOR");
        dto1.setIntervalMins(5);
        dto1.setTimes(3);
        dto1.setTaskTimeoutSecs(600);
        dto1.setSuccesThreshold(40);

        List<TaskSuccRateAlarmTimeListDTO> listDTOS = Lists.newArrayList();

        TaskSuccRateAlarmTimeListDTO timeListDTO = new TaskSuccRateAlarmTimeListDTO();
        timeListDTO.setEndTime("23:59:59");
        timeListDTO.setStartTime("19:00:00");
        timeListDTO.setThresholdError(new BigDecimal("70"));
        timeListDTO.setThresholdWarning(new BigDecimal("80"));
        timeListDTO.setThresholdInfo(new BigDecimal("90"));
        HashMap<String,String> mapEvening = new HashMap<>(4);
        mapEvening.put(EAlarmChannel.IVR.getValue(), SWITCH_ON);
        mapEvening.put(EAlarmChannel.EMAIL.getValue(), SWITCH_ON);
        mapEvening.put(EAlarmChannel.SMS.getValue(), SWITCH_ON);
        mapEvening.put(EAlarmChannel.WECHAT.getValue(), SWITCH_ON);
        timeListDTO.setSwitches(mapEvening);

        TaskSuccRateAlarmTimeListDTO timeListDTO1 = new TaskSuccRateAlarmTimeListDTO();
        timeListDTO1.setEndTime("06:00:00");
        timeListDTO1.setStartTime("00:00:00");
        timeListDTO1.setThresholdError(new BigDecimal("70"));
        timeListDTO1.setThresholdWarning(new BigDecimal("80"));
        timeListDTO1.setThresholdInfo(new BigDecimal("90"));

        timeListDTO1.setSwitches(mapEvening);

        TaskSuccRateAlarmTimeListDTO timeListDTO2 = new TaskSuccRateAlarmTimeListDTO();
        timeListDTO2.setEndTime("19:00:00");
        timeListDTO2.setStartTime("06:00:00");
        timeListDTO2.setThresholdError(new BigDecimal("70"));
        timeListDTO2.setThresholdWarning(new BigDecimal("80"));
        timeListDTO2.setThresholdInfo(new BigDecimal("90"));
        HashMap<String,String> mapDay = new HashMap<>(4);
        mapDay.put(EAlarmChannel.IVR.getValue(), AlarmConstants.SWITCH_OFF);
        mapDay.put(EAlarmChannel.EMAIL.getValue(), SWITCH_ON);
        mapDay.put(EAlarmChannel.SMS.getValue(), SWITCH_ON);
        mapDay.put(EAlarmChannel.WECHAT.getValue(), SWITCH_ON);
        timeListDTO2.setSwitches(mapDay);

        listDTOS.add(timeListDTO);
        listDTOS.add(timeListDTO1);
        listDTOS.add(timeListDTO2);

        dto1.setTimeConfig(listDTOS);

        MonitorAlarmLevelConfigDTO errorConfig = new MonitorAlarmLevelConfigDTO();
        errorConfig.setLevel(EAlarmLevel.error.name());
        errorConfig.setChannels(Arrays.asList(EAlarmChannel.IVR.getValue(),EAlarmChannel.EMAIL.getValue(),
                EAlarmChannel.WECHAT.getValue()));
        MonitorAlarmLevelConfigDTO warnConfig = new MonitorAlarmLevelConfigDTO();
        warnConfig.setLevel(EAlarmLevel.warning.name());
        warnConfig.setChannels(Arrays.asList(EAlarmChannel.SMS.getValue(),EAlarmChannel.EMAIL.getValue(),
                EAlarmChannel.WECHAT.getValue()));
        MonitorAlarmLevelConfigDTO infoConfig = new MonitorAlarmLevelConfigDTO();
        infoConfig.setLevel(EAlarmLevel.info.name());
        infoConfig.setChannels(Arrays.asList(EAlarmChannel.EMAIL.getValue(),
                EAlarmChannel.WECHAT.getValue()));

        dto1.setLevelConfig(Arrays.asList(errorConfig,warnConfig,infoConfig));

        return dto1;
    }

    public List<MonitorAlarmLevelConfigDTO> getLevelConfig() {
        return levelConfig;
    }

    public void setLevelConfig(List<MonitorAlarmLevelConfigDTO> levelConfig) {
        this.levelConfig = levelConfig;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
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
