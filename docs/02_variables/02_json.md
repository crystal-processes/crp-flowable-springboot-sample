### Use case:

Let's take the [same use case](01_serializable.md). The `contract.account` has changed from simple `String` value to complex structure.

![insurance event process](../images/insuranceEventProcess.png)

The difference is only in the `contract` variable instantiated in `Get Contract` service task.
Initial contract structure in `v0.2.2`:
```json
{
  "account" : "account-id",
  "maxAmount": 10000
}
```
`Send money` service task:
```xml
<serviceTask id="sendMoneyServiceTask" name="Send money" flowable:expression="${moneyService.sendMoney(contract.account, amount)}"/>
```

Is changed to `v0.2.3`:
```json
{
  "account" : { "owner" : "jlong", "id":"account-id" },
  "maxAmount": 10000
}
```
`Send money` service task uses following expression:
```xml
<serviceTask id="sendMoneyServiceTask" name="Send money" flowable:expression="${moneyService.sendMoney(contract.account.id, amount)}"/>
```
The tests works fine in:
[versioning upgrade project](https://github.com/crystal-processes/crp-sample-upgrade-test)
```shell
 echo '--------------------------------------------------------------'
 echo '--- Testing upgrade from release-0.2.2 -> release-0.2.3    ---'
 echo '--- json serialization                                     ---'
# generate v 0.2.2 data
  ./mvnw --projects release-0.2.2 clean test -Dspring.profiles.active=generateData
 # test process execution on the version 0.2.0
  ./mvnw --projects release-0.2.3 clean test
```

and for the latest application version too:
https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/2fe6220b5d53497d5fb7ab43414da780bb532e98/src/test/java/org/crp/flowable/springboot/sample/insurance/InsuranceEventTest.java#L30-L48

### :warning: Problem
If the operation on structure is outside the process model, we are in the similar trouble as in 
[java serialization](01_serializable.md#warning-problem).
**Example:**
Create a report with

| account (accountId) | Amount sent |
|---------------------|-------------|

