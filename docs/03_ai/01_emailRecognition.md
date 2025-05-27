Process the received email in "acme" insurance company.
### Receive an email
There are many possibilities how to read an email and send it to flowable. I choose (spring-integration-mail)[https://docs.spring.io/spring-integration/reference/mail.html].
Mail listener and message handler are initialized:
https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/58f894e01bf73aab6847230d70de3ff61aecbdd5/src/main/java/org/crp/flowable/springboot/sample/AcmeAiConfiguration.java#L19-L33

The `startProcessMessageHandler` is responsible for sending a message to the proper process.

https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/58f894e01bf73aab6847230d70de3ff61aecbdd5/src/main/java/org/crp/flowable/springboot/sample/services/impl/StartProcessMessageHandler.java#L35-46


### Process model
![process email](P005-aiMessageInputHandler.png)

When the process instance is started with the email content, `Identify processes to start` task is reached. AI comes to the scene now.
The service task is really easy:

https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/58f894e01bf73aab6847230d70de3ff61aecbdd5/src/main/model/acme/P005-aiMessageInputHandler.bpmn#L9-10

`ChatClientJavaDelegate` is responsible for sending the email content to the chat client. We are using (spring-ai)[https://spring.io/projects/spring-ai]
to to integrate.
The chat client `Identify processes to start` task configuration is divided into two obvious parts `system` and `user`.
1. `system` describes (the general purpose of the task)[https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/58f894e01bf73aab6847230d70de3ff61aecbdd5/src/main/model/acme/P005-aiMessageInputHandler.bpmn#L16]
in the human readable way.
2. `user` represents email data received e.g. `from:${message.from[0]}, Subject:${message.subject}, Body: ${message.body}`

The `Identify processes to start` task input is an email content in structured form:
https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/bf8736bc51c6a474674253b96b383935fbb00ced/src/test/groovy/org/crp/flowable/springboot/sample/ai/AiMessageInputHandlerTest.groovy#L83-L91

The output from the task:
https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/bf8736bc51c6a474674253b96b383935fbb00ced/src/test/resources/chatClient-happy-response.json#L1-L13

The output is stored in `processedInput` variable. The output stores processes to start and email to answer which is 
used later in the process.

For more info check (AiMessageInputHandlerTest)[https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/bf8736bc51c6a474674253b96b383935fbb00ced/src/test/groovy/org/crp/flowable/springboot/sample/ai/AiMessageInputHandlerTest.groovy#L81]