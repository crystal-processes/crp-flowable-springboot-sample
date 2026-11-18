package org.crp.flowable.springboot.sample.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.crp.flowable.springboot.sample.services.MoneyService;

/**
 * Default implementation of the MoneyService.
 */
@Slf4j
public class DefaultMoneyService implements MoneyService {

	/**
     * Default constructor for DefaultMoneyService.
     */
    public DefaultMoneyService() {
    }

    @Override
    public void sendMoney(String account, int amount) {
        log.info("Money send to ${} -> {}.", amount, account);
    }
}
