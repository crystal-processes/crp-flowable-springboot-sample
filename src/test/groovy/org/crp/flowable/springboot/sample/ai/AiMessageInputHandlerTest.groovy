package org.crp.flowable.springboot.sample.ai

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.GreenMailUtil
import com.icegreen.greenmail.util.ServerSetupTest
import jline.internal.Urls
import org.awaitility.Awaitility
import org.crp.flowable.springboot.sample.AcmeApplicationTest
import org.crp.flowable.springboot.sample.entities.jpa.AccountEntity
import org.crp.flowable.springboot.sample.entities.jpa.AccountRepository
import org.crp.flowable.springboot.sample.entities.jpa.ContractEntity
import org.crp.flowable.springboot.sample.entities.jpa.ContractRepository
import org.flowable.engine.RuntimeService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.Mockito
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.converter.StructuredOutputConverter
import org.springframework.beans.factory.annotation.Autowired

import java.time.Duration

import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyString

@AcmeApplicationTest
class AiMessageInputHandlerTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP_IMAP)
            .withConfiguration(GreenMailConfiguration.aConfig()
                    .withUser("request@acme.com", "acme", "test")
                    .withUser("homer.simpson@localhost", "homer", "test"))
            .withPerMethodLifecycle(false);

    @Autowired
    ChatClient chatClient
    @Autowired
    ObjectMapper objectMapper
    @Autowired
    AccountRepository accountRepository
    @Autowired
    ContractRepository contractRepository

    @BeforeEach
    void 'create valid contract'() {
        def accountEntity = accountRepository.save(AccountEntity.builder()
                .number("1234567")
                .owner('homer')
                .build()
        )
        contractRepository.save(
                ContractEntity.builder()
                        .maxAmount(10_000)
                        .contractId('HI-123421321')
                        .account(accountEntity).build()
        )
    }

    @AfterEach
    void 'clean up context'(RuntimeService runtimeService) {
        runtimeService.createProcessInstanceQuery().list().forEach {
            process -> {
                try {
                    runtimeService.deleteProcessInstance(process.getId(), 'After test context clean up')
                } catch (Throwable e) {
                    //swallow any exception
                }
            }
        }

        contractRepository.deleteAll()
    }

    @Test
    void 'recognize One Process'(RuntimeService runtimeService) {
        configureChatClientMockAnswer('classpath:/chatClient-happy-response.json')
        GreenMailUtil.sendTextEmailTest('request@acme.com', 'homer.simpson@localhost',
                'I broke my leg',
                '''
                        I broke my leg when I moved to my new house in Krakow. The health care fee was 200 eur. I want to refund the fee from my health insurance. The contract id is HI-123421321.
                        
                        Regards
                        Homer
                        ''')

        Awaitility.await("Process was started.")
                .timeout(Duration.ofSeconds(10)).until(
                () ->
                    runtimeService.createProcessInstanceQuery().processDefinitionKey('P005-aiMessageInputHandler').count() == 1 &&
                    runtimeService.createProcessInstanceQuery().processDefinitionKey('P004-jpaServicesProcessInsuranceEvent').count() == 1 &&
                            runtimeService.createProcessInstanceQuery().count() == 2
        )
        Awaitility.await("mail was sent.")
                .timeout(Duration.ofSeconds(10)).until(
                () -> {
                    def messages = AiMessageInputHandlerTest.greenMail.getReceivedMessagesForDomain("localhost")
                    messages.size() > 0 && messages[0].getFrom()[0].toString().equals('request@acme.com')
                }
        )
    }

    @Test
    void 'no process recognized'(RuntimeService runtimeService) {
        configureChatClientMockAnswer("classpath:/chatClient-happy-response.json")
        GreenMailUtil.sendTextEmailTest("request@acme.com", "homer.simpson@localhost",
                "Such a beautiful day",
                """
                        Hello acme, 
                        It is a beautiful day today in my new house in Krakow.

                        Homer
                        """)

        Awaitility.await("Email handling process was started only.")
                .timeout(Duration.ofSeconds(10)).until(
                () ->
                        runtimeService.createProcessInstanceQuery().processDefinitionKey('P004-jpaServicesProcessInsuranceEvent').count() == 1
        )
        Awaitility.await("mail was sent.")
                .timeout(Duration.ofSeconds(10)).until(
                () -> {
                    def messages = AiMessageInputHandlerTest.greenMail.getReceivedMessagesForDomain("localhost")
                    messages.size() > 0 && messages[0].getFrom()[0].toString().equals('request@acme.com')
                }
        )

    }
    private void setGreenMailUsers() {
        greenMail.setUser('request@acme.com', 'acme', 'test')
        greenMail.setUser('homer.simpson@localhost', 'homer.simson', 'test')
    }

    private void configureChatClientMockAnswer(String answerFile) {
        ChatClient.CallResponseSpec response = Mockito.mock(ChatClient.CallResponseSpec)
        ChatClient.ChatClientRequestSpec request = Mockito.mock(ChatClient.ChatClientRequestSpec)
        Mockito.when(chatClient.prompt()).thenReturn(request)
        Mockito.when(request.system(anyString())).thenReturn(request)
        Mockito.when(request.user(anyString())).thenReturn(request)
        Mockito.when(request.call()).thenReturn(response)
        Mockito.when(response.entity(any(StructuredOutputConverter.class))).thenReturn(objectMapper.readValue(Urls.create(answerFile), JsonNode.class))
    }
}
