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
package com.treefinance.saas.monitor.web.advice;

import com.datatrees.toolkits.util.http.servlet.ServletResponseUtils;
import com.datatrees.toolkits.util.json.Jackson;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.common.domain.Result;
import com.treefinance.saas.monitor.exception.TaskTimeOutException;
import com.treefinance.saas.monitor.exception.UnknownException;
import com.treefinance.saas.monitor.web.auth.exception.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.util.Map;

/**
 *
 * @author <A HREF="mailto:yaojun@datatrees.com.cn">Jun Yao</A>
 * @version 1.0
 * @since 2017年3月06日 上午10:12:41
 */
@ControllerAdvice
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    @ExceptionHandler(value = UnknownException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public void handleUnknownException(HttpServletRequest request, UnknownException ex, HttpServletResponse response) {
        responseException(request, ex, HttpStatus.SERVICE_UNAVAILABLE, response);
    }

    @ExceptionHandler(value = TaskTimeOutException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    @ResponseBody
    public void handleTimeoutException(HttpServletRequest request, TaskTimeOutException ex, HttpServletResponse response) {
        responseException(request, ex, HttpStatus.GATEWAY_TIMEOUT, response);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public void handleForbiddenException(HttpServletRequest request, ForbiddenException ex, HttpServletResponse response) {
        responseException(request, ex, HttpStatus.FORBIDDEN, response);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public void handleAllException(HttpServletRequest request, Exception ex, HttpServletResponse response) {
        responseException(request, ex, HttpStatus.INTERNAL_SERVER_ERROR, response);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void handleValidationException(ValidationException ex,
                                                        HttpServletRequest request, HttpServletResponse response) {
        responseException(request, ex, HttpStatus.BAD_REQUEST, response);
    }

    private void handleLog(HttpServletRequest request, Exception ex) {
        StringBuffer logBuffer = new StringBuffer();
        if (request != null) {
            logBuffer.append("request method=" + request.getMethod());
            logBuffer.append(",url=" + request.getRequestURL());
        }
        if (ex != null) {
            logBuffer.append(",exception:" + ex);
        }
        logger.error(logBuffer.toString(), ex);
    }

    private void responseException(HttpServletRequest request, Exception ex, HttpStatus httpStatus, HttpServletResponse response) {
        handleLog(request, ex);
        Result result = new Result();
        if (ex instanceof ForbiddenException) {
            Map map = Maps.newHashMap();
            map.put("mark", 0);
            result.setData(map);
        }
        result.setErrorMsg("系统忙，请稍后重试");//暂时
        //result.setErrorMsg(ex.getMessage());
        String responseBody = Jackson.toJSONString(result);
        ServletResponseUtils.responseJson(response, httpStatus.value(), responseBody);
    }

}
