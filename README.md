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







