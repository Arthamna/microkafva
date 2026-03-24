package com.pm.patientservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;

import billing.BillingRequest;

@Service
public class PatientService {

    private PatientRepository patientRepository;
    private BillingServiceGrpcClient billingServiceGrpcClient;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();

        List<PatientResponseDTO> patientRepsonseDTOs = patients.stream().map(PatientMapper::toDTO).toList();

        return patientRepsonseDTOs;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Patient with email " + patientRequestDTO.getEmail() + " already exists");
        }        

        Patient newpatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        BillingRequest request = BillingRequest.newBuilder()
            .setPatientId(newpatient.getId().toString())
            .setName(newpatient.getName())
            .setEmail(newpatient.getEmail())
            .build();

        billingServiceGrpcClient.createBillingAccount(request);
        
        return PatientMapper.toDTO(newpatient);
    }  

    public PatientResponseDTO updatePatient(UUID id,PatientRequestDTO patientRequestDTO) {

        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " not found"));

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Patient with email " + patientRequestDTO.getEmail() + " already exists");
        }        

        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        // patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()));
        
        Patient updatedPatient = patientRepository.save(patient);
        
        return PatientMapper.toDTO(updatedPatient);
    }  

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);   
    }

    


}
