# AWS
Localstack - Develop AWS system locally with docker. The codes are also compatible with AWS.


## Bare Essentials

To run our services in Cloud :
- VPC (Virtual Private Cloud)
    - AWS isolate resources inside VPC
    - VPC defines how Elastic Container Services (ECS) communicate with other services (RDS,ALB,S3,etc).
    - In short, how containers communicate inside this VPC

- ECS (Elastic Container Services)
    - orchestration services that managed docker container on AWS (starts and runs all microservices containers), like Kubernetes
    - ECS Cluster : Group of ECS Services and Tasks within VPC 
    - ECS Service : Managing ECS task and integrate with load balancer 
    - ECS Task : Container, running instance of it. Services manages task, task manages container
    - ECS Task Definition : Blueprint that defines how container should run , like dockerfile or dockercompose

- ALB (Application Load Balancer)
    - Routes external traffic to ECS Services in VPC, like API Gateway
    - It sits in public subnet (accesible in internet) and forward requests to ECS services in private subnets
    -

Flow 
```
VPC => ECS Cluster =>
```

## Infrastructure 
Some infra like RDS and MSK is maintain by AWS. Basically, dont worry about backup and depedency, as they will be scheduled by AWS. Well also means there will be costs.

ECS Task can somehow go down (stops for some reason), we also need ECS service to handle this (start new container)

There are possibilities when connection were always changed, so our load balancer can't correctly forward request. API Gateway is one of the solution. It operates in internal Cluster, have internal port and router, so we can just forward request to it  


### So Complex ?

Why hassle so much, when there are another easier option to deploy our app ?

- Full Control to :
    - resources
    - recovery and disaster management
    - security rules



## LocalStack
Requirements :
- Docker

Getting started :

- Pull localstack images from Docker Desktop 

```
docker pull localstack/localstack:latest
```

- Sign in on localstack web UI and Get auth token 


- Setup on localstack desktop

- Go back to localstack web, should be green dot instances if the services is running
https://app.localstack.cloud/inst/default/resources
 

Configuration so every aws cli command is run on localstack

do :
```sh
# configure aws cli
aws configure

# test localstack endpoint (localstack needs to be run) 
aws --endpoint-url=http://localhost:4566 lambda list-functions
# should return json format with empty array of functions


```

## Infrastructure as Code

Make the infra written in programming language.

In this case, write infra (ECS, MSK. RDS) in java. 

Flow :
```
Java => CloudFoundationTemplate (.yml, or other extension config file) => Create env in AWS/localstack based on config file
```

Terms : (Stack = Infra)


