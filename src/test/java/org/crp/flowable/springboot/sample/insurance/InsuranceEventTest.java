package org.crp.flowable.springboot.sample.insurance;


import org.crp.flowable.springboot.sample.AcmeApplicationTest;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat;

@AcmeApplicationTest()
public class InsuranceEventTest {

    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;

    @Autowired
    ProcessEngine processEngine;

    @Test
    void processInsuranceEventEndToEnd() {
        ProcessEngines.getProcessEngines().put(processEngine.getName(), processEngine);
        ProcessInstance insuranceEventProcess = runtimeService.createProcessInstanceBuilder().processDefinitionKey("P002-processInsuranceEvent")
                .variable("contractId", "testContractId")
                .variable("requestedAmount", 10)
                .variable("eventDescription", "I broke my leg.")
                .start();

        assertThat(insuranceEventProcess).isRunning()
                .hasVariable("contract")
                .userTasks().extracting("name").containsExactly("Assess event");

        Task assessmentTask = taskService.createTaskQuery().processInstanceId(insuranceEventProcess.getId()).singleResult();
        taskService.complete(assessmentTask.getId(), Map.of("amount", 5));

        assertThat(insuranceEventProcess).doesNotExist()
                .inHistory().isFinished()
                .hasVariableWithValue("amount", 5);
    }
}
