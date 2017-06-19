package com.treefinance.saas.monitor.biz.service;

import com.treefinance.saas.monitor.common.domain.dto.BankDTO;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/19.
 */
public interface BankService {
    /**
     * 查询所有银行
     * @return
     */
    List<BankDTO> queryAll();
}
