package org.crp.flowable.springboot.sample

import org.assertj.core.groups.Tuple
import org.flowable.engine.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static org.crp.flowable.assertions.CrpFlowableAssertions.assertThat

@AcmeApplicationTest
@ContextConfiguration
class HelloWorldProcessSpec extends Specification {

    @Autowired 
    RuntimeService runtimeService
    @Autowired
    TaskService taskService
    @Autowired
    HistoryService historyService
    @Autowired
    ProcessEngine processEngine

    def setup(){
        ProcessEngines.registerProcessEngine(processEngine)
    }

    def 'Follow happy path in Hello World process'() {
        given:
            def expectedPath = []

        when:
            def helloWorldProcess = runtimeService.createProcessInstanceBuilder()
                    .processDefinitionKey('P001-helloWorld').start()

        then:
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

        when:
            def userTask = taskService.createTaskQuery().processInstanceId(helloWorldProcess.getId()).singleResult()
            taskService.complete(userTask.getId(), ['greeting': 'Hello World!'])

        then:
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