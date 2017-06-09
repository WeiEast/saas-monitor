/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.monitor.web.filter;

import com.datatrees.toolkits.util.http.servlet.ServletRequestUtils;
import com.datatrees.toolkits.util.http.servlet.ServletResponseUtils;
import com.datatrees.toolkits.util.json.Jackson;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.common.domain.Result;
import com.treefinance.saas.monitor.common.domain.vo.LoginUserVO;
import com.treefinance.saas.monitor.web.auth.LoginManager;
import com.treefinance.saas.monitor.web.context.WebContext;
import com.treefinance.saas.monitor.web.auth.exception.ForbiddenException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Jerry
 * @since 14:22 26/04/2017
 */

public class WebContextFilter extends AbstractRequestFilter {

  public static final String WEB_CONTEXT_ATTRIBUTE = "WebContext";
  //已经验证的token
  private static final String LOGIN_TOKEN = "login_token";
  // 用户登录管理
  private LoginManager loginManager;

  @Override
  protected void initFilterBean(FilterConfig filterConfig) throws ServletException {
    WebApplicationContext webApplicationContext =
        WebApplicationContextUtils.findWebApplicationContext(filterConfig.getServletContext());

    if (webApplicationContext == null) {
      throw new ServletException("Web application context failed to init...");
    }

    LoginManager loginManager = webApplicationContext.getBean(LoginManager.class);
    if (loginManager == null) {
      throw new ServletException("Can't find the instance of the class 'LoginManager'");
    }

    this.loginManager = loginManager;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = request.getHeader(LOGIN_TOKEN);
      if (token == null) {
        throw new ForbiddenException("Can not find header 'appid' in request.");
      }

      if (StringUtils.isBlank(token)) {
        throw new ForbiddenException("Invalid app id.");
      }
      String ip = null;
      try {
        ip = ServletRequestUtils.getIP(request);
        logger.error(String
            .format("@[%s;%s;%s] >> token: %s", request.getRequestURI(), request.getMethod(), ip,
                    token));
      } catch (Exception e) {
        logger.warn(e.getMessage(), e);
        logger.error(String
            .format("@[%s;%s] >> token: %s", request.getRequestURI(), request.getMethod(), token));
      }

      request.setAttribute(WEB_CONTEXT_ATTRIBUTE, createWebContext(token, ip));

      try {
        filterChain.doFilter(request, response);
      } finally {
        request.removeAttribute(WEB_CONTEXT_ATTRIBUTE);
      }
    } catch (ForbiddenException e) {
      forbidden(request, response, e);
    }
  }

  private WebContext createWebContext(String token, String ip) throws ForbiddenException {
    LoginUserVO user = loginManager.getUserByToken(token);

    WebContext context = new WebContext();
    context.setUser(user);
    context.setIp(ip);

    return context;
  }

  private void forbidden(HttpServletRequest request, HttpServletResponse response,
      ForbiddenException e) {
    logger.error(String.format("@[%s;%s;%s] >> %s", request.getRequestURI(), request.getMethod(),
        ServletRequestUtils.getIP(request), e.getMessage()));

    Map map = Maps.newHashMap();
    map.put("mark", 0);
    Result result = new Result(map);
    result.setErrorMsg("用户未登录");
    String responseBody = Jackson.toJSONString(result);
    ServletResponseUtils.responseJson(response, 403, responseBody);
  }
}
