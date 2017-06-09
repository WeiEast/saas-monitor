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

package com.treefinance.saas.monitor.common.cache;

import java.util.List;

public interface RedisService {

  boolean saveString(final String key, final String value);

  boolean saveString(String key, String value, int ttlSeconds);

  String getString(final String key);

  boolean saveListString(final String key, final List<String> value);

  String getStringFromList(final String key);

  boolean deleteKey(String key);
}
