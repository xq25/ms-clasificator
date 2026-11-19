package backend.msclasificator.patient.api.mapper;

import backend.msclasificator.patient.api.dto.PatientCreateDTO;
import backend.msclasificator.patient.api.dto.PatientResponseDTO;
import backend.msclasificator.patient.api.dto.PatientSummaryDTO;
import backend.msclasificator.shared.mapping.Mapper;
import backend.msclasificator.patient.domain.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper implements Mapper<Patient, PatientCreateDTO, PatientResponseDTO, PatientSummaryDTO> {

    @Override
    public Patient toEntity(PatientCreateDTO patientCreateDTO) {
        if (patientCreateDTO == null) {
            return null;
        }

        return Patient.builder()
                .document(patientCreateDTO.getDocument())
                .dob(patientCreateDTO.getDob())
                .sex(patientCreateDTO.getSex())
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
                .dob(patient.getDob())
                .sex(patient.getSex())
                .userId(patient.getUserId())
                .build();
    }

    @Override
    public PatientResponseDTO toResponseDTO(Patient patient) {
        if (patient == null) {
            return null;
        }

        return PatientResponseDTO.builder()
                .id(patient.getId())
                .document(patient.getDocument())
                .dob(patient.getDob())
                .sex(patient.getSex())
                .userId(patient.getUserId())
                .userInfo(null)
                .build();
    }

    @Override
    public PatientSummaryDTO toSummaryDTO(Patient patient) {
        if (patient == null) {
            return null;
        }

        return PatientSummaryDTO.builder()
                .id(patient.getId())
                .document(patient.getDocument())
                .dob(patient.getDob())
                .sex(patient.getSex())
                .userId(patient.getUserId())
                .build();
    }
}