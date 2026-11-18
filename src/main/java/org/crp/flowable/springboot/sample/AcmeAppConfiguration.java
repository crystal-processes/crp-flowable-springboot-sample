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

/**
 * Spring configuration class for the Acme application.
 * Defines beans for various services used in the application.
 */
@Configuration
public class AcmeAppConfiguration {

    /**
     * Creates and configures the ContractService bean.
     *
     * @return an instance of DefaultContractService
     */
    @Bean
    ContractService contractService() {
        return new DefaultContractService();
    }

    /**
     * Creates and configures the MoneyService bean.
     *
     * @return an instance of DefaultMoneyService
     */
    @Bean
    MoneyService moneyService() {
        return new DefaultMoneyService();
    }

    /**
     * Creates and configures the ReportService bean.
     *
     * @return an instance of DefaultReportService
     */
    @Bean
    ReportService reportService() {
        return new DefaultReportService();
    }

    /**
     * Creates and configures the InsuranceEventService bean.
     *
     * @return an instance of DefaultInsuranceEventService
     */
    @Bean
    InsuranceEventService insuranceEventService() {
        return new DefaultInsuranceEventService();
    }
}
