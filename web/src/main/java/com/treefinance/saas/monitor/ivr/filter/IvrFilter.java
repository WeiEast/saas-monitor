package com.treefinance.saas.monitor.ivr.filter;

import com.alibaba.fastjson.JSON;
import com.datatrees.toolkits.util.http.servlet.ServletRequestUtils;
import com.datatrees.toolkits.util.http.servlet.ServletResponseUtils;
import com.datatrees.toolkits.util.json.Jackson;
import com.google.common.collect.Maps;
import com.treefinance.saas.knife.result.SimpleResult;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.common.Constants;
import com.treefinance.saas.monitor.common.utils.AESUtils;
import com.treefinance.saas.monitor.ivr.utils.WrapUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author:guoguoyun
 * @date:Created in 2018/1/31下午2:50
 */
public class IvrFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(IvrFilter.class);

    private DiamondConfig diamondConfig;

    public IvrFilter(DiamondConfig diamondConfig) {
        this.diamondConfig = diamondConfig;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        String token = request.getHeader(Constants.TOKEN);
        Map inputMap = Maps.newHashMap(request.getParameterMap());
        String params = null;
        try {
            // 1.验证token
            if (!diamondConfig.getIvrToken().equals(token)) {
                String responseBody = Jackson.toJSONString(SimpleResult.failResult("无效的token"));
                ServletResponseUtils.responseJson(response, 403, responseBody);
                return;
            }

            if (WrapUtils.isApplicationJsonBody(request)) {
                String jsonBody = WrapUtils.getJsonBody(request);
                Map<String, Object> jsonMap = WrapUtils.toJsonMap(jsonBody);
                params = (String) jsonMap.get(Constants.PARAMS);
                if (StringUtils.isNotEmpty(params)) {
                    params = AESUtils.decrytDataWithBase64AsString(params, diamondConfig.getIvrAccessKey());
                    // 合并加解密前后数据
                    Map<String, Object> paramsMap = WrapUtils.toJsonMap(params);
                    if (MapUtils.isNotEmpty(paramsMap)) {
                        jsonMap.putAll(paramsMap);
                    }
                }
                inputMap.putAll(jsonMap);
                request = WrapUtils.wrapRequest(request, null, WrapUtils.toInputStream(jsonMap));
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            String responseBody = Jackson.toJSONString(SimpleResult.failResult(e.getMessage()));
            ServletResponseUtils.responseJson(response, 400, responseBody);
            logger.error("{} request {} {} : params={},  cost {} ms",
                    ServletRequestUtils.getIP(request), request.getMethod(), request.getRequestURL(),
                    JSON.toJSONString(request.getParameterMap()),
                    (System.currentTimeMillis() - start), e);
        } finally {
            logger.info("{} request {} {} : params={},  cost {} ms",
                    ServletRequestUtils.getIP(request), request.getMethod(), request.getRequestURL(),
                    JSON.toJSONString(inputMap),
                    (System.currentTimeMillis() - start));
        }


    }


}
