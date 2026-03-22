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



