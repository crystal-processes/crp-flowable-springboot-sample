# Initial Spring Boot and Flowable Setup

This guide walks you through creating a basic Spring Boot application with Flowable BPMN support.

## What Was Done

Followed the [Flowable Spring Boot documentation](https://www.flowable.com/open-source/docs/bpmn/ch05a-Spring-Boot) to set up the project.

**Note:** This project uses `flowable-spring-boot-starter-rest` instead of the basic `flowable-spring-boot-starter` to include REST API support.

## Building the Project

```shell
./mvnw clean install
```

## Running the Application

### Prerequisites
- Docker Desktop or equivalent (e.g., [Docker Desktop](https://www.docker.com/products/docker-desktop/))

### Start the Database
```shell
# Start PostgreSQL server
docker-compose -f docker-compose/postgres-docker-compose.yml up
```

### Start the Application
```shell
java -jar target/crp-flowable-springboot-sample-*.jar
```

## Verifying the Setup

Once the application is running, verify it works correctly by checking these endpoints:

| Endpoint | Expected Output | Description |
|----------|-----------------|-------------|
| [`/idm-api/groups`](http://localhost:8080/idm-api/groups) | Security role data | Verifies identity management is working |
| [`/process-api/repository/process-definitions`](http://localhost:8080/process-api/repository/process-definitions) | Empty process definitions array | Verifies process engine is initialized |
