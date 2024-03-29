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

import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.common.domain.Result;
import com.treefinance.saas.monitor.web.auth.exception.ForbiddenException;
import com.treefinance.toolkit.util.http.servlet.ServletRequests;
import com.treefinance.toolkit.util.http.servlet.ServletResponses;
import com.treefinance.toolkit.util.json.Jackson;
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
 * @author <A HREF="mailto:yaojun@datatrees.com.cn">Jun Yao</A>
 * @version 1.0
 * @since 2017年3月06日 上午10:12:41
 */
@ControllerAdvice
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

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
//        StringBuilder logBuffer = new StringBuilder();
//        if (request != null) {
//            logBuffer.append("request method=").append(request.getMethod());
//            logBuffer.append(",url=").append(request.getRequestURL());
//        }
//        if (ex != null) {
//            logBuffer.append(",exception:").append(ex);
//        }
//        logger.error(logBuffer.toString(), ex);
        logger.error("{} of request url={},ip={}", request.getMethod(), request.getRequestURI(),
            ServletRequests.getIP(request), ex);

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
        ServletResponses.responseJson(response, httpStatus.value(), responseBody);
    }

}
