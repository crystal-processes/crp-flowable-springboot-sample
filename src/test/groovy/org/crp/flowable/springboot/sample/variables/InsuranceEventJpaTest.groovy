package org.crp.flowable.springboot.sample.variables

import jakarta.transaction.Transactional
import org.crp.flowable.springboot.sample.AcmeApplicationTest
import org.crp.flowable.springboot.sample.entities.jpa.AccountEntity
import org.crp.flowable.springboot.sample.entities.jpa.AccountRepository
import org.crp.flowable.springboot.sample.entities.jpa.ContractEntity
import org.crp.flowable.springboot.sample.entities.jpa.ContractRepository
import org.flowable.engine.RuntimeService
import org.flowable.engine.TaskService
import org.flowable.engine.runtime.ProcessInstance
import org.flowable.task.api.Task
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat

@AcmeApplicationTest
class InsuranceEventJpaTest {

    @Autowired
    RuntimeService runtimeService
    @Autowired
    TaskService taskService
    @Autowired
    ContractRepository contractRepository
    @Autowired
    AccountRepository accountRepository

    @BeforeEach
    @Transactional
    void 'initialize entities'() {
        def accountEntity = accountRepository.save(AccountEntity.builder()
                .number("1234567")
                .owner('jlong')
                .build()
        )
        contractRepository.save(
        ContractEntity.builder()
                .maxAmount(10000)
                .contractId('testContractId')
                .account(accountEntity).build()
        )
        contractRepository.flush()
    }

    @AfterEach
    @Transactional
    void 'remove entities'() {
        accountRepository.deleteAll()
        contractRepository.deleteAll()
    }

    @Test
    void 'end to end with jpa objects'() {
        ProcessInstance insuranceEventProcess = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey("P003-jpaProcessInsuranceEvent")
                .variables(['contractId':'testContractId',
                    'requestedAmount': 10, 
                    'eventDescription': 'I broke my leg.'])
                .start()

        assertThat(insuranceEventProcess).isRunning()
                .userTasks().extracting('name').containsExactly('Assess event')
        assertThat(insuranceEventProcess)
                .hasVariable('contract')
                .variables()
                .filteredOn {'contract' == it.getName() }
                .extracting('typeName')
                .containsOnly('jpa-entity')

        Task assessmentTask = taskService.createTaskQuery().processInstanceId(insuranceEventProcess.getId()).singleResult()
        taskService.complete(assessmentTask.getId(), ['amount': 5])

        assertThat(insuranceEventProcess).doesNotExist()
                .inHistory().isFinished()
                .hasVariableWithValue('amount', 5)
    }
}
