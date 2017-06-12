package com.treefinance.saas.monitor.biz.facade;

import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.service.WebsiteService;
import com.treefinance.saas.monitor.common.domain.dto.WebsiteDTO;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.MailRO;
import com.treefinance.saas.monitor.facade.service.MailFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/12.
 */
@Service("mailFacade")
public class MailFacadeImpl implements MailFacade {
    @Autowired
    private WebsiteService websiteService;

    @Override
    public MonitorResult<List<MailRO>> queryAll() {
        List<WebsiteDTO> websiteDTOS = websiteService.getSupportMails();
        if (CollectionUtils.isEmpty(websiteDTOS)) {
            return MonitorResultBuilder.build("暂不支持邮箱");
        }
        List<MailRO> mailList = Lists.newArrayList();
        websiteDTOS.forEach(websiteDTO -> {
            MailRO mail = new MailRO();
            mail.setMailCode(websiteDTO.getWebsiteName());
            mail.setMailName(websiteDTO.getWebsiteDomain());
            mailList.add(mail);
        });
        return MonitorResultBuilder.build(mailList);
    }
}
