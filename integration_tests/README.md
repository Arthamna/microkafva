# Testing

Flow to testing :
- What is this new function for ?
- Input / Output
- Steps to do it ?

## Automated Testing and Comparisons

- Unit Testing : Individual Units in isolation

- Integration Testing : Test multiple components working together, e.g. controller, service, repository.

Go : go_test (unit test)

PHP : Php (unit test) 

Does language above has Intergration testing ? Maybe..

### Integration Testing with Java

Tools : [Rest-assured](https://rest-assured.io/)

Integration test use maven without springboot 

Depedencies file : `.pom.xml`
```c
// scope test is for depedencies that will not be packaged in production
<depedency>
    <scope>test</scope>
<depedency>
```

Every org have it's own policy about name test convention

Btw, test can also focus on mistakes or wrong path (e.g. invalid users), not just happy path


## Abstraction of Test

3 Good Steps :
- Arrange (Preparation)
- Act (Execution)
- Assert (Evaluation on Result)

Check notes on Microservices in Go 




