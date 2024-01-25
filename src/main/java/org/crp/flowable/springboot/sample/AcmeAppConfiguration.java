package org.crp.flowable.springboot.sample;

import org.crp.flowable.springboot.sample.services.ContractService;
import org.crp.flowable.springboot.sample.services.InsuranceEventService;
import org.crp.flowable.springboot.sample.services.MoneyService;
import org.crp.flowable.springboot.sample.services.ReportService;
import org.crp.flowable.springboot.sample.services.impl.DefaultContractService;
import org.crp.flowable.springboot.sample.services.impl.DefaultInsuranceEventService;
import org.crp.flowable.springboot.sample.services.impl.DefaultMoneyService;
import org.crp.flowable.springboot.sample.services.impl.DefaultReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AcmeAppConfiguration {

    @Bean
    ContractService contractService() {
        return new DefaultContractService();
    }

    @Bean
    MoneyService moneyService() {
        return new DefaultMoneyService();
    }

    @Bean
    ReportService reportService() {
        return new DefaultReportService();
    }

    @Bean
    InsuranceEventService insuranceEventService() {
        return new DefaultInsuranceEventService();
    }
}
