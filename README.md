# Microservices with Java


Re-engineering microservices with VSCode setup (cos it was build on IntelijIDEA at first)

## VSCode 
### How to setup project

Requirement (Extensions):
- Spring Boot Extension Pack 
- Java Extension Pack.

### Start Comamnd
Command : Spring initialzr : Create a (maven/gradle/etc) project 

setup :
- springboot -v
- group 
- artifact
- package name
- depedencies

### Generate selected code
Select code -> Source Action -> Generate ...

- Application Properties ?
    - Auto update with jpa.hibernate.ddl-auto 


Currently hard_coded properties  

Checking :
```
localhost:{PORT}/h2-console
```

### Run application
Find entry or main point (in my case, it's on PatientServiceApplication.java)
Characteristic :
- Have `public static void main(String[] args)` 

There will be run|debug options

### In Memory DB

Replace :
- JDBC URL with `jdbc:h2:mem:testdb`
- usn with {YOUR_USN_applicaton.properties}
- pass with {YOUR_PASS_applicaton.properties}





### Repository
```java
//  JpaRepo<{model_name}, {primary key}>
interface PatientRepository extends JpaRepository<Patient, UUID> 
```


## Docker 
Outline :
```python
# base image + step
FROM ... AS builder 
# workdir
# build

# another steps (let's say until final)
# port
# entrypoint
```

Steps : Sequence of instructions

why use steps when we can build sequentially ?

Run command in makefile, Some options

- make clean
- make build
- make up
- make down

## GRPC
Service :
- List of Method (Interface ?)

message :
- DTO

Why message use unique value ?

To Help protofile recognize the variabel ?

e.g. BillingRequest for PatientName is 1, but BillingResponse for PatientName is 1

protofile = blueprint 

### Generate GRPC Protobuf Code 

IntelijIDEA :
Maven -> Lifecycle -> Compile

Muncul target folder

VSCode :
Go into parent service folder, on java project -> Right Click -> Maven -> Run Maven Commands -> Compile

#### Error Handling

If countered error, check on logs, usually wrong folder placement

---

Need to start both Billing and GRPC Services (They are separated) ..

Add GPRC to springboot lifecycle with GrpcService Package

### Stream Observer

Receive a grpc class and can send multiple responses. can also do back and forth communication with the client, really useful for real-time connection (unlike rest which single request and single response)


### Test GRPC Server
Many services (include grpc) can be defined on `application.properties`.  

```
Then run the project => make request, e.g. on grpc-requests => see results

GRPC {server-address}/{service_name (in proto)}/{rpc_method (in proto)}
```

send request with `httpyac` for GRPC

> Better Option for proto files, are to put on global folder, so every services can access / "consume" it centrally, and no need to do redundant changes  



## Kafka
gRPC - REST synchronous (?) (afaik gRPC can do bidirectional streaming and async with pub-sub)

But for 1 - many ?

Kafka, async communication 

Publish the event, e.g. CreatePatient on Patient Service => Put on Announcement (Kafka Topic) => Another service e.g. Analytics can consume it separatedly, no need to blocking wait

Kafka Broker
- Server, store and deliver message to Consumer
    - Topic, Categorized channel of messages
        - Hold different events that match certain category
    - Event, data/message that stored on a topic

Below is lived on each services :
- Producer, send events into spesific kafka Topic 
- Consumer, read events from (at least 1) kafka Topic 








