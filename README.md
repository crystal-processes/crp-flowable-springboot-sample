After 15+ years in BPM and 10+ years with jBPM, Activiti and Flowable, I decided to write a sample application.

# What was done
Follow the first steps in [Spring Boot](https://www.flowable.com/open-source/docs/bpmn/ch05a-Spring-Boot) flowable 
opensource documentation. There are several videos related to the same topic. Instead of `flowable-spring-boot-starter`, 
`flowable-spring-boot-starter-rest` is used.

# How to build
```shell
./mvnw clean install
```

# How to run
```shell
java -jar target/crp-flowable-springboot-sample-0.0.1-SNAPSHOT.jar
```

# How to check

| link                                                                                                            | expected output                                                                                                                    |
|-----------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| [/idm-api/groups](http://localhost:8080/idm-api/groups)                                                         | `{"data":[{"id":"user","url":null,"name":"users","type":"security-role"}],"total":1,"start":0,"sort":"id","order":"asc","size":1}` |
| [/process-api/repository/process-definitions](http://localhost:8080/process-api/repository/process-definitions) | `{"data":[],"total":0,"start":0,"sort":"name","order":"asc","size":0}`                                                             |