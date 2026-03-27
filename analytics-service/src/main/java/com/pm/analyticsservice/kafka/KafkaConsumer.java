package com.pm.analyticsservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.protobuf.InvalidProtocolBufferException;

import patient.events.PatientEvent;

@Service
public class KafkaConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    
    // subscribe to topic, based on who is this consumer (id)
    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumeEvent(byte[] message) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(message);
            //perform business logic analytics

            log.info("Received Patient Event: [Patient ID: " + patientEvent.getPatientId() + ", Name: " + patientEvent.getName() + ", Email: " + patientEvent.getEmail());
        } catch (InvalidProtocolBufferException e) {
            log.error("error deserializing event" + e.getMessage());
        }

    }
}
