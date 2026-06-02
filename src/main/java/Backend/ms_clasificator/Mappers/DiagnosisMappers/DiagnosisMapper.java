package Backend.ms_clasificator.Mappers.DiagnosisMappers;

import Backend.ms_clasificator.DTOs.Diagnosis.DiagnosisCreateDTO;
import Backend.ms_clasificator.DTOs.Diagnosis.DiagnosisResponseDTO;
import Backend.ms_clasificator.Mappers.Mapper;
import Backend.ms_clasificator.Models.Diagnosis;
import org.springframework.stereotype.Component;

@Component
public class DiagnosisMapper implements Mapper<Diagnosis, DiagnosisCreateDTO, DiagnosisResponseDTO> {

    @Override
    public Diagnosis toEntity(DiagnosisCreateDTO dto) {
        if (dto == null) return null;
        return Diagnosis.builder()
                .build();
    }

    @Override
    public DiagnosisCreateDTO toDTO(Diagnosis entity) {
        if (entity == null) return null;
        return DiagnosisCreateDTO.builder()
                .clinicalRecordId(entity.getClinicalRecord() != null ? entity.getClinicalRecord().getId() : null)
                .medicalDiagnosticId(entity.getMedicalDiagnostic() != null ? entity.getMedicalDiagnostic().getId() : null)
                .build();
    }

    @Override
    public DiagnosisResponseDTO toResponseDTO(Diagnosis entity) {
        if (entity == null) return null;
        return DiagnosisResponseDTO.builder()
                .id(entity.getId())
                .medicalDiagnostic(entity.getMedicalDiagnostic())
                .build();
    }
}

