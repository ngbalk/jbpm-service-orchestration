# Service Orchestration for BPM
Authors:
* Nick Balkissoon

This artifact extends the functionality of the out-of-the-box BPM workflow to asynchronously call external services with smart retry logic, and supporting callbacks.

 To use this in your BPM project, do the following:

#### 1) Build the artifact and run unit tests
```sh
$ mvn clean install
```

In order to run JUnit tests, you should run with JVM parameter.  This will tell our unit test where to load the service config from for local testing.
```sh
-Dorg.bpm.workflow.service.config.location=classpath:/org.bpm.workflow.service.config.yml
```

#### 2) Include the artifact as a dependency
From Business Central, navigation to "Dependencies: Dependencies List" and add the group, artifact, version of this artifact as a dependency.  Or, add directly to the pom.xml.
```
<dependency>
  <groupId>org.bpm.workflow</groupId>
  <artifactId>bpm-services</artifactId>
  <version>1.0.9</version>
</dependency>
```
#### 3) Configure AsyncWorkItemHandler in Deployment Descriptor
The deployment descriptor is where we will register new WorkItemHandlers.  We need to register AsyncWorkItemHandler and CompleteServiceWorkItemHandler which will asynchronously execute our service calls and receive our response when we get a callback.

Navigate to "Deployment Descriptor Editor" and add the following entry under "Work Item Handlers"


|     Value      |  Value   |
| ------------- |:-------------|
|AsyncService    |   new org.jbpm.executor.impl.wih.AsyncWorkItemHandler(org.jbpm.executor.ExecutorServiceFactory.newExecutorService())|
|CompleteService |   new org.bpm.workflow.workitems.CompleteServiceWorkItemHandler()                                                   |


Or add directly to kie-deployment-descriptor.xml
```
<work-item-handler>
    <resolver>mvel</resolver>
    <identifier>new org.bpm.workflow.workitems.CompleteServiceWorkItemHandler()</identifier>
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
import org.drools.core.process.core.datatype.impl.type.StringDataType;
import org.drools.core.process.core.datatype.impl.type.ObjectDataType;
import org.drools.core.process.core.datatype.impl.type.IntegerDataType;

[
    "name" : "AsyncService",
    "displayName" : "Async Service",
    "icon" : "defaultservicenodeicon.png",
    "parameters" : [
        "CommandClass" :  new StringDataType(),
        "Retries" : new IntegerDataType(),
        "RetryDelay" : new StringDataType(),
        "serviceName" : new StringDataType(),
        "callbackSignalName" : new StringDataType(),
        "data" : new ObjectDataType("")
       ],
    "results" : [
        "state" : new ObjectDataType("org.bpm.workflow.states.ServiceState")
        ]
],
[
    "name" : "CompleteService",
    "displayName" : "Complete Service",
    "icon" : "defaultservicenodeicon.png",
    "parameters" : [
        "lastServiceResponse" : new ObjectDataType("org.bpm.workflow.common.ServiceResponse"),
        "state" : new ObjectDataType("org.bpm.workflow.states.ServiceState"),
        "data" : new ObjectDataType("")
       ],
    "results" : [
        "state" : new ObjectDataType("org.bpm.workflow.states.ServiceState"),
        "data" : new ObjectDataType("")
        ]
]
```
The "name" should correspond to what you registered in the Deployment Descriptor
#### 5) Configure your services in configuration file
Services are configured in a configuration file.  You can write a custom parser depending on how you want to load your configuration file.
By default, we are using YAML to configure our services, and have written a custom parser to parse this file.

Our YAML configuration file is in the following format:
```
services:
  - name: serviceA
    url: http://some/url/to/service/A
    timeout: someNumber(millis)
    retries: someInt(i.e. 3)
    username:
    password:
    token: (i.e. JWT token)

  - name: serviceB
    url: http://localhost:3000/serviceB
    timeout: 3000
    retries: 3
    username: johndoe
    password: unicorns1!
```
By default, ServiceRegistry will look for a file named "org.bpm.workflow.service.config.yml" on the classpath.
You can configure ServiceRegistry to look anywhere by setting the property: org.bpm.workflow.service.config.location
```sh
$ bash standalone.sh -Dorg.bpm.workflow.service.config.location=file:///Users/johndoe/files/services_config.yml
```
#### 6) Add your AsyncWorkItemHandler to your workflow

Data Inputs And Assignments

