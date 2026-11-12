package org.crp.flowable.springboot.sample.model;

import org.crp.flowable.springboot.sample.AcmeApplicationTest;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.test.JobTestHelper;
import org.flowable.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.Test;

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat;


@AcmeApplicationTest
public class MigrationProcessTest {

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
    void processExecutesOnApplicationStart(ProcessEngineConfiguration configuration, HistoryService historyService) {
        JobTestHelper.waitForJobExecutorOnCondition(
                configuration, 10_000, 200,
                () -> historyService.createHistoricProcessInstanceQuery().processDefinitionKey("A001-migration").count() == 1);

        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey("A001-migration").singleResult();

		assertThat(historicProcessInstance)
				.as("Migration process has to be started automatically.")
				.isFinished();
    }

}
