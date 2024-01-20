### The only good variable

Rules to follow if you work with variables (proposal):
1. **The only good variable is non-existing variable.** Think twice if you create any new variable.  
2. **Transient variables.** If you need to have a variable, use transient as much as possible.
3. **Do not store application data in the process/case instance.** Use dataObjects or JPA to store data outside 
the process/case instances. Use services to fetch the data from application.  Keep in mind variable versioning. 
Is versioning managed by flowable or by the application? 


### How to apply rules on our use case
![insurance event process](../images/insuranceEventProcess.png)
The `contractId`, `requestedAmount`, `eventDescription` are process instance inputs. The `contract` process variable is 
instantiated after `GetContract` task. `amount` to send is created after `Assess event` task. 

Process operates on the top of application data. Application data is `Insurance Event` in our case. `Insurance Event` 
has its attributes:
* `contract`, referenced by `contractId`,
* `requestedAmount`,
* `eventDescription`,
* `amount`, filled after `Assess event` user task.

If the process stores the value Jpa variable, the process instance stores a reference to variable. 
:warning: Do not access `Insurance Event` values directly, use services ([Jpa variables use case](03_jpaVariables.md))

![insurance event process without contract](../images/insuranceEventProcess-cancelledContract.png)

The `GetContract` service task before the `Assess event` user task, can be removes completely. The data is fetch before
the user task only to be presented on the form. The approach creates redundant process variables, makes model vulnerable on 
changes in the external structures and requires synchronization. The form should receive the data from the service,
which provides contract data by contractId. (e.g. rest api call). The rules `1` and `3` were applied.

The next refactoring step after the `GetCOntract` task removal, is removal of `amount` variable. It is very easy to keep 
`amount` in the process instance. Flowable as a tool encourages us to do that. The problem is the same as with 
`contract`. The `amount` could be extended in the future with the currency... . The rule `2` application, makes variable
transient. The transient variable is not necessary, if the `amount` as application data is stored outside the process 
instance.

