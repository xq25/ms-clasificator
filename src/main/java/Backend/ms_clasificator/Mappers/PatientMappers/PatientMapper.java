package Backend.ms_clasificator.Mappers.PatientMappers;

import Backend.ms_clasificator.DTOs.Patient.PatientCreateDTO;
import Backend.ms_clasificator.DTOs.Patient.PatientResponseDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper implements Mapper<Patient, PatientCreateDTO, PatientResponseDTO> {
    @Override
    public Patient toEntity(PatientCreateDTO patientCreateDTO) {
        if (patientCreateDTO == null) {
            return null;
        }

        return Patient.builder()
                .document(patientCreateDTO.getDocument())
                .years(patientCreateDTO.getYears())
                .userId(patientCreateDTO.getUserId())
                .build();
    }

    @Override
    public PatientCreateDTO toDTO(Patient patient) {
        if (patient == null) {
            return null;
        }

        return PatientCreateDTO.builder()
                .document(patient.getDocument())
                .years(patient.getYears())
                .userId(patient.getUserId())
                .build();
    }
    @Override
    public PatientResponseDTO toResponseDTO(Patient patient){
        if (patient == null){
            return null;
        }
        return PatientResponseDTO.builder()
                .id(patient.getId())
                .document(patient.getDocument())
                .userId(patient.getUserId())
                .userName("") // Lo agregamos en el service. ya que depende de la relacion con el securityService
                .build();
    }
}

