package com.pm.patientservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.pm.patientservice.model.Patient;

import patient.events.PatientEvent;

@Service
public class kafkaProducer {
    
    // message type, key:value
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final Logger log = LoggerFactory.getLogger(kafkaProducer.class);

    public kafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient){
        PatientEvent event = PatientEvent.newBuilder()
            .setPatientId(patient.getId().toString())
            .setName(patient.getName())
            .setEmail(patient.getEmail())
            .setEventType("PATIENT_CREATED") // can be in enum
            .build();

        try {
            // send topic, value
            kafkaTemplate.send("patient", event.toByteArray());
        } catch (Exception e) {
            log.error("error sending PatientCreated event :" + event);
        }
    }


}
