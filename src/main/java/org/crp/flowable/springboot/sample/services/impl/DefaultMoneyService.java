package org.crp.flowable.springboot.sample.services.impl;

import org.crp.flowable.springboot.sample.services.MoneyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMoneyService implements MoneyService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultMoneyService.class);

    @Override
    public void sendMoney(String account, int amount) {
        LOG.info("Money send to ${} -> {}.", amount, account);
    }
}
