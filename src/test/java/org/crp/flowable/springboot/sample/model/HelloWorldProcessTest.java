package org.crp.flowable.springboot.sample.model;

import org.assertj.core.groups.Tuple;
import org.crp.flowable.springboot.sample.AcmeApplicationTest;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat;

@AcmeApplicationTest
public class HelloWorldProcessTest {

    @Test
    void followHappyPathWithFluentAssertions(RuntimeService runtimeService, TaskService taskService) {
        ProcessInstance helloWorldProcess = runtimeService.createProcessInstanceBuilder().processDefinitionKey("P001-helloWorld").start();

        List<String> path = new ArrayList<>(List.of("theStart", "theStart-theSayHelloUserTask", "theSayHelloUserTask"));
        assertThat(helloWorldProcess)
                .isRunning()
                .hasVariableWithValue("initiator", null)
                .activities()
                .extracting(ActivityInstance::getActivityId)
                .containsExactlyInAnyOrderElementsOf(path);
        assertThat(helloWorldProcess)
                .userTasks()
                .extracting("name", "taskDefinitionKey")
                .containsExactly(Tuple.tuple("Say hello world", "theSayHelloUserTask"));

        Task userTask = taskService.createTaskQuery().processInstanceId(helloWorldProcess.getId()).singleResult();
        taskService.complete(userTask.getId(), Map.of("greeting", "Hello World!"));

        assertThat(helloWorldProcess)
                .as("The hello world process is finished, no instance is present in the runtime.")
                .doesNotExist()
                .inHistory()
                .as("The hello world process must be present in the history and finished")
                .isFinished()
                .variables()
                .as("All variables must be present in the history")
                .extracting("name", "value")
                .containsExactlyInAnyOrder(Tuple.tuple("initiator", null), Tuple.tuple("greeting", "Hello World!"));
        path.addAll(List.of("theSayHelloUserTask-theEnd", "theEnd"));
        assertThat(helloWorldProcess).inHistory()
                .as("The hello world process must pass through all activities")
                .activities()
                .extracting(HistoricActivityInstance::getActivityId)
                .containsExactlyInAnyOrderElementsOf(path);
    }

    @Test
    void followHappyPath(RuntimeService runtimeService, TaskService taskService, HistoryService historyService) {
        ProcessInstance helloWorldProcess = runtimeService.createProcessInstanceBuilder().processDefinitionKey("P001-helloWorld").start();

        assertThat(helloWorldProcess).isNotNull();
        List<String> path = new ArrayList<>(List.of("theStart", "theStart-theSayHelloUserTask", "theSayHelloUserTask"));
        assertThat(runtimeService.createActivityInstanceQuery()
                .processInstanceId(helloWorldProcess.getId()).orderByActivityInstanceStartTime().asc().list())
                .as("The hello world process has to go directly through theStart -> theSayHelloUserTask")
                .extracting(ActivityInstance::getActivityId)
                .containsExactlyInAnyOrderElementsOf(path);
        assertThat(runtimeService.createVariableInstanceQuery().processInstanceId(helloWorldProcess.getId()).list())
                .extracting("name", "value")
                .containsExactly(Tuple.tuple("initiator", null));

        Task userTask = taskService.createTaskQuery().processInstanceId(helloWorldProcess.getId()).singleResult();

        assertThat(userTask).extracting(Task::getName).isEqualTo("Say hello world");
        assertThat(userTask).extracting(Task::getTaskDefinitionKey).isEqualTo("theSayHelloUserTask");

        taskService.complete(userTask.getId(), Map.of("greeting", "Hello World!"));

        assertThat(runtimeService.createActivityInstanceQuery().processInstanceId(helloWorldProcess.getId()).singleResult())
                .as("The hello world process is finished, no instance is present in the runtime.")
                .isNull();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(helloWorldProcess.getId()).singleResult();
        assertThat(historicProcessInstance)
                .as("The hello world process must be present in the history")
                .isNotNull();
        assertThat(historicProcessInstance.getEndTime())
                .as("The hello world process must be finished")
                .isNotNull();
        assertThat(historyService.createHistoricVariableInstanceQuery().processInstanceId(helloWorldProcess.getId()).list())
                .as("All variables must be present in the history")
                .extracting("name", "value")
                .containsExactlyInAnyOrder(Tuple.tuple("initiator", null), Tuple.tuple("greeting", "Hello World!"));
        path.addAll(List.of("theSayHelloUserTask-theEnd", "theEnd"));
        assertThat(historyService.createHistoricActivityInstanceQuery().processInstanceId(helloWorldProcess.getId()).orderByHistoricActivityInstanceStartTime().asc().list())
                .as("The hello world process must pass through all activities")
                .extracting(HistoricActivityInstance::getActivityId)
                .containsExactlyInAnyOrderElementsOf(path);
    }
}
