package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.treefinance.saas.monitor.common.constants.AlarmConstants.SWITCH_ON;

/**
 *
 * @author haojiahong
 * @date 2017/11/24
 */
@Setter
@Getter
@ToString
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

    /**用于生成线上的配置数据*/
    public static void main(String[] args) {
        List<TaskSuccessRateAlarmConfigDTO> list = Lists.newArrayList();
        TaskSuccessRateAlarmConfigDTO operator = getTaskSuccessRateAlarmConfigDTO(ESaasEnv.ALL,"OPERATOR");
        TaskSuccessRateAlarmConfigDTO ecommerce = getTaskSuccessRateAlarmConfigDTO(ESaasEnv.ALL,"ECOMMERCE");

        list.add(operator);
        list.add(ecommerce);

        System.out.println(JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect));
    }

    private static TaskSuccessRateAlarmConfigDTO getTaskSuccessRateAlarmConfigDTO(ESaasEnv saasEnv,String type) {
        TaskSuccessRateAlarmConfigDTO dto1 = new TaskSuccessRateAlarmConfigDTO();
        dto1.setAlarmSwitch(SWITCH_ON);
        dto1.setSaasEnv((byte)saasEnv.getValue());
        dto1.setSaasEnvDesc(saasEnv.getDesc());
        dto1.setType(type);
        dto1.setIntervalMins(5);
        dto1.setTimes(3);
        dto1.setTaskTimeoutSecs(600);
        dto1.setSuccesThreshold(40);

        HashMap<String,String> mapDay = new HashMap<>(4);
        mapDay.put(EAlarmChannel.IVR.getValue(), SWITCH_ON);
        mapDay.put(EAlarmChannel.EMAIL.getValue(), SWITCH_ON);
        mapDay.put(EAlarmChannel.SMS.getValue(), SWITCH_ON);
        mapDay.put(EAlarmChannel.WECHAT.getValue(), SWITCH_ON);

        HashMap<String,String> mapEvening = new HashMap<>(4);
        mapEvening.put(EAlarmChannel.IVR.getValue(), AlarmConstants.SWITCH_OFF);
        mapEvening.put(EAlarmChannel.EMAIL.getValue(), SWITCH_ON);
        mapEvening.put(EAlarmChannel.SMS.getValue(), SWITCH_ON);
        mapEvening.put(EAlarmChannel.WECHAT.getValue(), SWITCH_ON);

        List<TaskSuccRateAlarmTimeListDTO> listDTOS = Lists.newArrayList();

        TaskSuccRateAlarmTimeListDTO timeListDTO = getTaskSuccRateAlarmTimeListDTO( "19:00:00","23:59:59", "70", "80", "90",mapDay);

        TaskSuccRateAlarmTimeListDTO timeListDTO1 = getTaskSuccRateAlarmTimeListDTO( "00:00:00", "06:00:00","50", "70", "80",mapEvening);
        timeListDTO1.setIntervals(10);
        TaskSuccRateAlarmTimeListDTO timeListDTO2 = getTaskSuccRateAlarmTimeListDTO("06:00:00","19:00:00",  "70", "80", "90",mapDay);


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

    private static TaskSuccRateAlarmTimeListDTO getTaskSuccRateAlarmTimeListDTO(String startTime,String endTime, String error, String warn, String info,HashMap switches) {
        TaskSuccRateAlarmTimeListDTO timeListDTO1 = new TaskSuccRateAlarmTimeListDTO();
        timeListDTO1.setEndTime(endTime);
        timeListDTO1.setStartTime(startTime);
        timeListDTO1.setThresholdError(new BigDecimal(error));
        timeListDTO1.setThresholdWarning(new BigDecimal(warn));
        timeListDTO1.setThresholdInfo(new BigDecimal(info));
        timeListDTO1.setSwitches(switches);
        return timeListDTO1;
    }

}
