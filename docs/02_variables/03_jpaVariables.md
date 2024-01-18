### Use case:

Let's take the [same use case](01_serializable.md) with JPA serialization. The task is to change contract structure in the next step. 
The [reporting complexity](02_json.md) was already shown. We can use a luxury of sample application and start from the 
scratch with jpa.

![insurance event process](../images/insuranceEventProcess.png)

The initial contract is:
```java
@Entity
@Table(
        name = "APP_CONTRACT"
)
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class ContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_CONTRACT_SEQ")
    @SequenceGenerator(name="APP_CONTRACT_SEQ", allocationSize = 1)
    private Integer id;

    private String contractId;

    private String account;

    private int maxAmount;
}
```
Liquibase is used for the database schema management. The only necessary think is to allow liquibase in properties and create changelog file. 
```properties
spring.liquibase.enabled=true
spring.liquibase.change-log=config/liquibase/app-change-log.xml
```
Flowable allows us to store Jpa entities as process variables. As you can see in the following groovy test.

https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/bd426076176f76b9130a20df462ebbafe6361f83/src/test/groovy/org/crp/flowable/springboot/sample/variables/InsuranceEventJpaTest.groovy#L47-L61

The flowable stores only a reference variable in the flowable structures. The variable value is stored in the database 
table, which makes reporting much easier. So how will the database table cope with the same change request as before.
The `contract.account` has changed from simple `String` value to complex `Account` structure.

https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/522003613490db5fcccaf979099805e29903ae50/src/main/java/org/crp/flowable/springboot/sample/entities/jpa/AccountEntity.java#L15-L28

The contract is changed accordingly:

https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/522003613490db5fcccaf979099805e29903ae50/src/main/java/org/crp/flowable/springboot/sample/entities/jpa/ContractEntity.java#L21-L23

The change in the data structure requires the change in the process model:

https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/522003613490db5fcccaf979099805e29903ae50/src/main/model/acme/P003-jpaProcessInsuranceEvent.bpmn#L16-L17

The data are migrated in the change log:

https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/522003613490db5fcccaf979099805e29903ae50/src/main/resources/config/liquibase/app-change-log.xml#L19-L48


:warning: **"All" [tests]() work fine. Even our upgrade test from the version 0.2.5 to 0.2.6 works fine:**
```java
    @Test
    void continueInV1InsuranceEventProcessInstance(){
            ProcessInstance processInsuranceEvent=runtimeService.createProcessInstanceQuery()
                .processInstanceName("Insurance event process instance from release 0.2.5")
                .singleResult();
            Task assessEventTask=taskService.createTaskQuery().processInstanceId(processInsuranceEvent.getId()).singleResult();
            taskService.complete(assessEventTask.getId(),Map.of("amount",5));

            assertThat(processInsuranceEvent).doesNotExist()
                .inHistory().isFinished()
                .hasVariableWithValue("amount",5);
    }
```
**The problem is**, that money was not sent to the right account. The expectation is to send to account `1234567` amount of `5`.
```java
   Mockito.verify(moneyService, times(1)).sendMoney("1234567", 5);
```
The assertion throws a following exception:
```shell
[ERROR] Failures:
[ERROR]   ContinueInJpaInsuranceEventProcessTest.continueInV1InsuranceEventProcessInstance:49
Argument(s) are different! Wanted:
moneyService bean.sendMoney(
    "1234567",
    5
);
-> at org.crp.flowable.springboot.sample.upgrade.ContinueInJpaInsuranceEventProcessTest.continueInV1InsuranceEventProcessInstance(ContinueInJpaInsuranceEventProcessTest.java:49)
Actual invocations have different arguments at position [0]:
moneyService bean.sendMoney(
    "org.crp.flowable.springboot.sample.entities.jpa.AccountEntity@7b9c6e78",
    5
);
```
The amount is sent to `toString()` output of `AccountEntity@7b9c6e78` instance instead of account number `1234567`.
