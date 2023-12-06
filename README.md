After 15+ years in BPM and 10+ years with jBPM, Activiti and Flowable, I decided to write a sample application. The goal
is to provide a platform to share knowledge, best practices.

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

# [Initial spring boot and flowable application](docs/01-initialApp.md)
# [Hello World application model](docs/02-helloWorld.md)