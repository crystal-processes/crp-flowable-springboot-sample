package org.crp.flowable.springboot.sample;

import io.micrometer.observation.ObservationRegistry;
import org.crp.flowable.springboot.sample.services.ContractService;
import org.crp.flowable.springboot.sample.services.InsuranceEventService;
import org.crp.flowable.springboot.sample.services.MoneyService;
import org.crp.flowable.springboot.sample.services.ReportService;
import org.crp.flowable.springboot.sample.services.impl.DefaultContractService;
import org.crp.flowable.springboot.sample.services.impl.DefaultInsuranceEventService;
import org.crp.flowable.springboot.sample.services.impl.DefaultMoneyService;
import org.crp.flowable.springboot.sample.services.impl.DefaultReportService;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.ai.vectorstore.pgvector.IdentityAwarePgVectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

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

    @Bean
    BatchingStrategy pgVectorStoreBatchingStrategy() {
        return new TokenCountBatchingStrategy();
    }

    @Bean
    public IdentityAwarePgVectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel, ObjectProvider<ObservationRegistry> observationRegistry, ObjectProvider<VectorStoreObservationConvention> customObservationConvention, BatchingStrategy batchingStrategy) {
        return IdentityAwarePgVectorStore.builder(jdbcTemplate, embeddingModel)
                .idType(IdentityAwarePgVectorStore.DEFAULT_ID_TYPE)
                .distanceType(IdentityAwarePgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .indexType(IdentityAwarePgVectorStore.PgIndexType.HNSW)
                .build();
    }

}