| Name          | Data Type     | Source|
| ------------- |:-------------:| -----:|
| CommandClass  |   String      | org.bpm.workflow.commands.InvokeServiceCommand |
| Retries       |   Integer     | 3 |
| RetryDelay    |   String      | 5s, 10s, 15s |
| serviceName   |   String      |    some-service-name    |
| callbackSignalName   |   String      |    SignalA    |
| data | org.bpm.workflow.models.IncidentData | data |

NOTE: For testing purposes, you can pass a url of your mock service directly to the WIH by prepending your serviceName with '$TEST:'
For example, you could pass a url of your mock worker i.e. $TEST:http://some.ip.address:3000/mock-worker

Data Outputs And Assignments

| Name          | Data Type     | Target|
| ------------- |:-------------:| -----:|
| state  |   org.bpm.workflow.states.ServiceState      | state |

#### 7) Add your signal
Your signal should follow after the AsyncWorkItemHandler with the following config:

Data Outputs And Assignments

| Name          | Data Type     | Target|
| ------------- |:-------------:| -----:|
| lastServiceResponse  |   org.bpm.workflow.common.ServiceResponse      | lastServiceResponse |

#### 8) Add your CompleteServiceWorkItemHandler
This WorkItemHandler takes the response from Signal and applies the result to the state of the process, or handles failures

Data Inputs And Assignments

| Name          | Data Type     | Source|
| ------------- |:-------------:| -----:|
| lastServiceResponse  |   org.bpm.workflow.common.ServiceResponse      | lastServiceResponse |
| state| org.bpm.workflow.states.ServiceState | state|
| data | org.bpm.workflow.models.IncidentData | data |

Data Outputs And Assignments

| Name          | Data Type     | Source|
| ------------- |:-------------:| -----:|
| data | org.bpm.workflow.models.IncidentData | data |
| state| org.bpm.workflow.states.ServiceState | state|


#### 9) Enabling JPA Persistence for Query-able Data
By default, JBPM uses binary serialization for storing Process Variable data.  We want to use JPA to store these as Persistent Entities instead.
We will make changes to the following files to enable this at the project level:

* persistence.xml
* kie-deployment-descriptor.xml

1) Add your serializable classes to persistence.xml.  In this case we are using MappedVariable to correlate a variable to a process instance id, so we have to include a few more classes.
```
<class>org.drools.persistence.jpa.marshaller.MappedVariable</class>
<class>org.drools.persistence.jpa.marshaller.VariableEntity</class>
<class>org.bpm.workflow.models.IncidentData</class>
<class>org.bpm.workflow.models.PaymentData</class>
<class>org.bpm.workflow.models.DomainData</class>
```

2) Change Hibernate Dialect in persistence.xml
```
<property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
```
3) Add the following to kie-deployment-descriptor.xml
```
<marshalling-strategies>
    <marshalling-strategy>
        <resolver>mvel</resolver>
        <identifier>new org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy("bpm:hello-bpm:1.0.40", classLoader)</identifier>
        <parameters/>
    </marshalling-strategy>
</marshalling-strategies>
```

#### 10) How to create a new data model
In order to use new domain specific data models in your process (like IncidentData, PaymentData, etc), you must crate a
new model in this project, inside of org.bpm.workflow.models .   You can use previous models as examples to help you,
but in general you should be aware of a few things:

1) Your model should extend VariableEntity, and implement Serializable, and Copyable

2) Your model should have a unique serializable id field:
```
static final long serialVersionUID = 156474883874702398L; // this should be unique to this class
```

3) Your model should implement a proper 'copy" method that correctly copies relevant business data from another object
of the same type, but should ignore copying the id field.

4) Your model should be properly annotated for serialization / deserialization and persistence.
That includes: annotations contained in
 * org.codehaus.jackson.annotate
 * javax.persistence
 * javax.xml.bind.annotation

5) It is also generally considered best practice to override toString and equals

#### 11) Test mode

For testing purposes, you can pass a url of your mock service directly to the WIH by prepending your 'serviceName' with '$TEST:'
For example, you could pass a url of your mock worker i.e. $TEST:http://some.ip.address:3000/mock-worker
We also can pass a custom token in the optional 'authorization' parameter

Data Inputs And Assignments

| Name          | Data Type     | Source|
| ------------- |:-------------:| -----:|
| CommandClass  |   String      | org.bpm.workflow.commands.InvokeServiceCommand |
| serviceName   |   String      |    $TEST:http://some.ip.address:3000/mock-worker    |
| callbackSignalName   |   String      |    SignalA    |
| data | org.bpm.workflow.models.IncidentData | data |
| authorization | String | yourTokenHere |


