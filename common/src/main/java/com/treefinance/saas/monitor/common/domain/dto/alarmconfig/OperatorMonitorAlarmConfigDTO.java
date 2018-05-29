package com.treefinance.saas.monitor.common.domain.dto.alarmconfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.treefinance.saas.monitor.common.constants.AlarmConstants.SWITCH_ON;

/**
 * @author haojiahong
 * @date 2017/12/5
 */
@Setter
@Getter
@ToString
public class OperatorMonitorAlarmConfigDTO extends BaseAlarmConfigDTO {

    private String appId;
    private String appName;

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

    /**
     * 对不同预警级别的预警渠道配置
     */
    private List<MonitorAlarmLevelConfigDTO> levelConfig;

    private List<OperatorAlarmTimeConfigDTO> timeConfig;

    public OperatorAlarmTimeConfigDTO getInTimeTimeConfig(){
        for(OperatorAlarmTimeConfigDTO configDTO:timeConfig){
            if(configDTO.isInTime()){
                return configDTO;
            }
        }
        return null;
    }



    public static void main(String[] args) {
        List<OperatorMonitorAlarmConfigDTO> list = Lists.newArrayList();
        OperatorMonitorAlarmConfigDTO dto = getAllOperatorConfig(ESaasEnv.ALL,AlarmConstants.ALL_OPERATOR,"总运营商");
        OperatorMonitorAlarmConfigDTO dto1 = getAllOperatorConfig(ESaasEnv.PRODUCT,AlarmConstants.ALL_OPERATOR,"总运营商");
        OperatorMonitorAlarmConfigDTO dto2 = getAllOperatorConfig(ESaasEnv.PRE_PRODUCT,AlarmConstants.ALL_OPERATOR,"总运营商");

        list.add(dto);
        list.add(dto1);
        list.add(dto2);

        OperatorMonitorAlarmConfigDTO dto3 = getAllOperatorConfig(ESaasEnv.ALL,AlarmConstants.GROUP_OPERATOR,"分运营商");
        OperatorMonitorAlarmConfigDTO dto4 = getAllOperatorConfig(ESaasEnv.PRODUCT,AlarmConstants.GROUP_OPERATOR,
                "分运营商");
        OperatorMonitorAlarmConfigDTO dto5 = getAllOperatorConfig(ESaasEnv.PRE_PRODUCT,AlarmConstants.GROUP_OPERATOR,
                "分运营商");

        list.add(dto3);
        list.add(dto4);
        list.add(dto5);

        System.out.println(JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect));

    }

    private static OperatorMonitorAlarmConfigDTO getAllOperatorConfig(ESaasEnv eSaasEnv,Integer alarmType,String
            typeDesc) {
        OperatorMonitorAlarmConfigDTO dto1 = new OperatorMonitorAlarmConfigDTO();
        dto1.setAppId("virtual_total_stat_appId");
        dto1.setAppName("所有商户");
        dto1.setAlarmType(alarmType);
        dto1.setAlarmTypeDesc(typeDesc);
        dto1.setSaasEnv((byte)eSaasEnv.getValue());
        dto1.setSaasEnvDesc(eSaasEnv.getDesc());
        dto1.setDataType((byte)1);
        dto1.setDataTypeDesc("按人数");
        dto1.setIntervalMins(30);
        dto1.setTaskTimeoutSecs(600);
        dto1.setAlarmSwitch(AlarmConstants.SWITCH_ON);
        dto1.setPreviousDays(7);

        HashMap<String, String> mapEvening = new HashMap<>(4);
        mapEvening.put(EAlarmChannel.IVR.getValue(), SWITCH_ON);
        mapEvening.put(EAlarmChannel.EMAIL.getValue(), SWITCH_ON);
        mapEvening.put(EAlarmChannel.SMS.getValue(), SWITCH_ON);
        mapEvening.put(EAlarmChannel.WECHAT.getValue(), SWITCH_ON);

        List<OperatorAlarmTimeConfigDTO> timeConfigs = new ArrayList<>();

        OperatorAlarmTimeConfigDTO timeListDTO = getOperatorAlarmTimeConfigDTO("00:00:00","06:00:00",mapEvening);
        OperatorAlarmTimeConfigDTO timeListDTO1 = getOperatorAlarmTimeConfigDTO("06:00:00","19:00:00",mapEvening);
        OperatorAlarmTimeConfigDTO timeListDTO2 = getOperatorAlarmTimeConfigDTO("19:00:00","23:59:59",mapEvening);

        timeConfigs.add(timeListDTO);
        timeConfigs.add(timeListDTO1);
        timeConfigs.add(timeListDTO2);

        dto1.setTimeConfig(timeConfigs);

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

    private static OperatorAlarmTimeConfigDTO getOperatorAlarmTimeConfigDTO(String startTime, String
            endTime, HashMap<String, String> mapEvening) {
        OperatorAlarmTimeConfigDTO timeListDTO = new OperatorAlarmTimeConfigDTO();
        timeListDTO.setEndTime(endTime);
        timeListDTO.setStartTime(startTime);
        timeListDTO.setLoginConversionRate(70);
        timeListDTO.setLoginSuccessRate(70);
        timeListDTO.setCrawlSuccessRate(70);
        timeListDTO.setProcessSuccessRate(70);
        timeListDTO.setConfirmMobileConversionRate(70);
        timeListDTO.setCallbackSuccessRate(70);
        timeListDTO.setWholeConversionRate(90);
        timeListDTO.setSwitches(mapEvening);
        return timeListDTO;
    }

}
