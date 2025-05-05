Process the received email in "acme" insurance company.
### Receive an email
There are many possibilities how to read an email and send it to flowable. I choose (spring-integration-mail)[https://docs.spring.io/spring-integration/reference/mail.html].

https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/58f894e01bf73aab6847230d70de3ff61aecbdd5/src/main/java/org/crp/flowable/springboot/sample/AcmeAiConfiguration.java#L19-L39

### Process model
The process model can be really simple:
![process email](P005-aiMessageInputHandler.png)