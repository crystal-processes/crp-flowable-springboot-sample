package org.crp.flowable.springboot.sample.model;

import org.crp.flowable.springboot.sample.AcmeApplicationTest;
import org.crp.flowable.springboot.sample.entities.jpa.AccountEntity;
import org.crp.flowable.springboot.sample.entities.jpa.AccountRepository;
import org.crp.flowable.springboot.sample.entities.jpa.ContractEntity;
import org.crp.flowable.springboot.sample.entities.jpa.ContractRepository;
import org.crp.flowable.springboot.sample.entities.jpa.InsuranceEventRepository;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.test.JobTestHelper;
import org.flowable.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat;


@AcmeApplicationTest
public class MigrationProcessTest {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private ContractRepository contractRepository;
	@Autowired
	private InsuranceEventRepository insuranceEventRepository;

	@BeforeEach
	void initializeEntities() {
		var accountEntity = accountRepository.save(AccountEntity.builder()
				.number("1234567")
				.owner("jlong")
				.build()
		);
		contractRepository.save(
				ContractEntity.builder()
						.maxAmount(10_000)
						.contractId("testContractId")
						.account(accountEntity).build()
		);
	}

	@AfterEach
	void removeEntities() {
		insuranceEventRepository.deleteAll();
		contractRepository.deleteAll();
		accountRepository.deleteAll();
	}

    @Test
    void processDefinitionIsDeployed(RepositoryService repositoryService) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("A001-migration")
                .singleResult();

        assertThat(processDefinition).isNotNull();
        assertThat(processDefinition.getKey()).isEqualTo("A001-migration");
        assertThat(processDefinition.getName()).isEqualTo("Migration Process");
        assertThat(processDefinition.isSuspended()).isFalse();
    }

	@Test
	void processToMigrateHasVersion(ProcessEngineConfiguration configuration, RepositoryService repositoryService) {
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey("P001-helloWorld")
				.singleResult();

		assertThat(processDefinition).isNotNull();
	}

	@Test
    void migrationExecutesOnApplicationStart(ProcessEngineConfiguration configuration, HistoryService historyService) {
        JobTestHelper.waitForJobExecutorOnCondition(
                configuration, 10_000, 200,
                () -> historyService.createHistoricProcessInstanceQuery().processDefinitionKey("A001-migration").count() == 1);

		HistoricProcessInstance latestMigrationProcess = historyService.createHistoricProcessInstanceQuery()
				.processDefinitionKey("A001-migration").singleResult();

		assertThat(latestMigrationProcess)
				.as("Migration process has to be started automatically.")
				.isFinished();

		assertThat(configuration.getManagementService().getProperties()).containsEntry("acme.model.version", "0.3.1");
    }

}
