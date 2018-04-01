package com.treefinance.saas.monitor;

import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.utils.SpringIocUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
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

    @Test
    public void testRedisLock() throws InterruptedException {
        String key = "monitor:lock:1234567";
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
                    Thread.sleep(10 * 1000);//获得锁，执行业务逻辑方法
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


}
