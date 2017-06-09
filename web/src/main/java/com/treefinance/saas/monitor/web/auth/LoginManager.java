package com.treefinance.saas.monitor.web.auth;

import com.treefinance.saas.monitor.common.domain.vo.LoginUserVO;
import org.springframework.stereotype.Component;

/**
 * Created by yh-treefinance on 2017/5/24.
 */
@Component
public class LoginManager {

    /**
     * 根据token获取用户
     * @return
     */
    public LoginUserVO getUserByToken(String token){
        return new LoginUserVO();
    }
}
