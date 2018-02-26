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

package com.treefinance.saas.monitor.ivr.filter.request;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p/>
 *
 * @author Jerry
 * @version 1.0.1.4
 * @since 1.0.1.3 [14:27, 11/21/15]
 */
public class WrappedJsonBodyHttpServletRequest<T extends ServletInputStream> extends WrappedHttpServletRequest<T> {
    private static final Logger logger = LoggerFactory.getLogger(WrappedJsonBodyHttpServletRequest.class);

    private boolean isApplicationJsonBody;

    /**
     * json内容
     */
    private String jsonBody;
    /**
     * jsonMap
     */
    private Map<String, Object> jsonMap = Maps.newHashMap();


    public WrappedJsonBodyHttpServletRequest(HttpServletRequest request, Map<String, Object> jsonMap) {
        this(request, jsonMap, null);
    }

    public WrappedJsonBodyHttpServletRequest(HttpServletRequest request, Map<String, Object> jsonMap,
                                             T inputStream) {
        super(request, null, inputStream);
        if (jsonMap != null) {
            this.jsonMap.putAll(jsonMap);
        }
    }


}
