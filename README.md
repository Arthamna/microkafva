# Microservices with Java

Re-engineering microservices with VSCode setup (cos it was build on IntelijIDEA at first)

## VSCode 
### How to setup project

Requirement (Extensions):
- Spring Boot Extension Pack. 
- Java Extension Pack.

### Start Comamnd
Command : 

`Spring initialzr : Create a (maven/gradle/etc) project` 



## How to Run :
Requirements :
- Localstack
- Docker Desktop
- AWS cli

- LocalStack
Create localstack account and get personal auth token

Configure localstack with auth token, leave other settings to default 

- Install AWS CLI

Configure aws with :
```
aws configure
```

- Create Docker Image 
With docker compose and Makefile command

- Run Localstack :
```
./infrastructure/localstack-deploy.sh
```


Domain should be shown in command:

```
aws --endpoint-url=http://localhost:4566 elbv2 describe-load-balancers \
    --query "LoadBalancers[0].DNSName" --output text
```


### Run Command 
Makefile in each services, Some options :
- make clean
- make build
- make up
- make down


## Error Handling

If encounter any error in running script, maybe because depedencies issue :
```
aws --endpoint-url=http://localhost:4566 cloudformation describe-stack-events --stack-name patient-management --output table
```

Check error on log table


