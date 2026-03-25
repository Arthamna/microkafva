# Billing Service

Mostly about GRPC :

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


