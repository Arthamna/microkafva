# API Gateways

Place to put your routes
Like, route section in go Clean Architecture

The problem with current approach :
- Need to know exact address
    - If the port/service name changes, client also need to update 

- Service has to expose port to internet
    - Scalabitily, add more microservices = client update config 
    - Security, expose for any external trafic

So basically, use a `middleman` that connects the client with services. If we scaling, just register that in `middleman`, no need changes on client

Even better, we can puth authentication and authorization in this `middleman`



## Depedencies Needed :
- Reactive Gateway

(it's already listed on pom.xml btw)

We can do further setup, either with .yml or .properties file extension in src/main/resources/ 


routes :

for specify rules 

- id : name
- uri : internal address on docker

predicates : 

what requests can be sent into our patient service 

path=/api/patients/**

meaning all that start with /api/patients/

strip-prefix=1 <br>
strip all text after first slash of predicate before assign it to the uri

so :
- /api/patients/create

will be removed first slash, and send into patient-service 

- patient-service/patients/create

## Remove port

Remember that we don't want to expose any ports ?
this means remove the binding ports in Docker Compose  


Test :

```
localhost:4004/api-docs/patients
```


## Validation

After creating validation scheme on AuthService <br>
Time to Call validation :
```
Api Gateway => Auth-service
```

### Filter Class
Filter Class on SpringBoot : Custom class that allows to intercept http requests, add logic, then decided to whether continue or cancel requests  

### Specify Rules
on Application.yml


### Debugging
500 Internal Server Error on PatientService : `http://localhost:4004/api/patients`

Tidak ketemu path (?)

Check list :
- Error di api-gateway
- Patient, auth, no error logs
- Get dari exposed port bisa 

Cek baik-baik di error log api-gateway :

Sebelum stack trace error GET /api/patients, ada couldn't find `auth.service` .. Typo










