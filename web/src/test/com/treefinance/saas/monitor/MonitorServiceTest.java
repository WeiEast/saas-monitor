package com.treefinance.saas.monitor;

import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatAccessAlarmMsgDTO;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by haojiahong on 2017/11/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MonitorServiceTest {

    @Autowired
    private AlarmMessageProducer alarmMessageProducer;
    @Autowired
    private DiamondConfig diamondConfig;

    @Test
    public void testMail() {
        List<OperatorStatAccessAlarmMsgDTO> msgList = Lists.newArrayList();
        OperatorStatAccessAlarmMsgDTO msg1 = new OperatorStatAccessAlarmMsgDTO();
        msg1.setGroupName("浙江移动");
        msg1.setAlarmDesc("登录转化率低于前7天平均值的70%");
        msg1.setValue(new BigDecimal(10));
        msg1.setThreshold(new BigDecimal(20));
        msgList.add(msg1);
        OperatorStatAccessAlarmMsgDTO msg2 = new OperatorStatAccessAlarmMsgDTO();
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


    private String generateMailDataBody(List<OperatorStatAccessAlarmMsgDTO> msgList, Date dataTime) {
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
        for (OperatorStatAccessAlarmMsgDTO msg : msgList) {
            buffer.append("<tr>")
                    .append("<td>").append(msg.getAlarmDesc()).append("</td>")
                    .append("<td>").append(msg.getValue()).append("</td>")
                    .append("<td>").append(msg.getThreshold()).append("</td>")
                    .append("<td>").append(msg.getOffset()).append("</td>").append("</tr>");

        }
        buffer.append("</table>");
        return buffer.toString();
    }


}
