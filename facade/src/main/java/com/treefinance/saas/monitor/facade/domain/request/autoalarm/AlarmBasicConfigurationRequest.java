package com.treefinance.saas.monitor.facade.domain.request.autoalarm;

import com.treefinance.saas.monitor.facade.domain.base.PageRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 预警配置管理
 *
 * @author haojiahong
 * @date 2018/7/19
 */
@Setter
@Getter
public class AlarmBasicConfigurationRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 977996390384737005L;

    /**配置管理列表搜索-配置名称*/
    private String name;
    /**执行环境*/
    private String runEnv;

}
