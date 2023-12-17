package org.crp.flowable.springboot.sample.services;

import org.crp.flowable.springboot.sample.utils.RuntimeUsage;

public interface MoneyService {
    /**
     * Send @amount of money to @account
     *
     * @param account account to send money to.
     * @param amount amount of money to send.
     * @throws RuntimeException in case of any trouble.
     */
    @RuntimeUsage("Used in service task expressions.")
    void sendMoney(String account, int amount);
}
