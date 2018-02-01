package com.treefinance.saas.monitor.ivr.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.ivr.filter.request.ByteServletInputStream;
import com.treefinance.saas.monitor.ivr.filter.request.WrappedHttpServletRequest;
import com.treefinance.saas.monitor.ivr.filter.request.WrappedJsonBodyHttpServletRequest;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by yh-treefinance on 2017/11/24.
 */
public abstract class WrapUtils {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(WrapUtils.class);


    /**
     * 是否为application Json请求
     *
     * @param request
     * @return
     */
    public static boolean isApplicationJsonBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        return StringUtils.isNotEmpty(contentType) &&
                contentType.toLowerCase().contains("application/json");
    }


    /**
     * 获取json body
     *
     * @param request
     * @return
     */
    public static String getJsonBody(HttpServletRequest request) {
        String jsonBody = "";
        if (isApplicationJsonBody(request)) {
            InputStream ins = null;
            String encoding = StringUtils.isNotEmpty(request.getCharacterEncoding()) ? request.getCharacterEncoding() : "utf-8";
            try {
                ins = request.getInputStream();
                jsonBody = IOUtils.toString(ins, encoding);
            } catch (IOException e) {
                logger.error("wrap http request application/json body exception : requestUrl={},contentType={},encoding={},",
                        request.getRequestURL(), request.getContentType(), encoding, e);
            } finally {
                IOUtils.closeQuietly(ins);
            }
        }
        return jsonBody;
    }


    /**
     * 包装request
     *
     * @param request
     * @return
     */
    public static WrappedHttpServletRequest wrapRequest(HttpServletRequest request, Map<String, String[]> parameters,
                                                        ServletInputStream inputStream) {
        if (request instanceof WrappedHttpServletRequest) {
            WrappedHttpServletRequest wrapper = (WrappedHttpServletRequest) request;
            return new WrappedHttpServletRequest((HttpServletRequest) wrapper.getRequest(), parameters, inputStream);
        }
        return new WrappedHttpServletRequest(request, parameters, inputStream);
    }

    /**
     * 包装
     *
     * @param request
     * @return
     */
    public static WrappedJsonBodyHttpServletRequest wrapJsonRequest(HttpServletRequest request, Map<String, Object> jsonMap) {
        if (request instanceof WrappedJsonBodyHttpServletRequest) {
            WrappedJsonBodyHttpServletRequest wrapper = (WrappedJsonBodyHttpServletRequest) request;
            return new WrappedJsonBodyHttpServletRequest(wrapper, jsonMap);
        }
        return new WrappedJsonBodyHttpServletRequest(request, jsonMap);
    }

    /**
     * json to paramMap
     *
     * @param jsonMap
     * @return
     */
    public static Map<String, String[]> toRequestMap(Map<String, Object> jsonMap) {
        Map<String, String[]> map = Maps.newHashMap();
        if (jsonMap != null) {
            for (String key : jsonMap.keySet()) {
                Object value = jsonMap.get(key);
                if (value != null && value instanceof String) {
                    map.put(key, new String[]{(String) value});
                } else if (value != null) {
                    map.put(key, new String[]{JSON.toJSONString(value)});
                }
            }
        }
        return map;
    }

    /**
     * json转化
     *
     * @param json
     * @return
     */
    public static Map<String, Object> toJsonMap(String json) {
        Map<String, Object> jsonMap = Maps.newHashMap();
        if (StringUtils.isNotEmpty(json)) {
            try {
                Map<String, Object> _jsonMap = JSON.parseObject(json);
                if (MapUtils.isNotEmpty(_jsonMap)) {
                    jsonMap.putAll(_jsonMap);
                }
            } catch (Exception e) {
                logger.error("parse json error: json={}", json, e);
            }
        }
        return jsonMap;
    }

    /**
     * to input stream
     *
     * @param jsonMap
     * @return
     */
    public static ServletInputStream toInputStream(Map<String, Object> jsonMap) {
        String jsonBody = JSON.toJSONString(jsonMap);
        return new ByteServletInputStream(jsonBody.getBytes());
    }
}
