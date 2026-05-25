---
name: crp-flowable-developer
description: Perform business process management development in java and flowable.
license: APACHE 2.0
compatibility: flowable, java
user-invocable: true
---

# BPM Development Skill 

This skill implements flowable models and code (mostly in java, groovy and flowable expressions). 

# Rules
Use flowable custom extensions in the models. 
Treat models as a code. Keep models minimal and clean as code. Martin Fowler's Clean Code is applicable on models too.
Test models comprehensively. Use [crp-flowable-assert](https://github.com/crystal-processes/crp-flowable-ex/tree/main/crp-flowable-assert) for flowable entities assertions.  

Always compare models in the source with results provided from the flowable MCP tools in the running environments 
(e.g. *maxVariablesPerProcessDefinition, *variableTypes, *deadLetterJobs, *failingRuntimeJobs). The id in the model 
is the same as definitionKey for deployed definition. "A definition is the deployed BPMN model in the engine. For example, 
the process engine contains a process definition, which is the deployed BPMN model. Definition is immutable and represents 
the model which was deployed to engine in given time. How models evolve, one definitionKey is associated with several 
definitions which differ in version. The different kind of models are grouped together into a deployment.

If you do changes in the model, which is already in production, take into account instance migration. 
[The example of process instance migration project.](https://github.com/crystal-processes/crp-sample-upgrade-test). 
Flowable does not migrate instances automatically. Use `org.flowable.engine.migration.ProcessInstanceMigrationDocument`
to implement process instance migration. Use `org.flowable.engine.ProcessMigrationService` to execute migration.
Prefer to use bpmn process to migrate (be aware of long transactions) for auditability.  

Use flowable functions e.g. ${var:get('index')} in expressions. 
Simplicity over complexity = if expression is too complex,
use script or JavaDelegate. If script or java delegate is too simple, use expression instead.

# Most common issues

1. **Flowable variables are overused.** There are too many variables in the process instance. The variable types are complex
   (bytes, serializable, longString, jpa-entity-list, json). Try to propose JPA entities instead of too many or complex 
   process variables. 
   The bpmn process works on the top of the JPA entity and changes the state of the entity. Keep process execution 
   minimal. Long-running process instances are hard to maintain. The upgrade of the process definition in such a case 
   is too complex.
2. Use java **services** to access JPA entities.
3. **Models are hard to read.** Make models as small and readable as clean code. The approximate limits are 10 nodes 
   per process model. The process instance should not have more than 10 variables.
4. **Lack of error handling in async executions.** Asynchronous execution (e.g. timers, async continuation) creates a job. 
   The job is executed by jobExecutor in the separate thread. If execution fails, the job is moved to ACT_DEADLETTER_JOB 
   table. The execution won't continue till the job is not moved to executable jobs again. It is common source of critical errors.
5. **Long-running transactions.** The transaction starts on the event. The event examples: process started, user task completed,
   signal received, time reached timer's due date... . The transaction ends when the wait state is reached. The wait state examples:
   async continuation, user task, timer, catching events. The loops, multi instance and batch processing 
   are common causes of long transactions.

# Best Practices

1. **Modularity**: Break down complex processes into smaller, reusable sub-processes.
2. **Documentation**: Document each process, task, and gateway with clear descriptions.
3. **Testing**: Implement unit and integration tests for BPMN models and associated services.
4. **Performance**: Optimize process models for performance by avoiding unnecessary gateways or tasks. Short transactions.

# Examples

## jUnit test with crp-flowable-assert

```java
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
```
