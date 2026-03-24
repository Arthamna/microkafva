package com.pm.patientservice.grpc;


import org.checkerframework.checker.units.qual.s;
import org.hibernate.engine.spi.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Service
public class BillingServiceGrpcClient {
    // blocking = snyc to grpc server, wait for response
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class); 

    public BillingServiceGrpcClient(
        @Value("${billing.service.address:localhost}") String serverAddress,
        @Value("${billing.service.grpc.port:9001}") int serverPort
    ) {
        log.info("Connecting to billing service at " + serverAddress + ":" + serverPort);

        // init blocking stub, local dev
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();

        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    // create ; blocking
    public BillingResponse createBillingAccount(BillingRequest billingRequest) {

        BillingResponse response = blockingStub.createBillingAccount(billingRequest);
        
        log.info("createBillingAccount: response received : " + response.toString());
        return response;
    }
}
