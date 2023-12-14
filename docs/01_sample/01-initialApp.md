## What was done
Follow the first steps in [Spring Boot flowable opensource documentation](https://www.flowable.com/open-source/docs/bpmn/ch05a-Spring-Boot). There are several videos related to the same topic. Instead of `flowable-spring-boot-starter`,
`flowable-spring-boot-starter-rest` is used.

## How to build

```shell
./mvnw clean install
```

## How to run
Pre-requisites:
- running docker desktop e.g.[docker-desktop](https://www.docker.com/products/docker-desktop/)
```shell
# start postgresql server 
docker-compose -f docker-compose/postgres-docker-compose.yml up
# start acme application
java -jar target/crp-flowable-springboot-sample-0.1.0.jar
```

## How to check

| link                                                                                                            | expected output                                                                                                                    |
|-----------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| [/idm-api/groups](http://localhost:8080/idm-api/groups)                                                         | `{"data":[{"id":"user","url":null,"name":"users","type":"security-role"}],"total":1,"start":0,"sort":"id","order":"asc","size":1}` |
| [/process-api/repository/process-definitions](http://localhost:8080/process-api/repository/process-definitions) | `{"data":[],"total":0,"start":0,"sort":"name","order":"asc","size":0}`                                                             |