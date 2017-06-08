# Service Orchestration for BPM
Authors:
* Nick Balkissoon
* Alex Jurcenko

This artifact extends the functionality of the out-of-the-box BPM workflow to asynchronously call external services with smart retry logic, and supporting callbacks.

 To use this in your BPM project, do the following:

#### 1) Build the artifact
```sh
$ mvn clean install
```
#### 2) Include the artifact as a dependency
From Business Central, navigation to "Dependencies: Dependencies List" and add the group, artifact, version of this artifact as a dependency.  Or, add directly to the pom.xml.
```
<dependency>
  <groupId>org.rhc.renewals</groupId>
  <artifactId>svm-renewals</artifactId>
  <version>1.0.2</version>
</dependency>
```
#### 3) Configure AsyncWorkItemHandler in Deployment Descriptor
The deployment descriptor is where we will register new WorkItemHandlers.  We need to register AsyncWorkItemHandler and CompleteServiceWorkItemHandler which will asynchronously execute our service calls and receive our response when we get a callback.

Navigate to "Deployment Descriptor Editor" and add the following entry under "Work Item Handlers"


|     Value      |  Value   |
| ------------- |:-------------|
AsyncService    |   new org.jbpm.executor.impl.wih.AsyncWorkItemHandler(org.jbpm.executor.ExecutorServiceFactory.newExecutorService())|
CompleteService |   new org.rhc.renewals.workitems.CompleteServiceWorkItemHandler()                                                   |


Or add directly to kie-deployment-descriptor.xml
```
<work-item-handler>
    <resolver>mvel</resolver>
    <identifier>new org.rhc.renewals.workitems.CompleteServiceWorkItemHandler()</identifier>
    <parameters/>
    <name>CompleteService</name>
</work-item-handler>
<work-item-handler>
    <resolver>mvel</resolver>
    <identifier>new org.jbpm.executor.impl.wih.AsyncWorkItemHandler(org.jbpm.executor.ExecutorServiceFactory.newExecutorService())</identifier>
    <parameters/>
    <name>AsyncService</name>
</work-item-handler>
```
#### 4) Register AsyncWorkItemHandler for process editing
If you want to be able to access your fancy new WIH from the process editor within Business Central, you will need to register it as a WorkItemDefinition.
```
[
   "name" : "CompleteService",
   "displayName" : "Complete Service",
   "icon" : "defaultservicenodeicon.png"
],
[
   "name" : "AsyncService",
   "displayName" : "Async Service",
   "icon" : "defaultservicenodeicon.png"
]
```
The "name" should correspond to what you registered in the Deployment Descriptor
#### 5) Configure your services in configuration file
#### 6) Add your AsyncWorkItemHandler to your workflow

Data Inputs And Assignments

| Name          | Data Type     | Source|
| ------------- |:-------------:| -----:|
| CommandClass  |   String      | org.rhc.renewals.commands.InvokeServiceCommand |
| Retries       |   Integer     | 3 |
| RetryDelay    |   String      | 5s, 10s, 15s |
| Priority      |   Integer     |   0
| serviceName   |   String      |    some-service-name    |

Data Outputs And Assignments

| Name          | Data Type     | Target|
| ------------- |:-------------:| -----:|
| state  |   org.rhc.renewals.states.ServiceState      | state |

#### 7) Add your signal
Your signal should follow after the AsyncWorkItemHandler with the following config:

Data Outputs And Assignments

| Name          | Data Type     | Target|
| ------------- |:-------------:| -----:|
| lastServiceResponse  |   org.rhc.renewals.common.ServiceResponse      | lastServiceResponse |

#### 8) Add your CompleteServiceWorkItemHandler
This WorkItemHandler takes the response from Signal and applies the result to the state of the process, or handles failures

Data Inputs And Assignments

| Name          | Data Type     | Source|
| ------------- |:-------------:| -----:|
| lastServiceResponse  |   org.rhc.renewals.common.ServiceResponse      | lastServiceResponse |
| data | java.util.Map | data
| state| org.rhc.renewals.states.ServiceState | state

Data Outputs And Assignments

| Name          | Data Type     | Source|
| ------------- |:-------------:| -----:|
| data | java.util.Map | data
| state| org.rhc.renewals.states.ServiceState | state

