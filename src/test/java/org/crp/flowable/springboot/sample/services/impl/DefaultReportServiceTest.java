package org.crp.flowable.springboot.sample.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.crp.flowable.springboot.sample.AcmeApplicationTest;
import org.crp.flowable.springboot.sample.services.ContractService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat;

@AcmeApplicationTest
class DefaultReportServiceTest {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DefaultReportService reportService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    ContractService mockContractService;

    @Test
    void reportForRunningInstance() throws JsonProcessingException {
        Mockito.when(mockContractService.getContract("reportForRunningInstance")).thenReturn(
                objectMapper.readTree("""
                        {
                            "account" : { "owner" : "jlong", "id":"CNTR-reportForRunningInstance" },
                            "maxAmount": 10000
                        }
                        """)
        );
        runtimeService.createProcessInstanceBuilder().processDefinitionKey("P002-processInsuranceEvent")
                .variable("contractId", "reportForRunningInstance")
                .variable("requestedAmount", 123)
                .variable("eventDescription", "I broke my leg.")
                .start();

        assertThat(reportService.accountAmountReport())
                .as("Running process instance does not have any amount yet.")
                .contains(
                objectMapper.createObjectNode().put("account", "CNTR-reportForRunningInstance").put("amount", 0)
        );
    }
    @Test
    void reportForFinishedInstance() throws JsonProcessingException {
        Mockito.when(mockContractService.getContract("reportForFinishedInstance")).thenReturn(
                objectMapper.readTree("""
                        {
                            "account" : { "owner" : "jlong", "id":"CNTR-reportForFinishedInstance" },
                            "maxAmount": 10000
                        }
                        """)
        );
        ProcessInstance insuranceEventProcess = runtimeService.createProcessInstanceBuilder().processDefinitionKey("P002-processInsuranceEvent")
                .variable("contractId", "reportForFinishedInstance")
                .variable("requestedAmount", 123)
                .variable("eventDescription", "I broke my leg.")
                .start();
        Task assessmentTask = taskService.createTaskQuery().processInstanceId(insuranceEventProcess.getId()).singleResult();
        taskService.complete(assessmentTask.getId(), Map.of("amount", 321));

        assertThat(reportService.accountAmountReport()).contains(
                objectMapper.createObjectNode().put("account", "CNTR-reportForFinishedInstance").put("amount", 321)
        );
    }
}