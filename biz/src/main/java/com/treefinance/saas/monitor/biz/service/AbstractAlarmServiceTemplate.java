package com.treefinance.saas.monitor.biz.service;

import com.datatrees.notify.async.body.mail.MailEnum;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.config.IvrConfig;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.domain.dto.BaseAlarmMsgDTO;
import com.treefinance.saas.monitor.common.domain.dto.BaseStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.BaseAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmRecordStatus;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.enumeration.EOrderStatus;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.context.SpringUtils;
import com.treefinance.saas.monitor.context.component.AbstractService;
import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import com.treefinance.saas.monitor.dao.entity.AlarmRecordCriteria;
import com.treefinance.saas.monitor.dao.entity.AlarmWorkOrder;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import com.treefinance.saas.monitor.dao.entity.WorkOrderLog;
import com.treefinance.saas.monitor.dao.mapper.EmailStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.OperatorStatAccessMapper;
import com.treefinance.saas.monitor.exception.NoNeedAlarmException;
import com.treefinance.toolkit.util.DateUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
public abstract class AbstractAlarmServiceTemplate extends AbstractService implements MonitorAlarmService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAlarmServiceTemplate.class);

    public static Long day = 24 * 60 * 60 * 1000L;

    @Resource
    private UidService uidService;

    @Autowired
    protected EmailStatAccessMapper emailStatAccessMapper;
    @Autowired
    protected OperatorStatAccessMapper operatorStatAccessMapper;
    @Autowired
    protected AlarmRecordService alarmRecordService;
    @Autowired
    protected AlarmWorkOrderService alarmWorkOrderService;
    @Autowired
    protected DiamondConfig diamondConfig;
    @Autowired
    protected SmsNotifyService smsNotifyService;
    @Autowired
    protected IvrNotifyService ivrNotifyService;
    @Autowired
    protected SaasWorkerService saasWorkerService;
    @Autowired
    protected AlarmMessageProducer alarmMessageProducer;
    @Autowired
    protected IvrConfig ivrConfig;

    protected abstract EAlarmType getAlarmType();

    @Override
    public void alarm(Date now, BaseAlarmConfigDTO configDTO, ETaskStatDataType type, EAlarmType alarmType) {

        //获取时间
        Date baseTime = getBaseTime(now, configDTO);

        //获取key
        String alarmTimeKey = getKey(type, baseTime, configDTO);

        //是否预警过?
        ifAlarmed(now, baseTime, alarmTimeKey, configDTO);

        String summary = null;
        EAlarmLevel level = null;

        try {
            //获取基础数据
            List<BaseStatAccessDTO> emailStatAccessDTOS = getBaseData(baseTime, type, configDTO);

            //获取平均值数据
            Map<String, BaseStatAccessDTO> compareMap = getPreviousCompareDataMap(baseTime, emailStatAccessDTOS, configDTO, type);

            //计算需要预警的信息
            List<BaseAlarmMsgDTO> msgList = getAlarmMsgList(now, emailStatAccessDTOS, compareMap, configDTO);


            //确定回调预警等级
            level = determineLevel(msgList);
            ESaasEnv env = ESaasEnv.getByValue(configDTO.getSaasEnv());

            if (EAlarmLevel.info.equals(level)) {
                //发出全局的报警
                String content = sendAlarmMsg(level, msgList, configDTO, baseTime, type);
                //保存记录
                saveProcessedRecord(env, baseTime, msgList, level, content, configDTO);
                throw new NoNeedAlarmException("info等级的预警");
            }
            //生成摘要
            summary = generateSummary(level, env, msgList);
            AlarmRecord record = alarmRecordService.getFirstStatusRecord(level, summary, EAlarmRecordStatus.UNPROCESS);

            if (record != null) {
                logger.info("已存在{}的记录，不再继续", EAlarmRecordStatus.UNPROCESS.getDesc());
                //save record if has unprocessed same type record
                saveUnProcessRecord(env, baseTime, msgList, level, String.valueOf(record.getId()),configDTO);
                throw new NoNeedAlarmException("存在未处理的预警记录");
            }

            AlarmRecordCriteria criteria = new AlarmRecordCriteria();
            Date oneDayAgo = new Date(now.getTime() - day);
            criteria.createCriteria().andLevelEqualTo(level.name()).andSummaryEqualTo(summary).andIsProcessedEqualTo(EAlarmRecordStatus.DISABLE.getCode()).andStartTimeGreaterThan(oneDayAgo);
            List<AlarmRecord> records = alarmRecordService.queryByCondition(criteria);

            if (!records.isEmpty()) {
                logger.info("一天之内已存在{}的记录，不再继续", EAlarmRecordStatus.DISABLE.getDesc());
                //save record if has unprocessed same type record
                saveDisableRecord(env, baseTime, msgList, level, String.valueOf(records.get(0).getId()),configDTO);
                throw new NoNeedAlarmException("存在无法处理的预警");
            }

            //获取值班人员
            List<SaasWorker> saasWorkers = saasWorkerService.getDutyWorker(baseTime);

            if (saasWorkers == null || saasWorkers.isEmpty()) {
                logger.info("当前时间:{}没有配置值班人，使用默认值班人员", DateUtils.format(baseTime));
                saasWorkers = new ArrayList<>();
                saasWorkers.add(SaasWorker.DEFAULT_WORKER);
            }

            Long recordId = uidService.getId();
            Long orderId = uidService.getId();

            String content = "";

            for (SaasWorker saasWorker : saasWorkers) {
                content = genDutyManAlarmInfo(recordId, orderId, msgList, level, baseTime, env);
                Map<String, String> map = new HashMap<>(2);
                map.put("name", saasWorker.getName());
                map.put("saasEnv", diamondConfig.getSaasMonitorEnvironment());

                String newContent = StrSubstitutor.replace(content, map);
                Map<String, Object> params = genIvrMap(recordId, saasWorker, level, baseTime, env);

                sendIvr(newContent, saasWorker, params);
                sendSms(newContent, saasWorker);
                sendEmail(newContent, saasWorker);
            }

            Map<String, String> map = new HashMap<>(2);
            List<String> names = saasWorkers.stream().map(SaasWorker::getName).collect(Collectors.toList());
            map.put("name", Joiner.on(",").join(names));

            record = genAlarmRecord(recordId, baseTime, EAlarmRecordStatus.UNPROCESS, level, StrSubstitutor.replace(content, map), env, msgList, configDTO);
            AlarmWorkOrder workOrder = getAlarmWorkOrder(now, saasWorkers, recordId, orderId);
            WorkOrderLog orderLog = getInitWorkOrderLog(now, recordId, orderId);

            //构建回调内容 发送通知;
            String alarmMsg = sendAlarmMsg(level, msgList, configDTO, baseTime, type);
            record.setContent(alarmMsg);

            try {
                alarmRecordService.saveAlarmRecords(workOrder, record, orderLog);
            } catch (Exception e) {
                logger.error("插入工单记录等失败，仍然发送特定信息及群发信息,错误信息：{}", e.getMessage());
            }
            throw new NoNeedAlarmException("正常流程结束");
        } catch (NoNeedAlarmException e) {
            logger.info(e.getMessage());
            repairProcess(now, alarmType, summary, level ,configDTO);
        }
    }

    private void repairProcess(Date now, EAlarmType alarmType, String summary, EAlarmLevel level,BaseAlarmConfigDTO configDTO) {
        if (level == null || summary == null) {
            alarmRecordRepair(now, alarmType,configDTO,null);
        }else {
            alarmRecordRepair(now,alarmType,configDTO,summary);
        }
    }

    private void alarmRecordRepair(Date now, EAlarmType alarmType, BaseAlarmConfigDTO configDTO, String summary){
        List<AlarmRecord> alarmRecords = getUnprocessedRecords(alarmType,configDTO, summary);
        if (alarmRecords.isEmpty()) {
            logger.info("没有处于未处理的预警记录，预警类型：{}", alarmType.getCode());
            return;
        }
        doRepair(now, alarmRecords);
    }


    private void doRepair(Date now, List<AlarmRecord> alarmRecords) {
        for (AlarmRecord record : alarmRecords) {
            record.setEndTime(now);
            record.setIsProcessed(EAlarmRecordStatus.REPAIRED.getCode());
            AlarmWorkOrder order = alarmWorkOrderService.getByRecordId(record.getId());
            if (order != null) {
                WorkOrderLog workOrderLog = new WorkOrderLog();
                workOrderLog.setId(uidService.getId());
                workOrderLog.setOrderId(order.getId());
                workOrderLog.setRecordId(record.getId());
                workOrderLog.setOpDesc("系统判定预警恢复");
                workOrderLog.setOpName("system");
                workOrderLog.setLastUpdateTime(now);
                workOrderLog.setCreateTime(now);

                order.setLastUpdateTime(now);
                order.setStatus(EOrderStatus.REPAIRED.getCode());
                order.setRemark("系统判定修复");
                order.setProcessorName("system");

                alarmRecordService.repairAlarmRecord(order, record, workOrderLog);
            }else {
                alarmRecordService.repairAlarmRecord(null,record , null);
            }
            sendAlarmRepair(record);
        }
    }

    protected abstract List<AlarmRecord> getUnprocessedRecords(EAlarmType alarmType, BaseAlarmConfigDTO configDTO, String summary);




    private void sendAlarmRepair(AlarmRecord alarmRecord) {

        String stringBuilder = "【预警恢复】" + "环境:" + diamondConfig.getMonitorEnvironment() + "\n" +
                "发生在 时间为:" + DateUtils.format(alarmRecord.getStartTime()) + " \n预警等级：" + alarmRecord.getLevel() +
                " \n预警类型：" + alarmRecord.getAlarmType() + "\n预警编号:" +
                alarmRecord.getId() +
                "的预警由系统判定恢复。";

        alarmMessageProducer.sendWebChart4OperatorMonitor(stringBuilder, new Date());
        alarmMessageProducer.sendMail4OperatorMonitor(stringBuilder,stringBuilder,new Date());
    }

    private void sendEmail(String content, SaasWorker saasWorker) {
        String title = "值班人员预警";
        try {
            alarmMessageProducer.sendMail(title, content, MailEnum.SIMPLE_MAIL, saasWorker.getEmail());
        } catch (Exception e) {
            logger.error("发送邮件失败，{}", e.getMessage());
        }
    }

    private void sendSms(String content, SaasWorker saasWorker) {
        String mobile = saasWorker.getMobile();
        try {
            smsNotifyService.send(content, Collections.singletonList(mobile));
        } catch (Exception e) {
            logger.error("发送短信失败，{}", e.getMessage());
        }
    }

    public static WorkOrderLog getInitWorkOrderLog(Date now, Long recordId, Long orderId) {
        WorkOrderLog orderLog = new WorkOrderLog();
        orderLog.setId(SpringUtils.getBean(UidService.class).getId());
        orderLog.setOrderId(orderId);
        orderLog.setRecordId(recordId);
        orderLog.setOpName("system");
        orderLog.setOpDesc("创建操作工单");
        orderLog.setCreateTime(now);
        orderLog.setLastUpdateTime(now);
        return orderLog;
    }

    public static AlarmWorkOrder getAlarmWorkOrder(Date now, List<SaasWorker> saasWorkers, Long recordId, Long orderId) {
        AlarmWorkOrder workOrder = new AlarmWorkOrder();
        workOrder.setId(orderId);
        workOrder.setRecordId(recordId);
        List<String> names = saasWorkers.stream().map(SaasWorker::getName).collect(Collectors.toList());
        workOrder.setDutyName(Joiner.on(",").join(names));
        workOrder.setStatus(EOrderStatus.UNPROCESS.getCode());
        workOrder.setCreateTime(now);
        workOrder.setLastUpdateTime(now);
        return workOrder;
    }

    private void saveProcessedRecord(ESaasEnv env, Date baseTime, List<BaseAlarmMsgDTO> msgList, EAlarmLevel
            level, String content, BaseAlarmConfigDTO configDTO) {
        AlarmRecord alarmRecord = genAlarmRecord(null, baseTime, EAlarmRecordStatus.PROCESSED, level, content, env,
                msgList, configDTO);
        alarmRecordService.insert(alarmRecord);
    }

    private void saveUnProcessRecord(ESaasEnv env, Date baseTime, List<BaseAlarmMsgDTO> msgList, EAlarmLevel
            level, String content,BaseAlarmConfigDTO configDTO) {
        AlarmRecord alarmRecord = genAlarmRecord(null, baseTime, EAlarmRecordStatus.UNPROCESS, level, content, env,
                msgList, configDTO);
        alarmRecordService.insert(alarmRecord);
    }

    private void saveDisableRecord(ESaasEnv env, Date baseTime, List<BaseAlarmMsgDTO> msgList, EAlarmLevel
            level, String content,BaseAlarmConfigDTO configDTO) {
        AlarmRecord alarmRecord = genAlarmRecord(null, baseTime, EAlarmRecordStatus.DISABLE, level, content, env,
                msgList, configDTO);
        alarmRecordService.insert(alarmRecord);
    }


    /**
     * 通过每个预警信息(BaseAlarmMsgDTO)的等级，来确定本次预警的等级
     *
     * @param msgList 预警数据
     * @return 预警等级
     */
    protected abstract EAlarmLevel determineLevel(List<BaseAlarmMsgDTO> msgList);

    /**
     * 获取基础时间
     * <p>
     * 距离任务开始时间之前的timeout Seconds
     */
    private Date getBaseTime(Date jobTime, BaseAlarmConfigDTO config) {
        //由于任务监控时间已经有createTime改为lastUpdatedTime,所以无需再延迟预警了
        Date statTime = jobTime;
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
        return num < fewNum || rate.compareTo(compareRate) < 0;
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
     * @param alarmLevel   预警等级
     * @param dtoList      预警信息
     * @param configDTO    配置
     * @param endTime      结束时间
     * @param statDataType 统计类型
     * @return 微信的 发送内容 因为微信每个渠道都发
     */
    protected abstract String sendAlarmMsg(EAlarmLevel alarmLevel, List<BaseAlarmMsgDTO> dtoList, BaseAlarmConfigDTO
            configDTO, Date endTime, ETaskStatDataType statDataType);


    /**
     * 生成发送给值班人员的具体的预警信息；
     *
     * @param id         记录编号;
     * @param orderId    工单编号;
     * @param dtoList    预警的信息列表;
     * @param alarmLevel 预警的等级;
     * @param baseTime   数据时间;
     * @param env        环境;
     * @return 预警信息：
     */
    protected abstract String genDutyManAlarmInfo(Long id, Long orderId, List<BaseAlarmMsgDTO> dtoList, EAlarmLevel alarmLevel, Date baseTime, ESaasEnv env);

    /**
     * 生成预警记录AlarmRecord;
     *
     * @param baseTime    记录发出预警的数据时间;
     * @param isProcessed 是否是处理中的;
     * @param level       预警等级;
     * @param content     发出预警的信息;
     * @param eSaasEnv    环境;
     * @param msgList     预警信息列表;
     * @param alarmConfigDTO
     * @return 预警记录entity
     */
    private AlarmRecord genAlarmRecord(Long id, Date baseTime, EAlarmRecordStatus isProcessed, EAlarmLevel level, String content, ESaasEnv eSaasEnv, List<BaseAlarmMsgDTO> msgList, BaseAlarmConfigDTO alarmConfigDTO) {
        Date now = new Date();
        AlarmRecord alarmRecord = new AlarmRecord();
        alarmRecord.setId(id == null ? uidService.getId() : id);
        alarmRecord.setLevel(level.name());
        alarmRecord.setSummary(generateSummary(level, eSaasEnv, msgList));
        alarmRecord.setContent(content);
        alarmRecord.setDataTime(baseTime);
        alarmRecord.setIsProcessed(isProcessed.getCode());
        alarmRecord.setStartTime(now);
        if (EAlarmRecordStatus.PROCESSED.equals(isProcessed)) {
            alarmRecord.setEndTime(now);
        }
        alarmRecord.setCreateTime(now);
        alarmRecord.setLastUpdateTime(now);
        alarmRecord.setAlarmType(getAlarmType().name());
        return alarmRecord;
    }

    @Setter
    @Getter
    public class BizSourceAspect {

        private String bizSource;

        private String aspect;

        public BizSourceAspect(String bizSource, String aspect) {
            this.bizSource = bizSource;
            this.aspect = aspect;
        }

    }

    /**
     * 获取 业务线和某个方面（某个指标）的键值对列表 的 summary
     *
     * @param bizSourceAspectList 键值对列表
     * @return summary的一部分
     * example:
     * operator：group_code:alarmAspectType
     * 两个预警 中国移动登录转化率和浙江移动确认手机转化率
     * CHINA_10010:AS_LC&ZHE_JIANG_10086:AS_CMC
     */
    protected String getBizSourceAspect(List<BizSourceAspect> bizSourceAspectList) {

        bizSourceAspectList.sort((o1, o2) -> {
            if (o1.bizSource.compareTo(o2.bizSource) == 0) {
                return o1.aspect.compareTo(o2.aspect);
            }
            return o1.bizSource.compareTo(o2.bizSource);
        });

        StringBuilder sb = new StringBuilder();
        for (BizSourceAspect bizSourceAspect : bizSourceAspectList) {
            sb.append(bizSourceAspect.bizSource).append(":").append(bizSourceAspect.aspect).append("&");
        }

        return sb.substring(0, sb.length() - 1);
    }


    /**
     * 生成预警记录的summary，不同的预警可能会规则也不一样，具体交给子类自己去实现；
     *
     * @param alarmLevel          预警等级 ;
     * @param env                 环境;
     * @param bizSourceAspectList 预警业务和业务数据来源的键值对的列表;
     * @return summary字段的信息
     */
    protected abstract String generateSummary(EAlarmLevel alarmLevel, ESaasEnv env, List<BaseAlarmMsgDTO> bizSourceAspectList);

    /**
     * 生成发送ivr时需要replace placeholder的map
     *
     * @param alarmLevel 预警等级
     * @param id         预警记录id
     * @param saasWorker 工作人员
     * @param baseTime   数据时间
     * @param env        环境
     * @return map
     */
    protected abstract Map<String, Object> genIvrMap(Long id, SaasWorker saasWorker, EAlarmLevel
            alarmLevel, Date baseTime, ESaasEnv env);


    private void sendIvr(String content, SaasWorker saasWorker, Map<String, Object> params) {
        if (!AlarmConstants.SWITCH_ON.equals(diamondConfig.getDutyIvrSwitch())) {
            logger.info("对值班人员的ivr提醒已经关闭。。");
            return;
        }
        try {
            Map<String, String> map = Maps.newHashMap();

            map.put("\n", "");
            String ivrContent = StrSubstitutor.replace(content, map);
            logger.info("给值班人员预警，content={},mobile={}，name={}", ivrContent, saasWorker.getMobile(), saasWorker.getName());
            ivrNotifyService.notifyIvrToDutyMan(ivrContent, saasWorker.getMobile(), saasWorker.getName(), ivrConfig.getDutyManIvrModel(), params);
        } catch (Exception e) {
            logger.error("发送ivr失败,{}", e.getMessage());
        }
    }

}
