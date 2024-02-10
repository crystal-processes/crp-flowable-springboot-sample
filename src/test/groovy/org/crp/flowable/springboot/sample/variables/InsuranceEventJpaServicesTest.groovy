package org.crp.flowable.springboot.sample.variables

import jakarta.transaction.Transactional
import org.crp.flowable.springboot.sample.AcmeApplicationTest
import org.crp.flowable.springboot.sample.entities.jpa.*
import org.crp.flowable.springboot.sample.services.MoneyService
import org.flowable.engine.RuntimeService
import org.flowable.engine.TaskService
import org.flowable.engine.runtime.ProcessInstance
import org.flowable.task.api.Task
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat
import static org.mockito.Mockito.times

@AcmeApplicationTest
class InsuranceEventJpaServicesTest {

    @Autowired
    RuntimeService runtimeService
    @Autowired
    TaskService taskService
    @Autowired
    ContractRepository contractRepository
    @Autowired
    AccountRepository accountRepository
    @Autowired
    InsuranceEventRepository insuranceEventRepository

    @MockBean
    MoneyService moneyService

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
                .maxAmount(10_000)
                .contractId('testContractId')
                .account(accountEntity).build()
        )
    }

    @AfterEach
    @Transactional
    void 'remove entities'() {
        insuranceEventRepository.deleteAll()
        accountRepository.deleteAll()
        contractRepository.deleteAll()
    }

    @Test
    void 'end to end with jpa object and services'() {
        ProcessInstance insuranceEventProcess = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey("P004-jpaServicesProcessInsuranceEvent")
                .transientVariables(['contractId':'testContractId',
                    'requestedAmount': 10, 
                    'eventDescription': 'I broke my leg.'])
                .start()

        def assertThatInsuranceEventProcess = assertThat(insuranceEventProcess)
        assertThatInsuranceEventProcess.isRunning()
                .userTasks().extracting('name').containsExactly('Assess event')

        Task assessmentTask = taskService.createTaskQuery().processInstanceId(insuranceEventProcess.getId()).includeProcessVariables().singleResult()
        taskService.complete(assessmentTask.getId(), null, ['amount' : 5])

        assertThatInsuranceEventProcess.doesNotExist()
                .inHistory().isFinished()
        Mockito.verify(moneyService, times(1)).sendMoney("1234567", 5)
    }
}
