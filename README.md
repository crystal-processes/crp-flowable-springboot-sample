<!-- TOC -->
* [Goal:](#goal)
* [Step 01 - Basic spring boot and model application](#step-01---basic-spring-boot-and-model-application)
* [Step 02 - Variables](#step-02---variables)
<!-- TOC -->

# Goal:
The sample application should show how "easy" you can do something. The purpose of this sample application is to show,
how difficult is to solve problems caused by misleading samples. I do not have a name for this kind of sample. 
Let's name it fix-sample application.

Even my 15+ years in BPM and 10+ years with jBPM, Activiti and Flowable, is not enough to find and cover everything. 
I would like to encourage anybody, who has a problem to [write an issue to the project](https://github.com/crystal-processes/crp-flowable-springboot-sample/issues), or even better to contribute 

# Step 01 - Basic spring boot and model application
The first step is dedicated to an ordinary sample application. 
## :sunny: [Initial spring boot and flowable application](docs/01_sample/01-initialApp.md)
No problem till now, just follow the doc. (flowable, spring boot...)
## :partly_sunny: [Hello World application model](docs/01_sample/02-helloWorld.md)
How to test model with assertions?
## :partly_sunny: [Integration with remote designer](docs/01_sample/03-designer.md)
Automate model downloading in maven build.

# Step 02 - Variables
Flowable allows us to create any variable of almost any type. The projects use variables to store application data. 
The cost of creating a variable is almost none, the cost of maintenance is tremendous. 
## :sunny: Support tests between versions
To demonstrate cost of maintenance, I created a project to support tests between versions [crp-sample-upgrade](https://github.com/crystal-processes/crp-sample-upgrade-test), 
[HowTo](https://github.com/crystal-processes/crp-sample-upgrade-test?tab=readme-ov-file#prerequisites). 
Each module depends on the different `crp-flowable-springboot-sample` version. The test usually works in the following steps:
- Generate data on the "current" application version and store the status in the DB,
- Start the new application version and run the tests.

The script to run the tests:
https://github.com/crystal-processes/crp-sample-upgrade-test/blob/689fa62f31561b3e011680add6cd2da72d9b4138/run_test.sh#L4-L13

Generate data for release 0.1.0:
https://github.com/crystal-processes/crp-sample-upgrade-test/blob/689fa62f31561b3e011680add6cd2da72d9b4138/release-0.1.0/src/test/java/org/crp/flowable/springboot/sample/upgrade/GenerateDataForVersion1Test.java#L10-L22

The tests on the process instance from version 0.1.0 performed on the version 0.2.0.
https://github.com/crystal-processes/crp-sample-upgrade-test/blob/689fa62f31561b3e011680add6cd2da72d9b4138/release-0.2.0/src/test/java/org/crp/flowable/springboot/sample/upgrade/TestHelloWorldFromV1.java#L23-L41