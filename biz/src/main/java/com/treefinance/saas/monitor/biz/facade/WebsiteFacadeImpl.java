package com.treefinance.saas.monitor.biz.facade;

import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.service.EcommerceService;
import com.treefinance.saas.monitor.biz.service.OperatorService;
import com.treefinance.saas.monitor.biz.service.WebsiteService;
import com.treefinance.saas.monitor.common.domain.dto.EcommerceDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorDTO;
import com.treefinance.saas.monitor.common.domain.dto.WebsiteDTO;
import com.treefinance.saas.monitor.context.component.AbstractFacade;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResult;
import com.treefinance.saas.monitor.facade.domain.result.MonitorResultBuilder;
import com.treefinance.saas.monitor.facade.domain.ro.WebsiteRO;
import com.treefinance.saas.monitor.facade.service.WebsiteFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by haojiahong on 2017/8/15.
 */
@Service("websiteFacade")
public class WebsiteFacadeImpl extends AbstractFacade implements WebsiteFacade {

    @Autowired
    private WebsiteService websiteService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private EcommerceService ecommerceService;

    @Override
    public MonitorResult<List<WebsiteRO>> queryWebsiteDetailByWebsiteName(List<String> stringList) {
        List<WebsiteRO> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(stringList)) {
            return MonitorResultBuilder.build(result);
        }
        List<WebsiteDTO> websiteDTOList = websiteService.getWebsiteListByName(stringList);
        //<WebsiteType,List<WebsiteDTO>>
        Map<String, List<WebsiteDTO>> websiteMap = websiteDTOList
                .stream()
                .collect(Collectors.groupingBy(WebsiteDTO::getWebsiteType));
        for (Map.Entry<String, List<WebsiteDTO>> entry : websiteMap.entrySet()) {

            List<Integer> websiteIdList = entry.getValue().stream().map(WebsiteDTO::getId).collect(Collectors.toList());

            if ("1".equals(entry.getKey())) {//邮箱
                for (WebsiteDTO websiteDTO : entry.getValue()) {
                    WebsiteRO websiteRO = convert(websiteDTO, WebsiteRO.class);
                    if (websiteRO != null) {
                        websiteRO.setWebsiteDetailName(websiteDTO.getWebsiteName());
                        result.add(websiteRO);
                    }
                }
            }

            if ("2".equals(entry.getKey())) {//运营商
                List<OperatorDTO> operatorDTOList = operatorService.getOperatorListByWebsiteIds(websiteIdList);
                //<websiteId,OperatorDTO>
                Map<Integer, OperatorDTO> operatorMap = operatorDTOList
                        .stream()
                        .collect(Collectors.toMap(OperatorDTO::getWebsiteId, operatorDTO -> operatorDTO, (key1, key2) -> key1));
                for (WebsiteDTO websiteDTO : entry.getValue()) {
                    WebsiteRO websiteRO = convert(websiteDTO, WebsiteRO.class);
                    if (websiteRO != null) {
                        OperatorDTO operatorDTO = operatorMap.get(websiteDTO.getId());
                        if (operatorDTO != null) {
                            websiteRO.setWebsiteDetailName(operatorDTO.getOperatorName());
                        }
                        result.add(websiteRO);
                    }
                }
            }

            if ("3".equals(entry.getKey())) {//电商
                List<EcommerceDTO> ecommerceDTOList = ecommerceService.getEcommerceListByWebsiteIds(websiteIdList);
                Map<Integer, EcommerceDTO> ecommerceMap = ecommerceDTOList
                        .stream()
                        .collect(Collectors.toMap(EcommerceDTO::getWebsiteId, ecommerceDTO -> ecommerceDTO, (key1, key2) -> key1));
                for (WebsiteDTO websiteDTO : entry.getValue()) {
                    WebsiteRO websiteRO = convert(websiteDTO, WebsiteRO.class);
                    if (websiteRO != null) {
                        EcommerceDTO ecommerceDTO = ecommerceMap.get(websiteDTO.getId());
                        if (ecommerceDTO != null) {
                            websiteRO.setWebsiteDetailName(ecommerceDTO.getEcommerceName());
                        }
                        result.add(websiteRO);
                    }
                }
            }
        }
        return MonitorResultBuilder.build(result);
    }

}
