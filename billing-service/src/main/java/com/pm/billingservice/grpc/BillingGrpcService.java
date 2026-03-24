package com.pm.billingservice.grpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger
    (BillingGrpcService.class);

    @Override
    public void createBillingAccount(BillingRequest billingRequest, StreamObserver<BillingResponse> responseObserver) {

        log.info("createBillingAccount: request received : " + billingRequest.toString());

        //business logic
        
        //dummy response
        BillingResponse response = BillingResponse.newBuilder()
            .setAccountId("12345")
            .setStatus("ACTIVE")
            .build();
        
        // send response from grpc service to client    
        // we can send (many) another response tbh before completed this  
        responseObserver.onNext(response);
        // it's completed, end the cycle    
        responseObserver.onCompleted();
    }
}   
