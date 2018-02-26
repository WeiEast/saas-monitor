package treefinance.saas.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.IvrNotifyService;
import com.treefinance.saas.monitor.biz.service.OperatorMonitorAllAlarmService;
import com.treefinance.saas.monitor.biz.service.SmsNotifyService;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.domain.dto.TaskStatAccessAlarmMsgDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatAccessAlarmMsgDTO;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by haojiahong on 2017/11/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MonitorServiceTest {

    private static final String KEY_PREFIX = "saas-monitor-task-test";


    @Autowired
    private AlarmMessageProducer alarmMessageProducer;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private IvrNotifyService ivrNotifyService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SmsNotifyService smsNotifyService;
    @Autowired
    private OperatorMonitorAllAlarmService operatorMonitorAllAlarmService;

    @Test
    public void testMail() {
        List<TaskStatAccessAlarmMsgDTO> msgList = Lists.newArrayList();
        TaskStatAccessAlarmMsgDTO msg1 = new TaskStatAccessAlarmMsgDTO();
        msg1.setGroupName("浙江移动");
        msg1.setAlarmDesc("登录转化率低于前7天平均值的70%");
        msg1.setValue(new BigDecimal(10));
        msg1.setThreshold(new BigDecimal(20));
        msgList.add(msg1);
        TaskStatAccessAlarmMsgDTO msg2 = new TaskStatAccessAlarmMsgDTO();
        msg2.setGroupName("浙江电信");
        msg2.setAlarmDesc("登录转化率低于前7天平均值的70%");
        msg2.setValue(new BigDecimal(20));
        msg2.setThreshold(new BigDecimal(30));
        msgList.add(msg2);
        String mailDataBody = generateMailDataBody(msgList, new Date());
        String title = generateTitle();
        alarmMessageProducer.sendMail4OperatorMonitor(title, mailDataBody, new Date());

    }

    private String generateTitle() {
        return "saas-" + diamondConfig.getMonitorEnvironment() + "总运营商监控发生预警";
    }


    private String generateMailDataBody(List<TaskStatAccessAlarmMsgDTO> msgList, Date dataTime) {
        Integer intervalMins = diamondConfig.getOperatorMonitorIntervalMinutes();
        StringBuffer buffer = new StringBuffer();
        buffer.append("<br>").append("您好，").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append("总运营商监控预警,在")
                .append(MonitorDateUtils.format(MonitorDateUtils.getIntervalTime(dataTime, intervalMins)))
                .append("--")
                .append(MonitorDateUtils.format(MonitorDateUtils.getIntervalTime(DateUtils.addMinutes(dataTime, intervalMins), intervalMins)))
                .append("时段数据存在问题").append("，此时监控数据如下，请及时处理：").append("</br>");
        buffer.append("<table border=\"1\" cellspacing=\"0\" bordercolor=\"#BDBDBD\" width=\"80%\">");
        buffer.append("<tr bgcolor=\"#C9C9C9\">")
                .append("<th>").append("预警描述").append("</th>")
                .append("<th>").append("当前指标值(%)").append("</th>")
                .append("<th>").append("指标阀值(%)").append("</th>")
                .append("<th>").append("偏离阀值程度(%)").append("</th>")
                .append("</tr>");
        for (TaskStatAccessAlarmMsgDTO msg : msgList) {
            buffer.append("<tr>")
                    .append("<td>").append(msg.getAlarmDesc()).append("</td>")
                    .append("<td>").append(msg.getValue()).append("</td>")
                    .append("<td>").append(msg.getThreshold()).append("</td>")
                    .append("<td>").append(msg.getOffset()).append("</td>").append("</tr>");

        }
        buffer.append("</table>");
        return buffer.toString();
    }


    @Test
    public void testRedis() throws InterruptedException {
        Thread thread1 = new Thread(new AddTask());
        thread1.start();
        Thread.sleep(3000);
        Thread thread2 = new Thread(new RemoveTask());
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(redisDao.getRedisTemplate().opsForSet().members(KEY_PREFIX));
        System.out.println("结束");
    }

    private class AddTask implements Runnable {

        @Override
        public void run() {
            System.out.println("线程1开始执行...");
            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {

                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    System.out.println("线程1获取redis连接...");

                    redisOperations.watch(KEY_PREFIX);
                    redisOperations.multi();
                    BoundSetOperations<String, String> setOperations = redisOperations.boundSetOps(KEY_PREFIX);
                    if (!Boolean.TRUE.equals(redisOperations.hasKey(KEY_PREFIX))) {
                        setOperations.expire(2, TimeUnit.HOURS);
                    }
                    for (int i = 0; i < 5; i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        setOperations.add(String.valueOf(i));
                        System.out.println("线程1..." + setOperations.members());
                    }
                    redisOperations.exec();
                    System.out.println("线程1..." + setOperations.members());
                    return null;
                }
            });
        }
    }

    private class RemoveTask implements Runnable {

        @Override
        public void run() {
            System.out.println("线程2开始执行...");
            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {

                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    System.out.println("线程2获取redis连接...");
                    redisOperations.watch(KEY_PREFIX);
                    redisOperations.multi();
                    String[] array = new String[]{String.valueOf(0), String.valueOf(1), String.valueOf(2)};
                    redisOperations.opsForSet().remove(KEY_PREFIX, array);
                    redisOperations.exec();
                    return null;
                }
            });
        }
    }

    @Test
    public void testRedisExpire() {
        String key = "saas-monitor-test-redisTemplatee";
        String setKey = "saas-monitor-test-setOperatione";
        BoundSetOperations<String, String> setOperations = redisTemplate.boundSetOps(setKey);
        if (setOperations.isMember(String.valueOf(1))) {
            System.out.println("aaaa");
            return;
        }
        setOperations.add(String.valueOf(1));
        if (setOperations.getExpire() < 0) {
            setOperations.expire(2, TimeUnit.MINUTES);
        }
        redisTemplate.opsForSet().add(key, String.valueOf(1));
        redisTemplate.expire(key, 2, TimeUnit.MINUTES);
        for (int i = 0; i < 5; i++) {
            System.out.println(setKey + redisTemplate.getExpire(setKey));
            System.out.println(key + redisTemplate.getExpire(key));
            if (i == 2) {
                setOperations.expire(2, TimeUnit.MINUTES);
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    @Test
    public void smsTest() {
        String content = this.generateNoTaskWeChatBody(new Date(), new Date());
        smsNotifyService.send(content);
    }

    private String generateNoTaskWeChatBody(Date startTime, Date endTime) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(EAlarmLevel.error).append(",");
        buffer.append("您好,").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append("发生任务预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段内没有任务创建,").append("请及时处理!").append("\n");
        return buffer.toString();
    }

    @Test
    public void alarmAll(){
        String text = "{\"alarmSwitch\":\"on\",\"alarmType\":1,\"alarmTypeDesc\":\"总运营商按人统计预警\"," +
                "\"appId\":\"virtual_total_stat_appId\",\"appName\":\"所有商户\",\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"intervalMins\":30,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"mailAlarmSwitch\":\"on\",\"previousDays\":7,\"processSuccessRate\":70,\"taskTimeoutSecs\":600,\"weChatAlarmSwitch\":\"on\",\"wholeConversionRate\":90}";
        OperatorMonitorAlarmConfigDTO configDTO = JSONObject.toJavaObject(JSON.parseObject(text), OperatorMonitorAlarmConfigDTO
                .class);

        operatorMonitorAllAlarmService.alarm(new Date(),configDTO, ETaskOperatorStatType.TASK);
    }

    @Test
    public void ivrAlarm() {
        ivrNotifyService.notifyIvr(EAlarmLevel.error, EAlarmType.operator_alarm,"运营商-分时人数时间段是2018年02月06日 下午 14点30分00秒至2018年02月06日 下午 15点00分00秒运营商:中国联通预警类型是登陆成功率低于前7天平均值的70%偏离阀值程度百分之28.00时间段是2018年02月06日 下午 14点30分00秒至2018年02月06日 下午 15点00分00秒运营商:中国联通预警类型是抓取成功率低于前7天平均值的70%偏离阀值程度百分之100.00");
    }



}
