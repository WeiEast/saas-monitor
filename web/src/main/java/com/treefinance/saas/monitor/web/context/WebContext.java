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

package com.treefinance.saas.monitor.web.context;

import com.treefinance.saas.monitor.common.domain.vo.LoginUserVO;

/**
 * @author Jerry
 * @since 14:17 26/04/2017
 */
public final class WebContext {

  private LoginUserVO user;
  private String ip;

  public WebContext() {
  }

  public LoginUserVO getUser() {
    return user;
  }

  public void setUser(LoginUserVO user) {
    this.user = user;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

}
