package com.treefinance.saas.monitor.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.domain.dto.BaseAlarmMsgDTO;
import com.treefinance.saas.monitor.common.domain.dto.BaseStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.EmailMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.mapper.EmailStatAccessMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.treefinance.saas.monitor.common.constants.AlarmConstants.ALL_EMAIL;
import static com.treefinance.saas.monitor.common.constants.AlarmConstants.GROUP_EMAIL;

/**
 * 邮箱预警服务类的模板类
 *
 * @author chengtong
 * @date 18/3/12 11:20
 */
@Service
public abstract class AbstractEmailAlarmServiceTemplate implements EmailMonitorAlarmService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractEmailAlarmServiceTemplate.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    protected EmailStatAccessMapper emailStatAccessMapper;
    @Autowired
    protected DiamondConfig diamondConfig;

    @Autowired
    protected SmsNotifyService smsNotifyService;
    @Autowired
    protected IvrNotifyService ivrNotifyService;
    @Autowired
    protected AlarmMessageProducer alarmMessageProducer;


    @Override
    public void alarm(Date now, EmailMonitorAlarmConfigDTO configDTO, ETaskStatDataType type) {

        // TODO: 18/5/23 reactor abstract super class
        String[] emails = configDTO.getEmails().toArray(new String[configDTO.getEmails().size()]);

        //获取时间
        Date baseTime = getBaseTime(now, JSON.parseObject(JSON.toJSONString(configDTO)));
        //是否预警过?
        String alarmTimeKey = getKey(type, baseTime,ALL_EMAIL.equals(emails[0])?ALL_EMAIL:GROUP_EMAIL);
        if (ifAlarmed(baseTime, alarmTimeKey)) {
            logger.info("邮箱监控,预警定时任务执行jobTime={},baseTime={},statType={},alarmType={}已预警,不再预警",
                    MonitorDateUtils.format(baseTime), JSON.toJSONString(type), configDTO.getAlarmType());
            return;
        }
        //获取基础数据
        Date startTime = DateUtils.addMinutes(baseTime, -configDTO.getIntervalMins());
        List<BaseStatAccessDTO> emailStatAccessDTOS = getBaseData(startTime, baseTime, type, AlarmConstants
                .VIRTUAL_TOTAL_STAT_APP_ID, emails);

        //是否需要预警？
        if (emailStatAccessDTOS == null || emailStatAccessDTOS.isEmpty()) {
            logger.info("邮箱监控,预警定时任务执行jobTime={},要统计的数据时刻startTime={},endTime={},此段时间内,未查询到所有邮箱的统计数据",
                    MonitorDateUtils.format(now), MonitorDateUtils.format(startTime), MonitorDateUtils.format(baseTime));
            return;
        }

        //获取平均值数据
        Map<String, BaseStatAccessDTO> compareMap = getPreviousCompareDataMap(now, baseTime, emailStatAccessDTOS,
                configDTO, type, emails);

        //是否需要预警？
        logger.info("邮箱监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},获取前n天内,相同时刻区分邮箱统计的平均值compareMap={}",
                MonitorDateUtils.format(now), MonitorDateUtils.format(baseTime), JSON.toJSONString(compareMap));
        if (MapUtils.isEmpty(compareMap)) {
            return;
        }
        //计算需要预警的信息
        List<BaseAlarmMsgDTO> msgList = getAlarmMsgList(now, emailStatAccessDTOS, compareMap, configDTO);

        //是否需要预警
        logger.info("邮箱监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},区分邮箱统计需要预警的数据信息msgList={}",
                MonitorDateUtils.format(now), MonitorDateUtils.format(baseTime), JSON.toJSONString(msgList));
        if (CollectionUtils.isEmpty(msgList)) {
            return;
        }
        //确定回调预警等级
        EAlarmLevel level = msgList.stream().anyMatch(baseAlarmMsgDTO -> EAlarmLevel.error.equals(baseAlarmMsgDTO
                .getAlarmLevel())) ? EAlarmLevel.error : msgList
                .stream().anyMatch(baseAlarmMsgDTO -> EAlarmLevel.warning.equals(baseAlarmMsgDTO.getAlarmLevel()))
                ? EAlarmLevel
                .warning : EAlarmLevel.info;
        //构建回调内容 发送通知;


        sendAlarmMsg(level, msgList, configDTO, startTime, baseTime, type);
    }

    /**
     * 获取基础时间
     */
    private Date getBaseTime(Date jobTime, JSONObject config) {
        Date statTime = DateUtils.addSeconds(jobTime, -config.getIntValue("taskTimeoutSecs"));
        //取得预警原点时间,如:statTime=14:01分,30分钟间隔统计一次,则beginTime为14:00.统计的数据间隔[13:30-13:40;13:40-13:50;13:50-14:00]
        return TaskOperatorMonitorKeyHelper.getRedisStatDateTime(statTime, config.getInteger("intervalMins"));
    }

    /**
     * redis的key值判断 是否继续
     */
    private boolean ifAlarmed(Date baseTime, String alarmTimeKey) {
        BoundSetOperations<String, Object> setOperations = redisTemplate.boundSetOps(alarmTimeKey);
        if (setOperations.isMember(MonitorDateUtils.format(baseTime))) {
            return true;
        }

        setOperations.add(MonitorDateUtils.format(baseTime));
        if (setOperations.getExpire() == -1) {
            setOperations.expire(2, TimeUnit.DAYS);
        }

        return false;
    }

    /**
     * 获取基础数据 返回一段时间内的数据
     * 可以用基类作为返回的数据；兼容不同的业务线的数据机构
     * 使用email的数组 兼容分组的或者是大盘的邮箱监控
     *
     * @param startTime    数据查询的开始时间
     * @param endTime      数据查询的结束时间
     * @param statDataType 数据查询的类型
     * @param appId        数据查询的appid 商户编号
     * @param email        用于数据查询时 是大盘 or 分组的 （邮箱是 分邮箱提供商（邮箱） 、运营商是分 运营商（10个大的运营商））、以缺省参数配置；
     * @return 基础数据
     */
    protected abstract List<BaseStatAccessDTO> getBaseData(Date startTime, Date endTime, ETaskStatDataType statDataType, String
            appId, String... email);

    /**
     * 获取这个预警类型下的redis ke，不同的预警类型可以有不同的实现y
     *
     * @param type     统计类型
     * @param baseTime 任务时间
     * @param alarmType all or group
     * @return redis key
     */
    protected abstract String getKey(ETaskStatDataType type, Date baseTime, String alarmType);

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
            BaseStatAccessDTO> compareMap, EmailMonitorAlarmConfigDTO configDTO);


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
     * @param jobTime   任务时间
     * @param baseTime  任务时间区间的结束时间
     * @param dtoList   基础数据的列表
     * @param configDTO 配置
     * @param statType  统计类型
     * @param emails    缺省的参数（邮箱类型、运营商类型）列表
     * @return Map 已不同（运营商、邮箱）区分的比较值的集合体；
     */
    protected abstract Map<String, BaseStatAccessDTO> getPreviousCompareDataMap(Date jobTime, Date baseTime,
                                                                                List<BaseStatAccessDTO> dtoList,
                                                                                EmailMonitorAlarmConfigDTO configDTO,
                                                                                ETaskStatDataType statType, String... emails);

    /**
     * 发送预警信息
     *
     * @param alarmLevel 预警等级
     * @param dtoList    预警信息
     * @param configDTO  配置
     * @param startTime  开始时间
     * @param endTime    结束时间
     */
    protected abstract void sendAlarmMsg(EAlarmLevel alarmLevel, List<BaseAlarmMsgDTO> dtoList,
                                         EmailMonitorAlarmConfigDTO configDTO, Date startTime, Date endTime,
                                         ETaskStatDataType statDataType);

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

        String mailDataBody = generateMailDataBody(msgList, startTime, endTime, map, alarmLevel,alarmBiz);

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
                                                   Map<String, Object> placeHolder, EAlarmLevel alarmLevel,String
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
