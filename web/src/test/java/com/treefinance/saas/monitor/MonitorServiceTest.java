package com.treefinance.saas.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.service.IvrNotifyService;
import com.treefinance.saas.monitor.biz.service.SmsNotifyService;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.OperatorMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.facade.domain.request.BaseStatAccessRequest;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.ro.stat.RealTimeStatAccessRO;
import com.treefinance.saas.monitor.facade.service.stat.RealTimeStatAccessFacade;
import com.treefinance.saas.monitor.share.cache.RedisDao;
import com.treefinance.toolkit.util.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    private DiamondConfig diamondConfig;
    @Autowired
    private IvrNotifyService ivrNotifyService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SmsNotifyService smsNotifyService;
    @Autowired
    private RealTimeStatAccessFacade realTimeStatAccessFacade;


    private String generateTitle() {
        return "saas-" + diamondConfig.getMonitorEnvironment() + "总运营商监控发生预警";
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
        return String.valueOf(EAlarmLevel.error) + ","
            + "您好," + "saas-" + diamondConfig.getMonitorEnvironment()
            + "发生任务预警,在"
            + DateUtils.format(startTime)
            + "--"
            + DateUtils.format(endTime)
            + "时段内没有任务创建," + "请及时处理!" + "\n";
    }

    @Test
    public void alarmAll() {
        String text = "{\"alarmSwitch\":\"on\",\"alarmType\":1,\"alarmTypeDesc\":\"总运营商按人统计预警\"," +
                "\"appId\":\"virtual_total_stat_appId\",\"appName\":\"所有商户\",\"callbackSuccessRate\":70,\"confirmMobileConversionRate\":70,\"crawlSuccessRate\":70,\"intervalMins\":30,\"loginConversionRate\":70,\"loginSuccessRate\":70,\"mailAlarmSwitch\":\"on\",\"previousDays\":7,\"processSuccessRate\":70,\"taskTimeoutSecs\":600,\"weChatAlarmSwitch\":\"on\",\"wholeConversionRate\":90}";
        OperatorMonitorAlarmConfigDTO configDTO = JSONObject.toJavaObject(JSON.parseObject(text), OperatorMonitorAlarmConfigDTO
                .class);

//        operatorMonitorAllAlarmService.alarm(new Date(),configDTO, ETaskOperatorStatType.TASK);
    }

    @Test
    public void ivrAlarm() {
        ivrNotifyService.notifyIvr(EAlarmLevel.error, EAlarmType.operator_alarm, "运营商-分时人数时间段是2018年02月06日 下午 14点30分00秒至2018年02月06日 下午 15点00分00秒运营商:中国联通预警类型是登陆成功率低于前7天平均值的70%偏离阀值程度百分之28.00时间段是2018年02月06日 下午 14点30分00秒至2018年02月06日 下午 15点00分00秒运营商:中国联通预警类型是抓取成功率低于前7天平均值的70%偏离阀值程度百分之100.00");
    }

    @Test
    public void testRealTimeStatAccess() {
        BaseStatAccessRequest request = new BaseStatAccessRequest();
        request.setAppId("virtual_total_stat_appId");
        request.setSaasEnv((byte) 0);
        request.setStartTime(DateUtils.parse("2018-07-18 16:22:00"));
        request.setEndTime(DateUtils.parse("2018-07-18 16:32:00"));
        request.setBizType((byte) 0);
        request.setIntervalMins(10);
        MonitorResult<List<RealTimeStatAccessRO>> result = realTimeStatAccessFacade.queryRealTimeStatAccess(request);
        System.out.println(JSON.toJSONString(result));
    }


}
