# Patient Service

Pattern :
Controller - Service - Repository


Domain constructing in Java :

>[!NOTE] specs + field, space separated 

Example :

```java
@Entity
public class Patient {
    // specs
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // field
    private UUID id;

    // space
    @NotNull
    private String name;
}
```

```java
// performs validation on given parameters / variabel
@Valid @RequestBody PatientRequestDTO patientRequestDTO
```

## Swagger
Much easier then in go, maybe (?) 
But go have embedded UI

This one, we need swagger console/ui

Build Depedencies
```
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

### Implement
At least use 2 things :
- Tag
- Operation

To check docs :
```
{server}:{port}/v3/api-docs
// example
localhost:4000/v3/api-docs
```

Use editor to get the UI


## Kafka 
Kafka producer class
=> send event to given topic

Define event template messages

Based on Protofile that describe what are the event in 

Event interchangeable with messages

### Rebuild protos 
Use clean, then compile (refresh start)

#### Check Kafka Messages

Kafka => Topic => Messages

Consumer and producer are programmatically created




