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
To demonstrate cost of maintenance, I created a project to support tests between versions [crp-sample-upgrade](https://github.com/crystal-processes/crp-sample-upgrade-test).
Each module depends on the different `crp-flowable-springboot-sample` version. The test usually works in the following steps:
- Generate data on the "current" application version and store the status in the DB,
- Start the new application version and run the tests.

Detailed description: [HowTo](https://github.com/crystal-processes/crp-sample-upgrade-test?tab=readme-ov-file#prerequisites). 

## :partly_sunny: Variables with java serialization
I do not expect somebody use java serialization for variables. [Why not example](docs/02_variables/01_serializable.md)

## :partly_sunny: Json variables
Json variables are commonly used. The variable structure and operations on the variable instance are tighten together 
in the process model. If the operation on structure is outside the process model, 
[problem is similar](docs/02_variables/02_json.md).