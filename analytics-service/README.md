# Analytics Service

Analytics will be based on PatientEvent

But why proto file ? 

All microservices should be generated from proto file, which can also be consumed by analytics 

Remember that every services should have generated protofile, not just the protofile itself

Convert message that we consume (value : byteArray) into Java object, to work and manage


There are many built-in library in Java :
- Service
    - Automatically register class as springbean (?)
- Controller 

```java
PatientEvent patientEvent = PatientEvent.parseFrom(message);
//error : invalid exception

// solution : conv into try catch, because maybe it wont be compatible with patient event java class

// also remember that throw exception can make all of the services go down
```

## Debugging

consumer-offsets on kafka error :
INVALID_REPLICATION_FACTOR

defaultnya 3, tapi cuma ada 1 broker

docker-compose.yml
```
- KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1
- KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1
- KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1
```