[version: 0.0.2](https://github.com/crystal-processes/crp-flowable-springboot-sample/releases/tag/crp-flowable-springboot-sample-0.0.2)

[tag: crp-flowable-springboot-sample-0.0.2](https://github.com/crystal-processes/crp-flowable-springboot-sample/releases/tag/crp-flowable-springboot-sample-0.0.2)

## What was done
The [acme application model](../../src/main/resources/apps/acme-bar.zip) was created in flowable cloud designer.
![Initial acme application](../images/initial-acme-app.png)
Exported [acme-bar.zip](https://github.com/crystal-processes/crp-flowable-springboot-sample/commit/72b6657490c2740d86d083314ab11941cacf2ba7#diff-f560d6ffb8aab7489c1d92d4076d095a9a5052538e803572e31e6e7d3f18fc53) file was added to `src/main/resources/apps/` directory for automated deployment.
The happy path jUnit test was added in the following [commit](https://github.com/crystal-processes/crp-flowable-springboot-sample/commit/72b6657490c2740d86d083314ab11941cacf2ba7#diff-5c80c6fe57043ccfeb87e4a7eefa23903f8d7a7ab61cf8e6298ab3bc28dbdb2d).

## What to do next
Assertions are not readable. It is unclear, what is real flowable API call and what is assertion. 
If you read [the whole test class](https://github.com/crystal-processes/crp-flowable-springboot-sample/commit/72b6657490c2740d86d083314ab11941cacf2ba7#diff-5c80c6fe57043ccfeb87e4a7eefa23903f8d7a7ab61cf8e6298ab3bc28dbdb2d), it is even worse.

## Flowable fluent assertions
[The benefit is obvious](https://github.com/crystal-processes/crp-flowable-springboot-sample/commit/89fbdf46cea2e2235e8e34dc86ffe960d2584667):

### Before

https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/f33a00ac53aa97136cd978326b938748cf1d3ec1/src/test/java/org/crp/flowable/springboot/sample/model/HelloWorldProcessTest.java#L61-L102

### After with fluent assertions

https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/f33a00ac53aa97136cd978326b938748cf1d3ec1/src/test/java/org/crp/flowable/springboot/sample/model/HelloWorldProcessTest.java#L24-L59

### Groovy tests
The groovy tests can be even more readable:

https://github.com/crystal-processes/crp-flowable-springboot-sample/blob/a104ec735f37200a5eef93aab16142488c3cf82b/src/test/groovy/org/crp/flowable/springboot/sample/HelloWorldProcessGroovyTest.groovy#L19-L54