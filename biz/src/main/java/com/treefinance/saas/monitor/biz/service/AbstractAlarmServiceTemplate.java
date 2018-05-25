package com.treefinance.saas.monitor.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.common.domain.dto.BaseAlarmMsgDTO;
import com.treefinance.saas.monitor.common.domain.dto.BaseStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.BaseAlarmConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.EmailMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccess;
import com.treefinance.saas.monitor.dao.mapper.EmailStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.OperatorStatAccessMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 邮箱预警服务类的模板类
 *
 * @author chengtong
 * @date 18/3/12 11:20
 */
@Service
public abstract class AbstractAlarmServiceTemplate implements MonitorAlarmService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAlarmServiceTemplate.class);

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    @Autowired
    protected EmailStatAccessMapper emailStatAccessMapper;
    @Autowired
    protected OperatorStatAccessMapper operatorStatAccessMapper;


    @Autowired
    protected DiamondConfig diamondConfig;

    @Autowired
    protected SmsNotifyService smsNotifyService;
    @Autowired
    protected IvrNotifyService ivrNotifyService;
    @Autowired
    protected AlarmMessageProducer alarmMessageProducer;


    @Override
    public void alarm(Date now, BaseAlarmConfigDTO configDTO, ETaskStatDataType type) {

        //获取时间
        Date baseTime = getBaseTime(now, configDTO);

        //获取key
        String alarmTimeKey = getKey(type, baseTime, configDTO);

        //是否预警过?
        ifAlarmed(now, baseTime, alarmTimeKey, configDTO);

        //获取基础数据
        List<BaseStatAccessDTO> emailStatAccessDTOS = getBaseData(baseTime, type, configDTO);

        //获取平均值数据
        Map<String, BaseStatAccessDTO> compareMap = getPreviousCompareDataMap(baseTime, emailStatAccessDTOS,
                configDTO, type);

        //计算需要预警的信息
        List<BaseAlarmMsgDTO> msgList = getAlarmMsgList(now, emailStatAccessDTOS, compareMap, configDTO);

        //是否需要预警
        if (CollectionUtils.isEmpty(msgList)) {
            return;
        }

        //确定回调预警等级
        EAlarmLevel level = determineLevel(msgList);

        //构建回调内容 发送通知;
        sendAlarmMsg(level, msgList, configDTO, baseTime, type);
    }

    /**
     * @param msgList 预警数据
     * */
    protected abstract EAlarmLevel determineLevel(List<BaseAlarmMsgDTO> msgList) ;

    /**
     * 获取基础时间
     * <p>
     * 距离任务开始时间之前的timeout Seconds
     */
    private Date getBaseTime(Date jobTime, BaseAlarmConfigDTO config) {
        Date statTime = DateUtils.addSeconds(jobTime, -config.getTaskTimeoutSecs());
        //取得预警原点时间,如:statTime=14:01分,30分钟间隔统计一次,则beginTime为14:00.统计的数据间隔[13:30-13:40;13:40-13:50;13:50-14:00]
        return TaskOperatorMonitorKeyHelper.getRedisStatDateTime(statTime, config.getIntervalMins());
    }

    /**
     * redis的key值判断 是否继续
     *
     * @param now                预警时间
     * @param baseTime           预警数据时间
     * @param alarmTimeKey       预警的key
     * @param baseAlarmConfigDTO 配置
     */
    protected abstract void ifAlarmed(Date now, Date baseTime, String alarmTimeKey, BaseAlarmConfigDTO baseAlarmConfigDTO);


    /**
     * 获取基础数据 返回一段时间内的数据
     * 可以用基类作为返回的数据；兼容不同的业务线的数据机构
     * 使用email的数组 兼容分组的或者是大盘的邮箱监控
     *
     * @param baseTime       数据的endTime
     * @param statDataType   数据查询的类型
     * @param alarmConfigDTO 预警配置
     * @return 基础数据
     */
    protected abstract List<BaseStatAccessDTO> getBaseData(Date baseTime, ETaskStatDataType statDataType, BaseAlarmConfigDTO alarmConfigDTO);

    /**
     * 获取这个预警类型下的redis ke，不同的预警类型可以有不同的实现y
     *
     * @param type               统计类型
     * @param baseAlarmConfigDTO 预警配置
     * @param jobTime            预警时间
     * @return redis key
     */
    protected abstract String getKey(ETaskStatDataType type, Date jobTime, BaseAlarmConfigDTO baseAlarmConfigDTO);

    /**
     * 获取预警信息 的列表
     *
     * @param now        预警时间
     * @param dtoList    基础数据、源数据
     * @param compareMap 用于比较的map
     * @param configDTO  配置
     * @return 预警信息的列表
     */
    protected abstract List<BaseAlarmMsgDTO> getAlarmMsgList(Date now, List<BaseStatAccessDTO> dtoList, Map<String,
            BaseStatAccessDTO> compareMap, BaseAlarmConfigDTO configDTO);


    /**
     * 判断当前环节是否出发预警
     *
     * @param num         上一个环节的数量,分0-5,5-无穷的情况
     * @param rate        当前环节指标值
     * @param compareRate 当前环节阀值
     * @return 是否需要预警
     */
    protected Boolean isAlarm(Integer num, BigDecimal rate, BigDecimal compareRate, Integer fewNum, Integer threshold) {
        //20
        BigDecimal fewThresholdPercent = BigDecimal.valueOf(threshold);
        //上一个环节任务数量=0,不预警
        if (num == 0) {
            return false;
        }
        //统计数量较少,且大于设定的特殊阈值,不预警
        if (num > 0 && num < fewNum) {
            if (rate.compareTo(fewThresholdPercent) >= 0) {
                return false;
            }
        }
        //统计数量正常,且大于历史平均阈值,不预警
        if (num >= fewNum) {
            if (rate.compareTo(compareRate) >= 0) {
                return false;
            }
        }
        return true;
    }


    /**
     * 获取前7天内,相同时刻运营商统计的平均值(登录转化率平均值,抓取成功率平均值,洗数成功率平均值)
     *
     * @param baseTime           任务时间区间的结束时间
     * @param dtoList            基础数据的列表
     * @param statType           统计类型
     * @param baseAlarmConfigDTO 预警配置
     * @return Map 已不同（运营商、邮箱）区分的比较值的集合体；
     */
    protected abstract Map<String, BaseStatAccessDTO> getPreviousCompareDataMap(Date baseTime, List<BaseStatAccessDTO>
            dtoList, BaseAlarmConfigDTO baseAlarmConfigDTO, ETaskStatDataType statType);

    /**
     * 发送预警信息
     *
     * @param alarmLevel 预警等级
     * @param dtoList    预警信息
     * @param configDTO  配置
     * @param endTime    结束时间
     */
    protected abstract void sendAlarmMsg(EAlarmLevel alarmLevel, List<BaseAlarmMsgDTO> dtoList, BaseAlarmConfigDTO
            configDTO, Date endTime, ETaskStatDataType statDataType);

    protected void sendSms(List<BaseAlarmMsgDTO> msgList, Date startTime, Date endTime, EAlarmLevel alarmLevel, String alarmBiz) {

        String template = "${level} ${type} 时间段:${startTime}至${endTime},${alarmBiz} " +
                "预警类型:${alarmDesc},偏离阀值程度${offset}%";
        Map<String, Object> placeHolder = Maps.newHashMap();

        List<BaseAlarmMsgDTO> warningMsg = msgList.stream().filter(baseAlarmMsgDTO ->
                EAlarmLevel.warning.equals(baseAlarmMsgDTO.getAlarmLevel())).collect(Collectors.toList());

        String type = "SAAS-" + diamondConfig.getMonitorEnvironment() + "-" + alarmBiz;

        String format = "yyyy-MM-dd HH:mm:SS";
        String startTimeStr = new SimpleDateFormat(format).format(startTime);
        String endTimeStr = new SimpleDateFormat(format).format(endTime);

        BaseAlarmMsgDTO dto = warningMsg.get(0);

        placeHolder.put("level", alarmLevel.name());
        placeHolder.put("type", type);
        placeHolder.put("startTime", startTimeStr);
        placeHolder.put("endTime", endTimeStr);
        placeHolder.put("alarmBiz", alarmBiz);
        placeHolder.put("alarmDesc", dto.getAlarmDesc());
        placeHolder.put("offset", dto.getOffset());

        smsNotifyService.send(StrSubstitutor.replace(template, placeHolder));
    }

    protected void sendIvr(List<BaseAlarmMsgDTO> msgList, EAlarmLevel level, String alarmBiz) {

        List<BaseAlarmMsgDTO> errorMsgs = msgList.stream().filter(baseAlarmMsgDTO ->
                level.equals(baseAlarmMsgDTO.getAlarmLevel())).collect(Collectors.toList());

        logger.info(alarmBiz + "发送ivr请求 {}", errorMsgs.get(0).getAlarmDesc());

        ivrNotifyService.notifyIvr(level, EAlarmType.operator_alarm, alarmBiz + errorMsgs.get(0).getAlarmDesc());
    }

    protected void sendMail(List<BaseAlarmMsgDTO> msgList, Date jobTime, Date startTime, Date endTime,
                            EAlarmLevel alarmLevel, String alarmBiz) {

        String mailBaseTitle = "【${level}】【${module}】【${type}】发生 ${detail} 预警";

        Map<String, Object> map = new HashMap<>(4);
        map.put("type", alarmBiz);
        map.put("level", alarmLevel.name());

        String mailDataBody = generateMailDataBody(msgList, startTime, endTime, map, alarmLevel, alarmBiz);

        alarmMessageProducer.sendMail4OperatorMonitor(StrSubstitutor.replace(mailBaseTitle, map), mailDataBody, jobTime);
    }

    /**
     * 生成邮箱的内容方法
     * 不同的预警业务生成逻辑不同对此进行了封装，由具体的子类去实现
     *
     * @param alarmLevel  预警等级
     * @param endTime     结束时间
     * @param startTime   开始时间
     * @param placeHolder 参数映射类
     * @param msgList     预警信息
     * @param alarmBiz    预警业务信息
     * @return 邮件html
     */
    protected abstract String generateMailDataBody(List<BaseAlarmMsgDTO> msgList, Date startTime, Date endTime,
                                                   Map<String, Object> placeHolder, EAlarmLevel alarmLevel, String
                                                           alarmBiz);


    protected void sendWeChat(List<BaseAlarmMsgDTO> msgList, Date startTime, Date endTime,
                              EAlarmLevel alarmLevel, String alarmBiz) {
        String weChatBody = generateWeChatBody(msgList, startTime, endTime, alarmLevel, alarmBiz);
        alarmMessageProducer.sendWebChart4OperatorMonitor(weChatBody, new Date());
    }

    /**
     * 生成微信的内容方法
     * 不同的预警业务生成逻辑不同对此进行了封装，由具体的子类去实现
     *
     * @param alarmLevel 预警等级
     * @param endTime    结束时间
     * @param startTime  开始时间
     * @param msgList    预警信息
     * @param alarmBiz   预警业务信息
     * @return 微信预警内容
     */
    protected abstract String generateWeChatBody(List<BaseAlarmMsgDTO> msgList, Date startTime, Date endTime,
                                                 EAlarmLevel alarmLevel, String alarmBiz);


}
