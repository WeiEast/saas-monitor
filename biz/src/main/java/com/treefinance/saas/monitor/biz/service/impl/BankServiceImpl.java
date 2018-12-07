package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.service.BankService;
import com.treefinance.saas.monitor.common.domain.dto.BankDTO;
import com.treefinance.saas.monitor.context.component.AbstractService;
import com.treefinance.saas.monitor.dao.entity.Bank;
import com.treefinance.saas.monitor.dao.entity.BankCriteria;
import com.treefinance.saas.monitor.dao.mapper.BankMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/19.
 */
@Service("bankService")
public class BankServiceImpl extends AbstractService implements BankService {

    @Autowired
    private BankMapper bankMapper;

    @Override
    public List<BankDTO> queryAll() {
        BankCriteria criteria = new BankCriteria();
        criteria.createCriteria();
        List<Bank> banks = bankMapper.selectByExample(criteria);

        return convert(banks, BankDTO.class);
    }
}
