package org.crp.flowable.springboot.sample

import org.assertj.core.groups.Tuple
import org.flowable.engine.RuntimeService
import org.flowable.engine.TaskService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat

@AcmeApplicationTest
class HelloWorldProcessGroovyTest {

    @Autowired 
    RuntimeService runtimeService
    @Autowired
    TaskService taskService
    
    @Test
    void 'Follow happy path in Hello World process'() {
        def helloWorldProcess = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey('P001-helloWorld').start()
        def expectedPath = []

        assertThat(helloWorldProcess)
                .isRunning()
                .hasVariableWithValue('initiator', null)
                .activities()
                .extracting('activityId')
                .containsExactlyInAnyOrderElementsOf(expectedPath += ['theStart', 'theStart-theSayHelloUserTask', 'theSayHelloUserTask'])
        assertThat(helloWorldProcess)
                .userTasks()
                .extracting('name', 'taskDefinitionKey')
                .containsExactly(new Tuple('Say hello world', 'theSayHelloUserTask'))

        def userTask = taskService.createTaskQuery().processInstanceId(helloWorldProcess.getId()).singleResult()
        taskService.complete(userTask.getId(), ['greeting': 'Hello World!'])

        assertThat(helloWorldProcess)
                .as('The hello world process is finished, no instance is present in the runtime.')
                .doesNotExist()
                .inHistory()
                .as('The hello world process must be present in the history and finished')
                .isFinished()
                .variables()
                .as('All variables must be present in the history')
                .extracting('name', 'value')
                .containsExactlyInAnyOrder(new Tuple('initiator', null), new Tuple('greeting', 'Hello World!'))
        assertThat(helloWorldProcess).inHistory()
                .as('The hello world process must pass through all activities')
                .activities()
                .extracting('activityId')
                .containsExactlyInAnyOrderElementsOf(expectedPath + ["theSayHelloUserTask-theEnd", "theEnd"])
    }
}