package com.treefinance.saas.monitor.common.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luoyihua on 2017/5/2.
 */
        public final class IpUtils {
    private static final Pattern IP_PATTERN = Pattern.compile("([0-9]{1,3}\\.){3}[0-9]{1,3}");

    private IpUtils() {}

    /**
     * 从请求头中提取请求来源IP
     *
     * @param request 请求对象 {@link HttpServletRequest}
     * @return IP Address
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if (StringUtils.isNotBlank(ip)) {
            try {
                Matcher m = IP_PATTERN.matcher(ip);
                if (m.find()) {
                    ip = m.group();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
