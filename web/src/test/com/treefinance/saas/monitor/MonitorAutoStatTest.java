package com.treefinance.saas.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.biz.autostat.AutoStatService;
import com.treefinance.saas.monitor.biz.autostat.mybatis.MybatisService;
import com.treefinance.saas.monitor.biz.autostat.template.calc.calculator.DefaultStatDataCalculator;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.utils.SpringIocUtils;
import com.treefinance.saas.monitor.dao.entity.StatTemplate;
import com.treefinance.saas.monitor.dao.mapper.StatTemplateMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Buddha Bless , No Bug !
 *
 * @author haojiahong
 * @date 2018/3/30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MonitorAutoStatTest {

    @Autowired
    private RedisDao redisDao;
    @Autowired
    private AutoStatService autoStatService;
    @Autowired
    private MybatisService mybatisService;
    @Autowired
    private DefaultStatDataCalculator defaultStatDataCalculator;
    @Autowired
    private StatTemplateMapper statTemplateMapper;


    @Test
    public void testChangedTemplate() {
        List<StatTemplate> list = autoStatService.getNeedUpdateStatTemplates();
        System.out.println(JSON.toJSONString(list));
        List<StatTemplate> secList = autoStatService.getNeedUpdateStatTemplates();
        System.out.println("=====" + JSON.toJSONString(secList));

        StatTemplate s1 = new StatTemplate();
        s1.setTemplateCode("1111");

        StatTemplate s2 = new StatTemplate();
        s2.setTemplateCode("1111");

        System.out.println("hao=====" + s1.equals(s2));
    }

    public static void main(String[] args) {
        String json = "{\"source\":1,\"password\":\"12345\",\"username\":\"1235\"}";
        JSONObject map = JSON.parseObject(json);
        System.out.println(map);
    }


    @Test
    public void testAutoStatService() throws InterruptedException {
        autoStatService.execute(null);
        Thread.sleep(2000);
        autoStatService.execute(null);
    }


    @Test
    public void testRedisLock() throws InterruptedException {
        String key = "monitor:lock:1234567aaa";
        RedisKeyTask redisKeyTask = new RedisKeyTask(key, 20000L);
        Thread thread1 = new Thread(redisKeyTask);
        Thread thread2 = new Thread(redisKeyTask);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

    }


    public class RedisKeyTask implements Runnable {
        private RedisDao redisDao;
        private String lockKey;
        private Long expire;

        RedisKeyTask(String lockKey, Long expire) {
            this.redisDao = (RedisDao) SpringIocUtils.getBean("redisDaoImpl");
            this.lockKey = lockKey;
            this.expire = expire;
        }

        @Override
        public void run() {
            Map<String, Object> lockMap = new HashMap<>();
            try {
                lockMap = redisDao.acquireLock(lockKey, expire, 1000L, 20);
                if (lockMap != null) {
                    System.out.println(Thread.currentThread().getName() + "执行业务逻辑");
                    Thread.sleep(1 * 1000);//获得锁，执行业务逻辑方法
                    System.out.println("业务逻辑执行完成");
                } else {
                    System.out.println(Thread.currentThread().getName() + "未获取到锁");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisDao.releaseLock(lockKey, lockMap, expire);
            }
        }
    }

    @Test
    public void testMybatisService() {
        String jsonStr = "[{\n" +
                "    \"processSuccessCount\": \"0\",\n" +
                "    \"crawlSuccessCount\": \"0\",\n" +
                "    \"userCount\": \"1\",\n" +
                "    \"callbackSuccessCount\": \"0\",\n" +
                "    \"id\": 176424515785084928\n" +
                "  },  {\n" +
                "    \"groupCode\": \"SHANG_HAI_10086\",\n" +
                "    \"callbackSuccessCount\": \"3\",\n" +
                "    \"groupName\": \"上海移动\",\n" +
                "    \"userCount\": \"3\",\n" +
                "    \"saasEnv\": \"1\",\n" +
                "    \"startLoginCount\": \"3\",\n" +
                "    \"confirmMobileCount\": \"3\",\n" +
                "    \"appId\": \"virtual_total_stat_appId\",\n" +
                "    \"entryCount\": \"3\",\n" +
                "    \"crawlSuccessCount\": \"3\",\n" +
                "    \"dataTime\": 1525262400000,\n" +
                "    \"group\": \"saas-monitor:stat:operator-time-share:index-8:2018-05-02 20:00:00:appId-virtual_total_stat_appId:dataType-0:groupCode-SHANG_HAI_10086:saasEnv-1:groupName-上海移动\",\n" +
                "    \"loginSuccessCount\": \"3\",\n" +
                "    \"processSuccessCount\": \"3\",\n" +
                "    \"dataType\": \"0\",\n" +
                "    \"taskCount\": \"3\",\n" +
                "    \"id\": 176424515848003584\n" +
                "  }]";
        List<Map<String, Object>> resultList = JSON.parseObject(jsonStr, new TypeReference<List<Map<String, Object>>>() {
        });
        mybatisService.batchInsertOrUpdate("operator_stat_access", resultList);
        System.out.println("done====");
    }

    @Test
    public void testDataStatDataCalculator() {
        StatTemplate statTemplate = statTemplateMapper.selectByPrimaryKey(70000L);
        String dataJson = "{\n" +
                "  \"accountNo\": \"\",\n" +
                "  \"appId\": \"QATestabcdefghQA\",\n" +
                "  \"bizType\": 3,\n" +
                "  \"dataTime\": 1525251302000,\n" +
                "  \"taskId\": 176375711672594432,\n" +
                "  \"monitorType\": \"task-real-time-stat\",\n" +
                "  \"status\": 1,\n" +
                "  \"taskAttributes\": {\n" +
                "    \"groupCode\": \"ZHONG_GUO_10010\",\n" +
                "    \"groupName\": \"中国联通\"\n" +
                "  },\n" +
                "  \"uniqueId\": \"test\",\n" +
                "  \"saasEnv\":\"1\",\n" +
                "  \"webSite\": \"\",\n" +
                "  \"statCode\":\"taskCreate\",\n" +
                "  \"statName\":\"任务创建\"\n" +
                "}";
        Map<String, Object> dataMap = JSON.parseObject(dataJson);
        List<Map<String, Object>> dataList = Lists.newArrayList();
        dataList.add(dataMap);
        Map<Integer, List<Map<String, Object>>> result = defaultStatDataCalculator.calculate(statTemplate, dataList);
        System.out.println(JSON.toJSONString(result));
    }

}
