package com.pm.stack;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.appstream.model.Image;

import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.CloudMapNamespaceOptions;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerDefinitionOptions;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.FargateService;
import software.amazon.awscdk.services.ecs.FargateTaskDefinition;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.PortMapping;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;
import software.amazon.awscdk.services.route53.CfnHealthCheck;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Token;

public class LocalStack extends Stack {

    private final Vpc vpc;
    private final Cluster ecsCluster;
    
    // boilerplate constructor
    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);
        
        this.vpc = createVpc();

        DatabaseInstance authServiceDb = createDatabase("authService", "auth-service-db");
        DatabaseInstance patientServiceDb = createDatabase("patientService", "patient-service-db");
        
        CfnHealthCheck authDBHealthCheck  = createHealthCheck(authServiceDb, "authServiceDBHealthCheck");
        CfnHealthCheck patientDBHealthCheck = createHealthCheck(patientServiceDb, "patientServiceDBHealthCheck");
        
        CfnCluster mskCluster = createMskCluster();

        this.ecsCluster = createEcsCluster();

        // create ecs task for each services
        FargateService authService =
            createFargateService("AuthService",
                "auth-service",
                List.of(4005),
                authServiceDb,
                Map.of("JWT_SECRET", "sRAVkg4zg7Nb9vn4Pg/HhGKKDRXTTOOKn93ZNJIfSmw="));
        // auth service depedencies (ofc needed)
        authService.getNode().addDependency(authDBHealthCheck);
        authService.getNode().addDependency(authServiceDb);

        FargateService billingService =
        createFargateService("BillingService",
            "billing-service",
            List.of(4001,9001),
            null,
            null);

        FargateService analyticsService =
            createFargateService("AnalyticsService",
                "analytics-service",
                List.of(4002),
                null,
                null);

        analyticsService.getNode().addDependency(mskCluster);


        FargateService patientService = createFargateService("PatientService",
            "patient-service",
            List.of(4000),
            patientServiceDb,
            Map.of(
                "BILLING_SERVICE_ADDRESS", "host.docker.internal",
                "BILLING_SERVICE_GRPC_PORT", "9001"
            ));

            patientService.getNode().addDependency(patientServiceDb);
            patientService.getNode().addDependency(patientDBHealthCheck);
            patientService.getNode().addDependency(billingService);
            patientService.getNode().addDependency(mskCluster);

            createApiGatewayService();
    }

    // VPC 
    private Vpc createVpc() {
        return Vpc.Builder
            .create(this, "PatientManagementVPC")
            .vpcName("PatientManagementVPC")
            .maxAzs(2) //zones
            .build();
    }

    // RDS
    private DatabaseInstance createDatabase(String id, String dbName){
        return DatabaseInstance.Builder
            .create(this, id)
            .engine(DatabaseInstanceEngine.postgres(
                PostgresInstanceEngineProps.builder()
                    .version(PostgresEngineVersion.VER_16_6) 
                    .build() 
            ))
            .vpc(vpc)
            // storage, cpu, mem, config
            .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
            .allocatedStorage(20)

            .credentials(Credentials.fromGeneratedSecret("admin_user"))
            .databaseName(dbName)
            // when remove stack, normally for dev
            .removalPolicy(RemovalPolicy.DESTROY)
            .build();

    }

    // health-check db
    private CfnHealthCheck createHealthCheck(DatabaseInstance db, String id) {
        return CfnHealthCheck.Builder
            .create(this, id)
            .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                .type("TCP")
                .port(Token.asNumber(db.getDbInstanceEndpointPort()))
                .ipAddress(db.getDbInstanceEndpointAddress())
                // try every 30 seconds, 3 times
                .requestInterval(30)
                .failureThreshold(3)
                .build())
            .build();
    }

    //msk
    private CfnCluster createMskCluster() {
        return CfnCluster.Builder.create(this, "MskCluster")
            .clusterName("kafka-cluster")
            // update kafka version
            .kafkaVersion("3.5.1")
            .numberOfBrokerNodes(2)
            .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                .instanceType("kafka.t3.small")
                .clientSubnets(vpc.getPrivateSubnets().stream()
                    .map(ISubnet::getSubnetId)
                    .collect(Collectors.toList()))
            // distributed kafka based on avability zones (vpc)
                .brokerAzDistribution("DEFAULT")
                .build())
            .build();
    }

    //ecs cluster
    private Cluster createEcsCluster() {
        return Cluster.Builder.create(this, "PatientManagementCluster")
            .vpc(vpc)
            .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
            // for service discovery, microservices can find each other use name/domain
            //e.g. find auth service => auth-service.{domain}
                .name("patient-management.local")
                .build())
            .build();
    }

    // ecs service
    // there are lot of them, commonly use is fargate
    private FargateService createFargateService(String id, 
        String Imagename,
        List<Integer> ports,
        DatabaseInstance db,
        Map<String, String> additionalEnv) {
    
        // task definition
        FargateTaskDefinition taskDefinition =
            FargateTaskDefinition.Builder.create(this, id + "Task")
                .cpu(256)
                .memoryLimitMiB(512)
                .build();

        // container definition, task will pull img from here
        ContainerDefinitionOptions.Builder containerOptions =       
            ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry(Imagename))
                .portMappings(ports.stream()
                    .map(port -> PortMapping.builder()
                        .containerPort(port)
                    // exposed port, for internal services
                        .hostPort(port)
                        .protocol(Protocol.TCP)
                        .build())
                    .toList())
        // store logging config; debugging
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                    .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                        .logGroupName("/ecs/" + Imagename)
                        .removalPolicy(RemovalPolicy.DESTROY)
                        .retention(RetentionDays.ONE_DAY)
                        .build())
                    .streamPrefix(Imagename)   
                .build()));
            
        // env setup
        Map<String, String> envVars = new HashMap<>();
        envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS", "localhost.localstack.cloud:4510, localhost.localstack.cloud:4511, localhost.localstack.cloud:4512");

        if (additionalEnv != null) {
            envVars.putAll(additionalEnv);
        }

        if(db != null){
            envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db".formatted(
                db.getDbInstanceEndpointAddress(),
                db.getDbInstanceEndpointPort(),
                Imagename
            ));
        // currently hardcoded    
            envVars.put("SPRING_DATASOURCE_USERNAME", "admin_user");
            envVars.put("SPRING_DATASOURCE_PASSWORD",
                db.getSecret().secretValueFromJson("password").toString());
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "60000");
          }

          // insert config
          containerOptions.environment(envVars);
          // insert ecs task definition to container -> ecs task
          taskDefinition.addContainer(Imagename + "Container", containerOptions.build());

          return FargateService.Builder.create(this, id)
            .cluster(ecsCluster)
            .taskDefinition(taskDefinition)
            .assignPublicIp(false)
            .serviceName(id)
            .build();
    }


    private void createApiGatewayService(){
        // api-gateway task definition, hardcoded conf
        FargateTaskDefinition taskDefinition =
        FargateTaskDefinition.Builder.create(this, "ApiGatewayTaskDefiniton")
            .cpu(256)
            .memoryLimitMiB(512)
            .build();

        ContainerDefinitionOptions containerOptions =       
        ContainerDefinitionOptions.builder()
            .image(ContainerImage.fromRegistry("api-gateway"))
            .environment(Map.of(
            // look for application-prod.yml
                "SPRING_PROFILES_ACTIVE", "prod",
            // on localstack, for aws will use service discovery
                "AUTH_SERVICE_URL", "http://host.docker.internal:4005"
            ))
            .portMappings(List.of(4004).stream()
                .map(port -> PortMapping.builder()
                    .containerPort(port)
                    .hostPort(port)
                    .protocol(Protocol.TCP)
                    .build())
                .toList())
            .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                .logGroup(LogGroup.Builder.create(this,  "ApiGatewayLogGroup")
                    .logGroupName("/ecs/api-gateway")
                    .removalPolicy(RemovalPolicy.DESTROY)
                    .retention(RetentionDays.ONE_DAY)
                    .build())
                .streamPrefix("api-gateway")   
            .build()))
            .build();
        

        taskDefinition.addContainer("ApiGatewayContainer", containerOptions);
        // that connect with alb
        ApplicationLoadBalancedFargateService apiGateway 
            = ApplicationLoadBalancedFargateService.Builder.create(this, "ApiGatewayService")
            .cluster(ecsCluster)
            .serviceName("api-gateway")
            .taskDefinition(taskDefinition)
            // for many request, need to scale count
            .desiredCount(1)
            .healthCheckGracePeriod(Duration.seconds(60))
            .build();
}

    // run, main method
    public static void main(String[] args) {
        App app = new App(AppProps.builder().outdir("infrastructure/cdk.out").build());

        StackProps props = StackProps.builder()
            .synthesizer(new BootstraplessSynthesizer())
            .build();

        new LocalStack(app, "LocalStack", props);
        
        // start command
        app.synth();
        System.out.println("App synthesizing in progress...");
    }
}
