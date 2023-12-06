[version: 0.0.2](https://github.com/crystal-processes/crp-flowable-springboot-sample/releases/tag/crp-flowable-springboot-sample-0.0.2)

[tag: crp-flowable-springboot-sample-0.0.2](https://github.com/crystal-processes/crp-flowable-springboot-sample/releases/tag/crp-flowable-springboot-sample-0.0.2)

## What was done
The [acme application model](../src/main/resources/apps/acme-bar.zip) was created in flowable cloud designer.
![Initial acme application](images/initial-acme-app.png)
Exported [acme-bar.zip](https://github.com/crystal-processes/crp-flowable-springboot-sample/commit/72b6657490c2740d86d083314ab11941cacf2ba7#diff-f560d6ffb8aab7489c1d92d4076d095a9a5052538e803572e31e6e7d3f18fc53) file was added to `src/main/resources/apps/` directory for automated deployment.
The happy path jUnit test was added in the following [commit](https://github.com/crystal-processes/crp-flowable-springboot-sample/commit/72b6657490c2740d86d083314ab11941cacf2ba7#diff-5c80c6fe57043ccfeb87e4a7eefa23903f8d7a7ab61cf8e6298ab3bc28dbdb2d).

## What to do next
Assertions are not readable. It is unclear, what is real flowable API call and what is assertion. 
If you read [the whole test class](https://github.com/crystal-processes/crp-flowable-springboot-sample/commit/72b6657490c2740d86d083314ab11941cacf2ba7#diff-5c80c6fe57043ccfeb87e4a7eefa23903f8d7a7ab61cf8e6298ab3bc28dbdb2d), it is even worse.

## Flowable fluent assertions
[The benefit is obvious](https://github.com/crystal-processes/crp-flowable-springboot-sample/commit/89fbdf46cea2e2235e8e34dc86ffe960d2584667):

### Before
```java
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
``` 

### After with fluent assertions
```java
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
```